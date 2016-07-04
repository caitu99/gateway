package com.caitu99.gateway.console;


import com.caitu99.gateway.gateway.cache.ICacheClear;
import com.caitu99.gateway.gateway.cache.LocalCache;
import com.caitu99.gateway.utils.ClassUtils;
import com.caitu99.gateway.utils.SpringContext;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class CacheManager {

    private static Logger logger = LoggerFactory.getLogger(CacheManager.class);

    private static CacheManager cacheManager=null;

    private String cacheNode = "/cache/api/config";

    private String preCacheNodePrefix="/preCache";

    private Map<String, ICacheClear> cacheMap = new HashMap<>();

    private ZkFramework framework = ZkFramework.getInstance();

    private String localCacheBasePath;

    private Map<String,Long> cacheSizeMap=new HashMap<>();


    private CacheManager() {
        List<Class> allCaches = ClassUtils.getAllClassByInterface(ICacheClear.class);
        for (Class clazz : allCaches) {
            LocalCache localCache = (LocalCache) clazz.getAnnotation(LocalCache.class);
            if (localCache == null) {
                logger.error("cache must have annotation of LocalCache");
            }

            assert localCache != null;
            String name = localCache.value();
            ICacheClear clear = (ICacheClear) SpringContext.getBean(clazz);

            cacheMap.put(name, clear);
            cacheSizeMap.put(name,-1L);
        }

        this.createNodes();
    }

    public static CacheManager getInstance() {
        if (cacheManager==null){
            cacheManager=new CacheManager();
        }
        return cacheManager;
    }

    public  void createCacheSizeNode(String cacheNode){
        this.localCacheBasePath=cacheNode+Constants.LOCAL_CACHE_NODE;
        List<String> allCacheNames = getAllCacheNames();
        for (String name : allCacheNames) {
            String Path=cacheNode+Constants.LOCAL_CACHE_NODE+"/"+name;
            this.createNode(Path);
        }
    }



    public void start() {
        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    CuratorFramework client = framework.getClient();
                    for (Map.Entry<String, ICacheClear> entry : cacheMap
                            .entrySet()) {
                        String key=entry.getKey();
                        ICacheClear cacheClear=entry.getValue();
                        long size=cacheClear.cacheSize();
                        long previous= cacheSizeMap.get(key);
                        if (previous!=size) {
                            cacheSizeMap.put(key,size);
                            client.setData()
                                    .inBackground()
                                    .forPath(localCacheBasePath + "/" + key,
                                             String.valueOf(size).getBytes());
                        }

                    }

                } catch (Exception e) {
                    logger.error("{}",e);
                }
            }
        },0,2*1000);
    }

    public void stop() {

    }

    public void createNodes() {
        try {
            CuratorFramework client = framework.getClient();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            byte[] bytes = formatter.format(Calendar.getInstance().getTime()).getBytes();
            for (Map.Entry<String, ICacheClear> entry : cacheMap.entrySet()) {
                String node = cacheNode + "/" + entry.getKey();
                String preCacheNode=preCacheNodePrefix+"/"+entry.getKey();
                try {
                    client.create()
                            .creatingParentsIfNeeded()
                            .withMode(CreateMode.PERSISTENT)
                            .forPath(node, bytes);
                } catch (KeeperException.NodeExistsException e) {
                    logger.info("node {} exist.", node);
                }
                try {
                    //增加缓存预加载功能
                    client.create()
                            .creatingParentsIfNeeded()
                            .withMode(CreateMode.PERSISTENT)
                            .forPath(preCacheNode, bytes);

                } catch (KeeperException.NodeExistsException e) {
                    logger.info("node {} exist.", preCacheNode);
                }
                client.getData()
                        .usingWatcher(new CacheWatcher(this))
                        .forPath(node);
                //增加缓存预加载功能,设置watch
                client.getData()
                        .usingWatcher(new PreCacheWatcher(this))
                        .forPath(preCacheNode);
            }
        } catch (Exception e) {
            logger.error("create node error", e);
        }
    }

    public Map<String, String> readNodes() {
        CuratorFramework client = framework.getClient();

        Map<String, String> cacheMap = new HashMap<>();

        List<String> pathList = null;

        try {
            pathList = client
                    .getChildren()
                    .forPath(cacheNode);
        } catch (Exception e) {
            logger.error("read cache children node error", e);
        }

        if (pathList != null) {
            for (String name : pathList) {
                byte[] bytes = new byte[0];
                try {
                    String path = cacheNode + "/" + name;
                    bytes = client.getData()
                            .forPath(path);
                } catch (Exception e) {
                    logger.error("read cache node error", e);
                }
                cacheMap.put(name, new String(bytes));
            }
        }

        return cacheMap;
    }

    public void updateNodes(List<String> names) throws Exception {
        CuratorFramework client = framework.getClient();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        byte[] bytes = formatter.format(Calendar.getInstance().getTime()).getBytes();
        for (String name : names) {
            String node = cacheNode + "/" + name;
            client.setData()
                    .forPath(node, bytes);
        }
    }

    public void updatePreNodes(List<String> names) throws Exception{
        CuratorFramework client = framework.getClient();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        byte[] bytes = formatter.format(Calendar.getInstance().getTime()).getBytes();
        for (String name : names) {
            String node = preCacheNodePrefix + "/" + name;
            client.setData()
                    .forPath(node, bytes);
        }
    }

    public List<String> getAllCacheNames() {
        List<String> names = new ArrayList<>();
        for (Map.Entry<String, ICacheClear> entry : cacheMap.entrySet()) {
            names.add(entry.getKey());
        }
        return names;
    }

    public void clearAll() {
        for (Map.Entry<String, ICacheClear> entry : cacheMap.entrySet()) {
            entry.getValue().clear();
        }
    }

    public void clear(String name) {
        ICacheClear iCacheClear = cacheMap.get(name);
        if (iCacheClear != null) {
            iCacheClear.clear();
        }
    }

    //新建节点
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


    private class CacheWatcher implements CuratorWatcher {

        private CacheManager cacheManager;

        public CacheWatcher(CacheManager cacheManager) {
            this.cacheManager = cacheManager;
        }

        @Override
        public void process(WatchedEvent watchedEvent) {
            if (watchedEvent.getType() == Watcher.Event.EventType.NodeDataChanged) {
                String path = watchedEvent.getPath();
                try {
                    String name = path.substring(path.lastIndexOf('/') + 1);

                    this.cacheManager.clear(name);

                    logger.info("cache {} has been cleared.", path);
                } catch (Exception e) {
                    logger.error("clear cache", e);
                }finally {
                    try {
                        framework.getClient()
                                .getData()
                                .usingWatcher(new CacheWatcher(cacheManager))
                                .forPath(path);
                    } catch (Exception e) {
                        logger.error("clear watcher",e);
                    }
                }
            }
        }
    }


    private class PreCacheWatcher implements CuratorWatcher {

        private CacheManager cacheManager;

        public PreCacheWatcher(CacheManager cacheManager) {
            this.cacheManager = cacheManager;
        }

        @Override
        public void process(WatchedEvent watchedEvent) {
            if (watchedEvent.getType() == Watcher.Event.EventType.NodeDataChanged) {
               String path=watchedEvent.getPath();
                try {

                    String name = path.substring(path.lastIndexOf('/') + 1);

                    ICacheClear cache = cacheMap.get(name);

                    cache.load();

                    logger.info("cache {} has been cleared.", path);
                } catch (Exception e) {
                    logger.error("preload cache:{}", e);
                } finally {
                    try {
                        framework.getClient()
                                .getData()
                                .usingWatcher(new PreCacheWatcher(cacheManager))
                                .forPath(path);
                    } catch (Exception e) {
                        logger.error("preload watch:{}",e);
                    }
                }

            }
        }
    }

}
