package com.caitu99.gateway.frequency.test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenyun on 15/8/3.
 */
public class ScheduledTest {

    public static void main(String[] args) {

        doSchedule2();
    }


    public static void doSchedule1() {

        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                System.out
                        .println(Thread.currentThread().getName() + "--start.time--" + new Date());
                processCommand();
                System.out.println(Thread.currentThread().getName() + "--end.time--" + new Date());
            }
        };

        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "--start--" + new Date());
                processCommand();
                System.out.println(Thread.currentThread().getName() + "--end--" + new Date());
            }
        };

        Timer timer = new Timer();

        long delay = 0;
        long intervalPeriod1 = 1 * 1000;
        long intervalPeriod2 = 2 * 1000;

        timer.scheduleAtFixedRate(task1, delay, intervalPeriod1);
        timer.scheduleAtFixedRate(task2, delay, intervalPeriod2);

    }

    public static void processCommand() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void doSchedule2() {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out
                        .println(Thread.currentThread().getName() + "--start.time--" + new Date());
                processCommand();
                System.out.println(Thread.currentThread().getName() + "--end.time--" + new Date());
                throw new RuntimeException("xxxxx");
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        long delay = 0;
        long intervalPeriod1 = 1;
        service.scheduleAtFixedRate(task, delay, intervalPeriod1, TimeUnit.SECONDS);

    }

    public static void doSchedule3() {

        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                System.out
                        .println(Thread.currentThread().getName() + "--start.time--" + new Date());
                processCommand();
                System.out.println(Thread.currentThread().getName() + "--end.time--" + new Date());
            }
        };

        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "--start--" + new Date());
                processCommand();
                System.out.println(Thread.currentThread().getName() + "--end--" + new Date());
            }
        };
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);//线程执行池
        long delay = 1;
        long intervalPeriod1 = 7;
        long intervalPeriod2 = 8;
        executor.scheduleAtFixedRate(task1, delay, intervalPeriod1, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(task2, delay, intervalPeriod2, TimeUnit.SECONDS);

    }

}
