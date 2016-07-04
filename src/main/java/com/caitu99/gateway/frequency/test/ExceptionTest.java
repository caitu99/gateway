package com.caitu99.gateway.frequency.test;

/**
 * Created by chenyun on 15/8/13.
 */
public class ExceptionTest {

    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getName());
        Test t = new Test();
        t.start();
        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(t.getName() + ":" + e.getMessage());
            }
        });
    }

    public static class Test extends Thread {

        public Test() {

        }

        @Override
        public void run() {
            throw new RuntimeException("test");
        }
    }

}
