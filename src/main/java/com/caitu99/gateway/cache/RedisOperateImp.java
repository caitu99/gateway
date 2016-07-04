package com.caitu99.gateway.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Repository
public class RedisOperateImp implements RedisOperate {

    private static final Logger Logger = LoggerFactory.getLogger(RedisOperateImp.class);

    @Resource
    private RedisPool redispool;

    public void set(String key, String value) {
        Jedis con = redispool.getConnection();
        try {
            con.set(key, value);
        } finally {
            redispool.closeConnection(con);
        }
    }

    public void set(String key, String value, int second) {
        Jedis con = redispool.getConnection();
        con.set(key, value);
        con.expire(key, second);
        redispool.closeConnection(con);
    }

    public void createCountKey(String key, int value) {
        Jedis con = redispool.getConnection();
        con.set(key, new Integer(value).toString());
        redispool.closeConnection(con);
    }

    public void del(String key) {
        Jedis con = redispool.getConnection();
        if (con != null) {
            con.del(key);
        }
        redispool.closeConnection(con);
    }

    public String getStringByKey(String key) {
        Jedis con = redispool.getConnection();
        String res = con.get(key);
        redispool.closeConnection(con);
        return res;
    }

    public void keyDecrement(String key, int step) {
        Jedis con = redispool.getConnection();
        con.decrBy(key, step);
        redispool.closeConnection(con);
    }

    public void keyIncrement(String key, int step) {
        Jedis con = redispool.getConnection();
        con.incrBy(key, step);
        redispool.closeConnection(con);
    }

    public void setAdd(String key, String value) {
        Jedis con = redispool.getConnection();
        con.sadd(key, value);
        redispool.closeConnection(con);
    }

    public void setDel(String key, String value) {
        Jedis con = redispool.getConnection();
        con.srem(key, value);
        redispool.closeConnection(con);
    }

    public Set<String> getSetMember(String key) {
        Jedis con = redispool.getConnection();
        Set<String> set = con.smembers(key);
        redispool.closeConnection(con);
        return set;
    }

    public long getSetSize(String key) {
        Jedis con = redispool.getConnection();
        long size = con.scard(key);
        redispool.closeConnection(con);
        return size;
    }

    public void zsetAdd(String key, long score, String value) {
        Jedis con = redispool.getConnection();
        con.zadd(key, score, value);
        redispool.closeConnection(con);
    }

    public void zsetDel(String key, String value) {
        Jedis con = redispool.getConnection();
        con.zrem(key, value);
        redispool.closeConnection(con);
    }

    public long getZsetSize(String key) {
        Jedis con = redispool.getConnection();
        long size = con.zcard(key);
        redispool.closeConnection(con);
        return size;
    }

    public Set<String> getZsetByscoreByPage(String key, String min, String max,
                                            int offset, int count) {
        Jedis con = redispool.getConnection();
        Set<String> sets = con.zrangeByScore(key, min, max, offset, count);
        redispool.closeConnection(con);
        return sets;
    }

    @Override
    public Set<String> getZsetByScore(String key, String min, String max) {

        Jedis con = redispool.getConnection();
        Set<String> sets = con.zrangeByScore(key, min, max);
        redispool.closeConnection(con);
        return sets;
    }

    @Override
    public long zsetRemrangebyScore(String key, String min, String max) {
        Jedis con = redispool.getConnection();
        long size = con.zremrangeByScore(key, min, max);
        redispool.closeConnection(con);
        return size;
    }

    public void listAdd(String key, String value) {
        Jedis con = redispool.getConnection();
        con.lpush(key, value);
        redispool.closeConnection(con);
    }

    public List<String> getListAll(String key, int start, int stop) {
        Jedis con = redispool.getConnection();
        List<String> list = con.lrange(key, start, stop);
        redispool.closeConnection(con);
        return list;
    }

    public void listRangeDel(String key, int start, int stop) {
        Jedis con = redispool.getConnection();
        con.ltrim(key, start, stop);
        redispool.closeConnection(con);
    }

    @Override
    public long listDelByValue(String key, long count, String value) {
        Jedis con = redispool.getConnection();
        long amount = con.lrem(key, count, value);
        redispool.closeConnection(con);
        return amount;
    }

    @Override
    public String hmset(String key, Map<String, String> map) {
        Jedis con = redispool.getConnection();
        String res = con.hmset(key, map);
        redispool.closeConnection(con);
        return res;
    }

    @Override
    public long hincrby(String key, String field, long step) {
        Jedis con = redispool.getConnection();
        long size = con.hincrBy(key, field, step);
        redispool.closeConnection(con);
        return size;
    }

    @Override
    public String hget(String key, String field) {
        Jedis con = redispool.getConnection();
        String res = con.hget(key, field);
        redispool.closeConnection(con);
        return res;
    }

    @Override
    public Map<String, String> hgetall(String key) {
        Jedis con = redispool.getConnection();
        Map<String, String> res = con.hgetAll(key);
        redispool.closeConnection(con);
        return res;
    }

    @Override
    public List<Object> getZsetByScoreAndRemove(String key, String min, String max) {
        Jedis con = redispool.getConnection();
        Transaction tx = con.multi();
        tx.zrangeByScore(key, min, max);
        tx.zremrangeByScore(key, min, max);
        List<Object> res = tx.exec();
        return res;
    }

    @Override
    public long setExpire(String key, int seconds) {
        Jedis con = redispool.getConnection();
        Long expire = con.expire(key, seconds);
        redispool.closeConnection(con);
        return expire;
    }
}
