package com.caitu99.gateway.frequency.service.imp;

import com.alibaba.fastjson.JSON;
import com.caitu99.gateway.cache.RedisOperate;
import com.caitu99.gateway.frequency.ExpiredDataType;
import com.caitu99.gateway.frequency.FrequencyType;
import com.caitu99.gateway.frequency.TimeUtil;
import com.caitu99.gateway.frequency.model.CarmenFreqLog1;
import com.caitu99.gateway.frequency.model.CarmenFreqLog2;
import com.caitu99.gateway.frequency.service.ICarmenExpiredQueue;
import com.caitu99.gateway.frequency.service.ICarmenFreqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenyun on 15/8/10.
 */
public class CarmenGetExpiredDataService implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(CarmenGetExpiredDataService.class);

    private ScheduledExecutorService executorService;

    @Resource
    private ICarmenExpiredQueue queue1;

    @Resource
    private ICarmenFreqService freqService;

    @Resource
    private RedisOperate redis;

    private final String zsetKey = "carmen_freq_set";

    private int interval = 10;

    @Override
    public void run() {
        long currentTime = TimeUtil.getTimeDistanceExpired(FrequencyType.min) - 1;
        List<Object> hashRes = freqService.getExpireHashAndRemove(zsetKey, currentTime);
        Set<String> hashSet = (Set<String>) hashRes.get(0);
        long mount = (long) hashRes.get(1);
        logger.error("删除key值数量:{}", mount);
        logger.error("过期时间戳:{}", currentTime);
        for (String hash : hashSet) {
            Map<String, String> context = redis.hgetall(hash);//todo 能否批量处理
            String json = context.get("json");
            String type = context.get("type");
            String count = context.get("count");
            //入队及更改count
            if (type.equalsIgnoreCase("1")) {
                CarmenFreqLog1 log1 = JSON.parseObject(json, CarmenFreqLog1.class);
                log1.setCount(Integer.valueOf(count));
                json = JSON.toJSONString(log1);
                queue1.ExpiredDataEnqueue(json, ExpiredDataType.freqLog1);
            }
            if (type.equalsIgnoreCase("2")) {
                CarmenFreqLog2 log2 = JSON.parseObject(json, CarmenFreqLog2.class);
                log2.setCount(Integer.valueOf(count));
                json = JSON.toJSONString(log2);
                queue1.ExpiredDataEnqueue(json, ExpiredDataType.freqLog2);
            }
            redis.del(hash);//删除zmset
            logger.error("删除的hash:{}", hash);
        }

    }

    public CarmenGetExpiredDataService(int interval) {
        this.interval = interval;
    }

    private void init() {

        executorService = Executors.newScheduledThreadPool(1);

        executorService.scheduleAtFixedRate(this, 0, interval, TimeUnit.SECONDS);

    }
}
