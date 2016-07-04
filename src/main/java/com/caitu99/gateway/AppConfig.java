package com.caitu99.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppConfig {

    @Value("${application.env}")
    public byte env;

    @Value("${application.name}")
    public String appName;

    @Value("${zookeeper.address}")
    public String ZookeeperAddress;

    @Value("${redis.ip}")
    public String RedisAddress;

    @Value("${redis.port}")
    public String RedisPort;

    @Value("${token.frequency}")
    public int frequency;

    @Value("${token.access}")
    public int tokenAccessExpire;

    @Value("${token.refresh}")
    public int tokenRefreshExpire;

    @Value("${client.credential.scope}")
    public String clientCredentialScope;

    @Value("${allow.access}")
    public String allowAccess;

    public boolean isDevMode() {
        return env == 1;
    }

}
