package com.caitu99.gateway.console;

import com.caitu99.gateway.AppConfig;
import com.caitu99.gateway.utils.SpringContext;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZkFramework {

    private static ZkFramework zkFramework = new ZkFramework();

    private CuratorFramework client;

    private ZkFramework() {
        AppConfig appConfig = SpringContext.getBean(AppConfig.class);
        client = CuratorFrameworkFactory.builder()
                .connectString(appConfig.ZookeeperAddress)
                .retryPolicy(new ExponentialBackoffRetry(1000, 5))
                .connectionTimeoutMs(5000)
                .namespace("carmen")
                .defaultData(null)
                .build();
        client.start();
    }

    public static ZkFramework getInstance() {
        return zkFramework;
    }

    public CuratorFramework getClient() {
        return client;
    }

}
