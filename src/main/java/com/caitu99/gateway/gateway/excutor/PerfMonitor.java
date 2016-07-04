package com.caitu99.gateway.gateway.excutor;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class PerfMonitor {

    private Map<String, MethodEntry> methodEntryMap = new ConcurrentHashMap<>();

    private AtomicLong preSync = new AtomicLong(0);
    private AtomicLong preAll = new AtomicLong(0);
    private AtomicLong preCount = new AtomicLong(0);


    public PerfMonitor() {

    }

    public static int getCpuCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    /*public void freshPrePerfData(long phpSync, long phpPre) {
        this.preSync.addAndGet(phpSync);
        this.preAll.addAndGet(phpPre);
        this.preCount.addAndGet(1);

        if (this.preSync.get() < 0 || this.preSync.get() < 0) {
            this.preSync.set(0);
            this.preAll.set(0);
            this.preCount.set(0);
        }
    }

    public int getPreThreadCount() {
        if (preCount.get() < 20) {
            return getCpuCount() * 2;
        }
        int ret = (int) ((preSync.get() / (preAll.get() - preSync.get()) + (double) 1) * getCpuCount());
        if (ret < 0)
            ret = getCpuCount() * 2;
        return ret;
    }*/

    public void putPerfData(String method, long time) {
        MethodEntry methodEntry = methodEntryMap.get(method);
        if (methodEntry == null) {
            MethodEntry entry = new MethodEntry();
            methodEntry = methodEntryMap.putIfAbsent(method, entry);
            if (methodEntry == null) {
                methodEntry = entry;
            }
        }
        methodEntry.put(time);
    }

    /*public long avgPerfData(String method) {
        return methodEntryMap.get(method).avg();
    }*/

    public class MethodEntry {

        private final int BUFFER_NUMBER = 10;
        private int currentIndex = 0;
        private long buffer[] = new long[BUFFER_NUMBER];

        public synchronized void put(long time) {
            buffer[currentIndex % BUFFER_NUMBER] = time;
            ++currentIndex;
        }

        /*public long avg() {
            long sum = 0;
            for (long item : buffer) {
                sum += item;
            }
            return sum / BUFFER_NUMBER;
        }*/
    }

}
