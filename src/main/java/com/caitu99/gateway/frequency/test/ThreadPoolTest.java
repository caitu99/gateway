package com.caitu99.gateway.frequency.test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenyun on 15/8/13.
 */
public class ThreadPoolTest {

    private static ThreadPoolExecutor
            executor =
            new ThreadPoolExecutor(1, 1, 1000, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    System.out.println("test");
                    throw new RuntimeException("test");
                }
            };
            executor.execute(r);
        }
        System.out.println(executor.getActiveCount());
        executor.shutdown();
    }

}
