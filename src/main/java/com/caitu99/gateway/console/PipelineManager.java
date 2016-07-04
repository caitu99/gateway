package com.caitu99.gateway.console;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.gateway.gateway.excutor.Pipeline;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PipelineManager {

    private static Logger logger = LoggerFactory.getLogger(PipelineManager.class);

    private static PipelineManager pipelineManager;

    private ZkFramework framework = ZkFramework.getInstance();

    private Pipeline pipeline;

    private String threadNode;
    private String threadAllNode;
    private String threadPreNode;
    private String threadDubboNode;

    private String requestNode;
    private String requestPreNode;
    private String requestHttpAsyncNode;
    private String requestDubboSyncNode;

    private String iNode;

    private int threadAllCount;
    private int threadPreCount;
    private int threadDubboCount;

    private int requestPreCount;
    private int requestHttpAsyncCount;
    private int requestDubboSyncCount;

    private PipelineManager(String instanceNode) {
        this.threadNode = instanceNode + Constants.THREAD_NODE;
        this.threadAllNode = this.threadNode + Constants.THREAD_ALL_NODE;
        this.threadPreNode = this.threadNode + Constants.THREAD_PRE_NODE;

        this.requestNode = instanceNode + Constants.REQUEST_NODE;
        this.requestPreNode = this.requestNode + Constants.REQUEST_PRE_NODE;
        this.requestHttpAsyncNode = this.requestNode + Constants.REQUEST_HTTP_ASYNC;

        this.iNode = instanceNode + "/i";
        this.createNode(iNode);

        this.createNodes();

        pipeline = Pipeline.getInstance();
    }

    public static PipelineManager getInstance(String instanceNode) {
        if(pipelineManager == null)
            pipelineManager = new PipelineManager(instanceNode);
        return pipelineManager;
    }

    public void start() {
        Timer timer = new Timer();
        CuratorFramework client = framework.getClient();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {

                    int tmp = 0;
                    tmp = Thread.activeCount();
                    if (tmp != threadAllCount) {
                        client.setData()
                                .inBackground()
                                .forPath(threadAllNode, String.valueOf(tmp).getBytes());
                        threadAllCount = tmp;
                    }

                    tmp = pipeline.getPreThreadCount();
                    if (tmp != threadPreCount) {
                        client.setData()
                                .inBackground()
                                .forPath(threadPreNode, String.valueOf(tmp).getBytes());
                        threadPreCount = tmp;
                    }

                    tmp = pipeline.getPreTaskCount();
                    if (tmp != requestPreCount) {
                        client.setData()
                                .inBackground()
                                .forPath(requestPreNode, String.valueOf(tmp).getBytes());
                        requestPreCount = tmp;
                    }

                    tmp = pipeline.getHttpAsyncTask();
                    if (tmp != requestHttpAsyncCount) {
                        client.setData()
                                .inBackground()
                                .forPath(requestHttpAsyncNode, String.valueOf(tmp).getBytes());
                        requestHttpAsyncCount = tmp;
                    }

                } catch (Exception e) {
                    logger.error("update pipe monitor data error", e);
                }
            }
        }, 0, 2 * 1000);
    }

    public String readNodes() throws Exception {
        CuratorFramework client = framework.getClient();
        JSONArray jsonArray = new JSONArray();

        JSONObject thread = new JSONObject();
        jsonArray.add(thread);
        List<String> pathList = client
                .getChildren()
                .forPath(threadNode);
        for (String path : pathList) {
            byte[] bytes = client.getData()
                    .forPath(path);
            String name = path.substring(path.lastIndexOf('/'));
            thread.put(name, new String(bytes));
        }

        JSONObject request = new JSONObject();
        jsonArray.add(request);
        pathList = client.getChildren()
                .forPath(requestNode);
        for (String path : pathList) {
            byte[] bytes = client.getData()
                    .forPath(path);
            String name = path.substring(path.lastIndexOf('/'));
            thread.put(name, new String(bytes));
        }

        return jsonArray.toJSONString();
    }

    public void createNodes() {
        //this.createNode(threadNode);
        this.createNode(threadAllNode, "0");
        this.createNode(threadPreNode, "0");
        this.createNode(threadDubboNode, "0");

        //this.createNode(requestNode);
        this.createNode(requestPreNode, "0");
        this.createNode(requestHttpAsyncNode, "0");
        this.createNode(requestDubboSyncNode, "0");
    }

    public void createNode(String node) {
        try {
            Stat stat = framework.getClient()
                    .checkExists()
                    .forPath(node);

            if (stat == null) {
                framework.getClient()
                        .create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(node);
            }
        } catch (Exception e) {
            logger.error("create node error", e);
        }
    }

    public void createNode(String node, String data) {
        try {
            Stat stat = framework.getClient()
                    .checkExists()
                    .forPath(node);
            if (stat == null) {
                framework.getClient()
                        .create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(node, data.getBytes());
            }
        } catch (Exception e) {
            logger.error("create node error", e);
        }
    }

}
