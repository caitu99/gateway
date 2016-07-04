package com.caitu99.gateway.console;

import com.caitu99.gateway.AppConfig;
import com.caitu99.gateway.utils.AppUtils;
import com.caitu99.gateway.utils.SpringContext;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static com.caitu99.gateway.utils.AppUtils.executeShellCommand;


public class InstanceMonitor {

    private static final String instanceNode = "/console/instance";
    private static Logger logger = LoggerFactory.getLogger(InstanceMonitor.class);
    private static String thisNode;

    private static InstanceMonitor instanceMonitor;

    private static CacheManager cacheManager;

    private InstanceMonitor() throws Exception {

        AppConfig appConfig = SpringContext.getBean(AppConfig.class);

        InetAddress localHost = InetAddress.getLocalHost();
        String hostName = executeShellCommand("hostname");
        //String hostAddress = this.getHostAddress(appConfig.RedisAddress + ":" + appConfig.RedisPort);
        String hostAddress = this.getHostAddress(appConfig.ZookeeperAddress);
        //String hostAddress = localHost.getHostAddress();

        String node = String.format(instanceNode + "/%s(%s)", hostName, hostAddress);
        String lockNode = node + "/lock";

        ZkFramework framework = ZkFramework.getInstance();
        InterProcessMutex lock = new InterProcessMutex(framework.getClient(), lockNode);

        try {

            byte[] bytes = new byte[0];
            if (lock.acquire(120, TimeUnit.SECONDS)) {

                // remove dead nodes
                // and get current active nodes number
                CuratorFramework client = framework.getClient();

                String sequence = readData(client, lockNode);
                int nodeSequence=Integer.valueOf(sequence);

                removeDeadNode(client, node);

                nodeSequence = nodeSequence + 1; // increment

                client.setData()
                        .inBackground()
                        .forPath(lockNode, String.valueOf(nodeSequence).getBytes());

                thisNode = String.format(node + "/%d", nodeSequence);

                /*Stat instanceState = framework.getClient()
                        .checkExists()
                        .forPath(thisNode);

                if(instanceState == null) {
                    // create instance node
                    framework.getClient()
                            .create()
                            .creatingParentsIfNeeded()
                            .withMode(CreateMode.PERSISTENT)
                            .forPath(thisNode);
                }*/
            } else {
                logger.error("cannot get lock when booting");
            }
        } catch (Exception e) {
            logger.error("lock acquire failure", e);
        } finally {
            lock.release();
        }

    }

    public static InstanceMonitor getInstance() {
        try {
            if (instanceMonitor == null) {
                instanceMonitor = new InstanceMonitor();
            }
        } catch (Exception e) {
            logger.error("get monitor instance error", e);
        }
        return instanceMonitor;
    }

    public Map<String, String> readAllInstanceInfo() {

        CuratorFramework client = ZkFramework.getInstance().getClient();
        Map<String, String> instanceMap = new HashMap<>();
        List<String> machineList = null;
        String rootNode = instanceNode;

        try {

            machineList = client.getChildren()
                    .forPath(rootNode);

            for (String machine : machineList) {
                String machineNode = rootNode + "/" + machine;
                List<String> instanceList = client.getChildren()
                        .forPath(machineNode);

                Predicate<String> predicate = (child) -> !child.equals("lock");

                instanceList.stream().filter(predicate).forEach((instance) -> {

                    String childNode = machineNode + "/" + instance;

                    List<String> allCacheNames = cacheManager.getAllCacheNames();

                    if (checkExist(client, childNode)) {

                        StringBuilder sb = new StringBuilder();

                        { // thread node
                            String threadNode = childNode + Constants.THREAD_NODE;

                            String allThreadNode = threadNode + Constants.THREAD_ALL_NODE;
                            sb.append(readData(client, allThreadNode)).append(";");

                            String preThreadNode = threadNode + Constants.THREAD_PRE_NODE;
                            sb.append(readData(client, preThreadNode)).append(";");
                        }

                        { // request node
                            String requestNode = childNode + Constants.REQUEST_NODE;

                            String requestPreNode = requestNode + Constants.REQUEST_PRE_NODE;
                            sb.append(readData(client, requestPreNode)).append(";");

                            String requestHttpAsyncNode = requestNode + Constants.REQUEST_HTTP_ASYNC;
                            sb.append(readData(client, requestHttpAsyncNode)).append(";");
                        }

                        {// cache size
                            String cacheSizeNode=childNode+Constants.LOCAL_CACHE_NODE;
                            for (String cacheName : allCacheNames) {
                                sb.append(readData(client,cacheSizeNode+"/"+cacheName)).append(";");
                            }
                        }

                        instanceMap.put(machine + "/" + instance, sb.toString());
                    } else {// delete dead node
                        try {
                            client.delete()
                                    .deletingChildrenIfNeeded()
                                    .forPath(childNode);
                        } catch (Exception e) {
                            logger.error("delete dead node error", e);
                        }
                    }

                });
            }

        } catch (Exception e) {
            logger.error("read all instances error", e);
        }

        // delete dead instance
        if (machineList != null) {
            for (String machine : machineList) {
                boolean flag = false;
                for (Map.Entry<String, String> entry : instanceMap.entrySet()) {
                    if (entry.getKey().startsWith(machine)) {
                        flag = true;
                    }
                }
                if (!flag) {
                    String machineNode = rootNode + "/" + machine;
                    try {
                        client.delete()
                                .deletingChildrenIfNeeded()
                                .forPath(machineNode);
                    } catch (Exception e) {
                        logger.error("delete machine node error", e);
                    }
                }
            }
        }

        return instanceMap;
    }

    private String readData(CuratorFramework client, String node) {
        String ret = "0";
        try {
            byte[] bytes = client.getData()
                    .forPath(node);
            ret = new String(bytes);
            if (StringUtils.isEmpty(ret)) {
                ret = "0";
            }
        } catch (Exception e) {
            logger.error("get info from {}", node);
        }
        return ret;
    }

    private void removeDeadNode(CuratorFramework client, String node) {
        try {
            List<String> children = client.getChildren()
                    .forPath(node);
            Predicate<String> predicate = (child) -> !child.equals("lock");

            children.stream().filter(predicate).forEach((childNode) -> {
                String childPath = node + "/" + childNode;
                if (!checkExist(client, childPath)) {
                    try {
                        client.delete()
                                .deletingChildrenIfNeeded()
                                .forPath(childPath);
                        logger.info("remove instance node {}", childPath);
                    } catch (Exception e) {
                        logger.error("remove dead node error: " + childPath, e);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("remove dead node error", e);
        }

    }

    private boolean checkExist(CuratorFramework client, String node) {
        try {
            Stat stat = client.checkExists()
                    .forPath(node + "/i");
            if (stat != null) {
                return true;
            }
        } catch (Exception e) {
            logger.error("read node error", e);
        }
        return false;
    }

    private String getHostAddress(String zkAddressAndPort) throws Exception {
        String[] arr = zkAddressAndPort.split(",");
        arr = arr[0].split(":");
        if (arr.length != 2) {
            throw new Exception("incorrect address format");
        }
        int port = Integer.valueOf(arr[1]);
        return AppUtils.getHostAddressByConnection(arr[0], port);
    }

    public void start() {
        cacheManager = CacheManager.getInstance();
        cacheManager.createCacheSizeNode(thisNode);
        cacheManager.start();

        PipelineManager pipelineManager = PipelineManager.getInstance(thisNode);
        pipelineManager.start();
    }

    public void stop() {

    }



}
