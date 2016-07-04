package com.caitu99.gateway.apiconfig.dto.req;


import com.caitu99.platform.dao.base.para.IPageConverter;
import com.caitu99.platform.dao.base.para.IPageParameter;
import com.caitu99.platform.dao.common.para.PageParameter;

public class BasePageConvertor implements IPageConverter<ReqQueryBase> {

    public IPageParameter toPage(ReqQueryBase qrydto) {
        PageParameter pageParameter = new PageParameter();
        pageParameter.setRequireTotal(qrydto.isRequireTotal());
        pageParameter.setStart(qrydto.getStart());
        pageParameter.setLimit(qrydto.getLimit());
        return pageParameter;
    }

    public void returnTotal(ReqQueryBase qrydto, int total) {
        qrydto.setTotal(total);
    }


}
