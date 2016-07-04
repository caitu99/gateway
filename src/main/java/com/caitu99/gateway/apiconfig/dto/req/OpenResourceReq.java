package com.caitu99.gateway.apiconfig.dto.req;

import com.caitu99.gateway.apiconfig.model.OpenResource;

import java.util.Date;

/**
 * Created by chenyun on 15/7/31.
 */
public class OpenResourceReq extends ReqQueryBase<OpenResource> {

    private String groupAlias;

    private Byte isWrite;

    private Byte isInner;

    private Byte level;

    private Date createTimeBegin;

    private Date createTimeEnd;

    public String getGroupAlias() {
        return groupAlias;
    }

    public void setGroupAlias(String groupAlias) {
        this.groupAlias = groupAlias;
    }

    public Byte getIsWrite() {
        return isWrite;
    }

    public void setIsWrite(Byte isWrite) {
        this.isWrite = isWrite;
    }

    public Byte getIsInner() {
        return isInner;
    }

    public void setIsInner(Byte isInner) {
        this.isInner = isInner;
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
