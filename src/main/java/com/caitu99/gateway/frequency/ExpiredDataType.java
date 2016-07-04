package com.caitu99.gateway.frequency;

/**
 * Created by chenyun on 15/8/5.
 */
public enum ExpiredDataType {

    freqLog1("1", "freqLog1"), freqLog2("2", "freqLog2");

    private ExpiredDataType(String id, String des) {
        this.id = id;
        this.des = des;
    }

    private String id;

    private String des;

    public String getId() {
        return id;
    }

    public String getDes() {
        return des;
    }
}
