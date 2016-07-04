package com.caitu99.gateway.apiconfig.dao.gateway;


import com.caitu99.gateway.apiconfig.dto.req.OpenResourceGroupReq;
import com.caitu99.gateway.apiconfig.model.OpenResourceGroup;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface IResourceGroupDAO extends IEntityDAO<OpenResourceGroup, OpenResourceGroupReq> {

    List<String> getGroupAlias();

    List<OpenResourceGroup> getAll();

}