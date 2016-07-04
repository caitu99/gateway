package com.caitu99.gateway.frequency.model;

public class CarmenFreqLog2 {

    private Long id;

    private String appId;

    private String method;

    private Long distanceFrom;

    private String hash;

    private Integer count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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