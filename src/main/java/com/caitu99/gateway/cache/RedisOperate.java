package com.caitu99.gateway.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface RedisOperate {

    void set(String key, String value);

    void set(String key, String value, int second);

    void createCountKey(String key, int value);

    void del(String key);

    String getStringByKey(String key);

    /**
     * 计数
     */
    void keyDecrement(String key, int step);

    void keyIncrement(String key, int step);

    /**
     * set
     */
    void setAdd(String key, String value);

    void setDel(String key, String value);

    Set<String> getSetMember(String key);

    long getSetSize(String key);

    /**
     * zset
     */
    void zsetAdd(String key, long score, String value);

    void zsetDel(String key, String value);

    long getZsetSize(String key);

    Set<String> getZsetByscoreByPage(String key, String min, String max, int offset, int count);

    Set<String> getZsetByScore(String key, String min, String max);

    long

    zsetRemrangebyScore(String key, String min, String max);

    /**
     * list
     */
    void listAdd(String key, String value);

    List<String> getListAll(String key, int start, int stop);

    void listRangeDel(String key, int start, int stop);

    long listDelByValue(String key, long count, String value);

    /**
     * hash
     */
    String hmset(String key, Map<String, String> map);

    long hincrby(String key, String field, long step);

    String hget(String key, String field);

    Map<String, String> hgetall(String key);

    /**
     * 事务处理
     */
    List<Object> getZsetByScoreAndRemove(String key, String min, String max);

    /**
     * 设置key过期时间
     */
    long setExpire(String key, int seconds);


}
