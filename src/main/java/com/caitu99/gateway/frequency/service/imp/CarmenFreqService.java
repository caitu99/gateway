package com.caitu99.gateway.frequency.service.imp;

import com.alibaba.fastjson.JSON;
import com.caitu99.gateway.cache.RedisOperate;
import com.caitu99.gateway.frequency.ExpiredDataType;
import com.caitu99.gateway.frequency.HashUtil;
import com.caitu99.gateway.frequency.model.CarmenFreqLog1;
import com.caitu99.gateway.frequency.model.CarmenFreqLog2;
import com.caitu99.gateway.frequency.service.ICarmenFreqService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chenyun on 15/7/30.
 */
@Service
public class CarmenFreqService implements ICarmenFreqService {

    private static final String MIN = "0";
    @Resource
    private RedisOperate redis;

    @Override
    public String hash1(String zsetkey, String appid, String method, long distance) {
        String key = appid + method + distance;
        String hash = HashUtil.hash(key);
        redis.zsetAdd(zsetkey, distance, hash);
        String count = redis.hget(hash, "count");
        if (count == null) {
            CarmenFreqLog2 log2 = new CarmenFreqLog2();
            log2.setAppId(appid);
            log2.setMethod(method);
            log2.setDistanceFrom(distance);
            log2.setHash(hash);
            Map<String, String> map = new HashMap<>();
            String context = JSON.toJSONString(log2);
            map.put("json", context);
            map.put("count", "0");
            map.put("type", ExpiredDataType.freqLog2.getId());
            redis.hmset(hash, map);
        }
        return hash;
    }


    @Override
    public Set<String> getExpireHash(String zsetkey, long currentTime) {
        Set<String> hashSet = redis.getZsetByScore(zsetkey, MIN, Long.toString(currentTime));
        return hashSet;
    }

    @Override
    public long delExpireHash(String zsetkey, long currentTime) {
        long size = redis.zsetRemrangebyScore(zsetkey, MIN, Long.toString(currentTime));
        return size;
    }

    @Override
    public List<Object> getExpireHashAndRemove(String zsetkey, long currentTime) {
        List<Object> hashRes = redis.getZsetByScoreAndRemove(zsetkey, MIN,
                                                             Long.toString(currentTime));//提前一个周期
        return hashRes;
    }

    @Override
    public String hash2(String zsetkey, String ip, long distance) {
        String key = ip + distance;
        String hash = HashUtil.hash(key);
        redis.zsetAdd(zsetkey, distance, hash);
        String count = redis.hget(hash, "count");
        if (count == null) {
            CarmenFreqLog1 log1 = new CarmenFreqLog1();
            log1.setIp(ip);
            log1.setDistanceFrom(distance);
            log1.setHash(hash);
            Map<String, String> map = new HashMap<>();
            String context = JSON.toJSONString(log1);
            map.put("json", context);
            map.put("count", "0");
            map.put("type", ExpiredDataType.freqLog1.getId());
            redis.hmset(hash, map);
        }
        return hash;
    }

    @Override
    public String hash3(String appid, String method, long distance, int seconds) {
        String key = appid + method + distance;
        String hash = HashUtil.hash(key);
        String count = redis.getStringByKey(hash);
        if (count == null) {
            redis.set(hash, "0", seconds);
        }
        return hash;
    }

    @Override
    public String hash4(String ip, long distance, int seconds) {
        String key = ip + distance;
        String hash = HashUtil.hash(key);
        String count = redis.getStringByKey(hash);
        if (count == null) {
            redis.set(hash, "0", seconds);
        }
        return hash;
    }
}
