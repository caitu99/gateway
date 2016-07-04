package com.caitu99.gateway.frequency.model;

public class CarmenFreqConfig {

    private Long id;

    private Integer value;

    private Integer type;

    private Long apiRef;

    private Long clientId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getApiRef() {
        return apiRef;
    }

    public void setApiRef(Long apiRef) {
        this.apiRef = apiRef;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}