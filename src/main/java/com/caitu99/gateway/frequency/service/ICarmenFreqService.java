package com.caitu99.gateway.frequency.service;

import java.util.List;
import java.util.Set;

/**
 * Created by chenyun on 15/7/30.
 */
public interface ICarmenFreqService {

    /**
     * 返回hash
     */
    String hash1(String zsetkey, String appid, String method, long distance);

    /**
     *
     * @param zsetkey
     * @param ip
     * @param distance
     * @return
     */
    String hash2(String zsetkey, String ip, long distance);


    /**
     * 获取时间早于currentTime的hash集合
     */
    Set<String> getExpireHash(String zsetkey, long currentTime);


    /**
     * 删除时间早于currentTime的hash集合
     */
    long delExpireHash(String zsetkey, long currentTime);

    /**
     * 获取时间早于currentTime的hash集合
     */
    List<Object> getExpireHashAndRemove(String zsetkey, long currentTime);

    /**
     *
     * @param appid
     * @param method
     * @param distance
     * @param seconds--多少秒之后过期
     * @return
     */
    String hash3(String appid, String method, long distance, int seconds);

    /**
     *
     * @param ip
     * @param distance
     * @param seconds--多少秒之后过期
     * @return
     */
    String hash4(String ip, long distance, int seconds);

}
