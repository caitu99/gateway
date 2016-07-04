package com.caitu99.gateway.frequency.model;

public class CarmenFreqLog1 {

    private Long id;

    private String ip;

    private Long distanceFrom;

    private String hash;

    private Integer count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getDistanceFrom() {
        return distanceFrom;
    }

    public void setDistanceFrom(Long distanceFrom) {
        this.distanceFrom = distanceFrom;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}