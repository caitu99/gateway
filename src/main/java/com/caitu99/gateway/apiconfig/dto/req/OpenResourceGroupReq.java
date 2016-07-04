package com.caitu99.gateway.apiconfig.dto.req;

import com.caitu99.gateway.apiconfig.model.OpenResourceGroup;

import java.util.Date;

/**
 * Created by chenyun on 15/7/31.
 */
public class OpenResourceGroupReq extends ReqQueryBase<OpenResourceGroup> {

    private String alias;

    private Byte level;

    private Date createTimeBegin;

    private Date createTimeEnd;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Byte getLevel() {
        return level;
    }

    public void setLevel(Byte level) {
        this.level = level;
    }

    public Date getCreateTimeBegin() {
        return createTimeBegin;
    }

    public void setCreateTimeBegin(Date createTimeBegin) {
        this.createTimeBegin = createTimeBegin;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }
}
