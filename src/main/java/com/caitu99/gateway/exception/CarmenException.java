package com.caitu99.gateway.exception;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

public class CarmenException extends Exception {

    private int code;
    private String description;
    private static final SimplePropertyPreFilter filter = new SimplePropertyPreFilter(CarmenException.class, "code", "message");

    public CarmenException(int code, String description) {
        super(description);
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @JSONField(name = "message")
    public String getDescription() {
        return description;
    }

    @JSONField(name = "message")
    public void setDescription(String description) {
        this.description = description;
    }

    @JSONField(serialize = false)
    public String getMessage() {
        return description;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, filter, SerializerFeature.WriteNullStringAsEmpty);
    }
}
