package com.caitu99.gateway.cache;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import javax.annotation.Resource;


@Repository
public class RedisPool {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(RedisPool.class);

    @Resource
    private JedisPool pool;

    public Jedis getConnection() {
        Jedis con = null;
        try {
            con = pool.getResource();
        } catch (JedisConnectionException e) {
            throw e;
            //Todo 监控
        }
        return con;
    }

    public void closeConnection(Jedis con) {

        try {
            pool.returnResource(con);
        } catch (JedisException e) {
            throw e;
            //TODO 监控
        }
    }
}
