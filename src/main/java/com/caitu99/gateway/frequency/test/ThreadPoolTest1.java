package com.caitu99.gateway.frequency.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenyun on 15/8/13.
 */
public class ThreadPoolTest1 implements Runnable {

    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);

    public static void main(String[] args) {
        for (int i = 0; i < 7; i++) {
            ThreadPoolTest1 task = new ThreadPoolTest1();
            executorService.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
        }
        executorService.shutdown();
    }


    @Override
    public void run() {
        throw new RuntimeException("xxxxxxxx");
    }
}
