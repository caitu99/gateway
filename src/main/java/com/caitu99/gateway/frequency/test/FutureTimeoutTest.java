package com.caitu99.gateway.frequency.test;


import java.util.concurrent.*;

/**
 * Created by chenyun on 15/8/3.
 */
public class FutureTimeoutTest {

    /* 信号量*/
    private Semaphore semaphore = new Semaphore(0);

    /**
     * 线程池
     */
    private ThreadPoolExecutor
            pool =
            new ThreadPoolExecutor(3, 5, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3));

    private Future<String> future;

    public void futureTimeout() {
        future = pool.submit(new Callable<String>() {
            @Override
            public String call() {
                String res = null;
                try {
                    System.out.println("xxxxxxxxxxxxxxxx");
                    semaphore.acquire();//同步阻塞获取信号量
                    res = "ok";

                } catch (InterruptedException e) {
                    res = "interrupted";
                }
                return res;
            }
        });

        String res = "timeout";
        try {
            res = future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        boolean cancelRes = future.cancel(true);

        System.out.println("res is :" + res);

        try {
            res = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("after cancel res is :" + res);

        System.out.println("cancel result:" + cancelRes);

        System.out.println("当前active线程数:" + pool.getActiveCount());


    }

    public static void main(String[] args) {
        FutureTimeoutTest test = new FutureTimeoutTest();
        test.futureTimeout();
        test.pool.shutdown();
    }

}
