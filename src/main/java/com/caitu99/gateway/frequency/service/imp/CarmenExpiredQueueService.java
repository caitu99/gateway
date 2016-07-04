package com.caitu99.gateway.frequency.service.imp;

import com.alibaba.fastjson.JSON;
import com.google.code.fqueue.FQueue;
import com.google.common.collect.Maps;
import com.caitu99.gateway.frequency.ExpiredDataType;
import com.caitu99.gateway.frequency.dao.gateway.IFreqLog1DAO;
import com.caitu99.gateway.frequency.dao.gateway.IFreqLog2DAO;
import com.caitu99.gateway.frequency.model.CarmenFreqLog1;
import com.caitu99.gateway.frequency.model.CarmenFreqLog2;
import com.caitu99.gateway.frequency.service.ICarmenExpiredQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenyun on 15/8/5.
 */
public class CarmenExpiredQueueService implements Runnable, ICarmenExpiredQueue {

    private static final Logger logger = LoggerFactory.getLogger(CarmenExpiredQueueService.class);

    private int interval = 10;

    private boolean fileFlag = false;

    /**
     * 类型与文件名称对应
     */
    private Map<ExpiredDataType, String> type2QueueName;
    /**
     * 不同分类信息进入不同queue 文件队列
     */

    private Map<ExpiredDataType, FQueue> type2Queue;

    /**
     * 内存结构的队列
     */
    private ConcurrentLinkedQueue queue;

    /**
     * 定时任务
     */
    private ScheduledExecutorService executorService;


    @Resource
    private IFreqLog1DAO carmenFreqLog1DAO;

    @Resource
    private IFreqLog2DAO carmenFreqLog2DAO;


    public CarmenExpiredQueueService(int interval, boolean fileFlag,
                                     Map<ExpiredDataType, String> type2QueueName) {
        this.interval = interval;
        this.fileFlag = fileFlag;
        this.type2QueueName = type2QueueName;
    }


    private void init() {
        if (fileFlag) {
            type2Queue = Maps.newHashMapWithExpectedSize(type2QueueName.size());
            for (Map.Entry<ExpiredDataType, String> entry : type2QueueName
                    .entrySet()) {
                try {
                    type2Queue.put(entry.getKey(), new FQueue(entry.getValue()));
                } catch (Exception e) {
                    throw new RuntimeException(
                            "failed to create fqueue with name " + entry.getValue(), e);
                }
            }
        } else {
            queue = new ConcurrentLinkedQueue();

        }
        executorService = Executors.newScheduledThreadPool(1);

        executorService.scheduleAtFixedRate(this, 0, interval, TimeUnit.SECONDS);

    }

    @Override
    public boolean ExpiredDataEnqueue(String context, ExpiredDataType type) {
        boolean flag = false;
        if (fileFlag) {
            FQueue queue = type2Queue.get(type);
            byte[] bytes = context.getBytes();
            flag = queue.offer(bytes);
        } else {
            switch (type) {
                case freqLog1:
                    CarmenFreqLog1 log1 = JSON.parseObject(context, CarmenFreqLog1.class);
                    flag = queue.offer(log1);
                    break;
                case freqLog2:
                    CarmenFreqLog2 log2 = JSON.parseObject(context, CarmenFreqLog2.class);
                    flag = queue.offer(log2);
            }
        }
        return flag;
    }

    @Override
    public void run() {
        try {
            if (fileFlag) {
                Set<ExpiredDataType> types = type2Queue.keySet();
                for (ExpiredDataType expiredDataType : types) {

                    if (expiredDataType.equals(ExpiredDataType.freqLog1)) {
                        FQueue fQueue1 = type2Queue.get(expiredDataType);
                        int size1 = fQueue1.size();
                        for (int i = 0; i < size1; i++) {
                            byte[] bytes1 = fQueue1.poll();
                            String context1 = new String(bytes1);
                            CarmenFreqLog1 log1 = JSON.parseObject(context1, CarmenFreqLog1.class);
                            carmenFreqLog1DAO.save(log1);
                        }
                    }
                    if (expiredDataType.equals(ExpiredDataType.freqLog2)) {
                        FQueue fQueue2 = type2Queue.get(expiredDataType);
                        int size2 = fQueue2.size();
                        for (int i = 0; i < size2; i++) {
                            byte[] bytes2 = fQueue2.poll();
                            String context = new String(bytes2);
                            CarmenFreqLog2 log2 = JSON.parseObject(context, CarmenFreqLog2.class);
                            carmenFreqLog2DAO.save(log2);
                        }
                    }

                }
            } else {
                int size = queue.size();
                for (int i = 0; i < size; i++) {
                    Object res = queue.poll();
                    if (res != null) {
                        if (res instanceof CarmenFreqLog1) {
                            carmenFreqLog1DAO.save((CarmenFreqLog1) res);
                        }
                        if (res instanceof CarmenFreqLog2) {
                            carmenFreqLog2DAO.save((CarmenFreqLog2) res);
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("exception", e);
        }

    }
}
