package com.caitu99.gateway.frequency.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by chenyun on 15/8/3.
 */
public class threadTest {

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(5);
        for (int index = 0; index < 20; index++) {
            int no = index;
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        System.out.println("access:" + no);
                        Thread.sleep((long) Math.random() * 10000);
                        semaphore.release();
                        System.out.println("-----" + semaphore.availablePermits());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            exec.submit(run);
        }
        exec.shutdown();
    }

}
