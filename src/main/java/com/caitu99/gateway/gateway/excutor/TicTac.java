package com.caitu99.gateway.gateway.excutor;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TicTac {

    private static Logger logger = LoggerFactory.getLogger(TicTac.class);

    private Map<String, STEntry> entryMap = new HashMap<>();

    /**
     * record starting point
     */
    public void tic(String name) {
        if (entryMap.get(name) != null) {
            logger.error(name + " already exists");
            return;
        }
        entryMap.put(name, new STEntry(System.currentTimeMillis(), 0));
    }

    /**
     * record ending point
     */
    public void tac(String name) {
        STEntry entry = entryMap.get(name);
        if (entry == null) {
            logger.error(name + " does not exist");
            return;
        }
        entry.setEndThreadId(Thread.currentThread().getId());
        entry.setEnd(System.currentTimeMillis());
    }

    public Map<String, STEntry> getEntryMap() {
        return entryMap;
    }

    /*public void setEntryMap(Map<String, STEntry> entryMap) {
        this.entryMap = entryMap;
    }*/

    /**
     * get time by name
     */
    public STEntry get(String name) {
        return entryMap.get(name);
    }

    /**
     * get elapsed time by name
     */
    public long elapsed(String name) {
        STEntry entry = entryMap.get(name);
        if (entry != null) {
            if (entry.getEnd() != 0) {
                return entry.getEnd() - entry.getStart();
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(entryMap);
    }

    public class STEntry {

        private long startThreadId;
        private long endThreadId;
        private long start;
        private long end;

        public STEntry(long start, long end) {
            this.start = start;
            this.end = end;
            this.startThreadId = Thread.currentThread().getId();
        }

        public long getElapsed() {
            if (end == 0) {
                return -1;
            }
            return end - start;
        }

        /*public long getStartThreadId() {
            return startThreadId;
        }

        public void setStartThreadId(long startThreadId) {
            this.startThreadId = startThreadId;
        }

        public long getEndThreadId() {
            return endThreadId;
        }*/

        public void setEndThreadId(long endThreadId) {
            this.endThreadId = endThreadId;
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }

}
