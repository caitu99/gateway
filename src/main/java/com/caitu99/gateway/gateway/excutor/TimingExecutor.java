package com.caitu99.gateway.gateway.excutor;


import com.caitu99.gateway.gateway.model.RequestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.ReferenceQueue;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TimingExecutor extends ThreadPoolExecutor {

    private static Logger logger = LoggerFactory.getLogger(TimingExecutor.class);
    private static long EXPIRE_TIME = 5000;

    private String name;
    private AtomicLong threadCount = new AtomicLong(0);
    private ReferenceQueue<RequestEvent> queue = new ReferenceQueue<>();
    private TreeMap<Long, RequestEvent> timeMap = new TreeMap<>();
    private boolean monitor;

    private AtomicInteger count;

    public TimingExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime,
                          TimeUnit unit, BlockingQueue<Runnable> workQueue, boolean monitor) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.name = name;
        this.monitor = monitor;
        /*if (monitor) {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    timeMapMonitor(timeMap);
                }
            }, 0, 100);
        }*/
    }

    public TimingExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime,
                          TimeUnit unit, BlockingQueue<Runnable> workQueue, boolean monitor, AtomicInteger count) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.name = name;
        this.monitor = monitor;
        if (monitor) {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    timeMapMonitor(timeMap);
                }
            }, 0, 100);
        }
        this.count = count;
    }

    public static TimingExecutor newFixedThreadPool(String name, int corePoolSize, int maximumPoolSize,
                                                    long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, boolean monitor) {
        return new TimingExecutor(name, corePoolSize, maximumPoolSize,
                keepAliveTime, unit, workQueue, monitor);
    }

    public static TimingExecutor newFixedThreadPool(String name, int corePoolSize, int maximumPoolSize,
                                                    long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                                    boolean monitor, AtomicInteger count) {
        return new TimingExecutor(name, corePoolSize, maximumPoolSize,
                keepAliveTime, unit, workQueue, monitor, count);
    }

    public static TimingExecutor newCachedThreadPool(String name) {
        return new TimingExecutor(name, 0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), false);
    }

    public static TimingExecutor newCachedThreadPool(String name, boolean monitor) {
        return new TimingExecutor(name, 0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), monitor);
    }

    private synchronized void putToTimeMap(String name, RequestEvent event) {
        timeMap.put(event.getTicTac().get(name).getStart(), event);
    }

    private synchronized void rmvFromTimeMap(String name, RequestEvent event) {
        timeMap.remove(event.getTicTac().get(name).getStart());
    }

    public synchronized void timeMapMonitor(TreeMap<Long, RequestEvent> map) {
        Map.Entry<Long, RequestEvent> entry;
        while ((entry = map.firstEntry()) != null) {
            if (entry.getKey() + EXPIRE_TIME > System.currentTimeMillis()) {
                return;
            }

            // pop the first entry
            entry = map.pollFirstEntry();

            RequestEvent event = entry.getValue();
            if (event == null)
                continue;

            // TODO: process timeout event, cancel or other action

            logger.warn("an event expired: " + event);
        }
    }

    public TreeMap<Long, RequestEvent> getTimeMap() {
        return timeMap;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        if (r instanceof PipeWorker) {
            PipeWorker worker = (PipeWorker) r;
            RequestEvent event = worker.getEvent();
            event.getTicTac().tic(worker.getName());
            if (monitor)
                this.putToTimeMap(worker.getName(), event);
        }
        threadCount.incrementAndGet();

        if (count != null)
            count.incrementAndGet();
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if (r instanceof PipeWorker) {
            PipeWorker worker = (PipeWorker) r;
            RequestEvent event = worker.getEvent();
            event.getTicTac().tac(worker.getName());
            if (monitor)
                this.rmvFromTimeMap(worker.getName(), event);
        }
        super.afterExecute(r, t);
        threadCount.decrementAndGet();

        if (count != null)
            count.decrementAndGet();
    }

    @Override
    protected void terminated() {
        super.terminated();
    }

    public AtomicLong getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(AtomicLong threadCount) {
        this.threadCount = threadCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
