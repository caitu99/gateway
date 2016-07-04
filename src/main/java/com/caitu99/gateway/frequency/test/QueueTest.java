package com.caitu99.gateway.frequency.test;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by chenyun on 15/8/4.
 */
public class QueueTest {

    private static Queue<Integer> queue1 = new ConcurrentLinkedQueue<>();

    private static Queue<Integer> queue2 = new LinkedBlockingQueue<>();

    private static int count = 2;

    private static CountDownLatch latch = new CountDownLatch(count);

    private static ExecutorService es = Executors.newFixedThreadPool(4);


    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        offer1();
        long endTime = System.currentTimeMillis();
        System.out.println("add time:" + (endTime - startTime));
        for (int i = 0; i < count; i++) {
            es.submit(new PollTask1());
        }
        latch.await();
        System.out.println("get time:" + (System.currentTimeMillis() - endTime));
        es.shutdown();

    }

    public static void offer1() {
        for (int i = 0; i < 100000; i++) {
            queue1.offer(i);
        }
    }

    public static void offer2() {
        for (int i = 0; i < 100000; i++) {
            queue2.offer(i);
        }
    }


    static class PollTask1 implements Runnable {

        @Override
        public void run() {
            while (!queue1.isEmpty()) {
                queue1.poll();
                //System.out.println(queue1.poll());
            }
            latch.countDown();
        }


    }

    static class PollTask2 implements Runnable {

        @Override
        public void run() {
            while (!queue2.isEmpty()) {
                queue2.poll();
                //System.out.println(queue2.poll());
            }
            latch.countDown();
        }


    }

}
