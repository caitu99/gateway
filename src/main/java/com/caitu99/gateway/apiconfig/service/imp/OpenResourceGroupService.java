package com.caitu99.gateway.apiconfig.service.imp;

import com.caitu99.gateway.apiconfig.dao.gateway.IResourceGroupDAO;
import com.caitu99.gateway.apiconfig.dto.req.OpenResourceGroupReq;
import com.caitu99.gateway.apiconfig.model.OpenResourceGroup;
import com.caitu99.gateway.apiconfig.service.IOpenResourceGroupService;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * Created by chenyun on 15/7/31.
 */
@Service
public class OpenResourceGroupService implements IOpenResourceGroupService {

    @Resource
    private IResourceGroupDAO resourceGroupDAO;

    @Override
    public int insert(OpenResourceGroup config) {
        resourceGroupDAO.save(config);
        return config.getId();
    }

    @Override
    public void update(OpenResourceGroup config) {
        resourceGroupDAO.update(config);
    }

    @Override
    public void deleteById(int id) {
        resourceGroupDAO.deleteById(id);
    }

    @Override
    public OpenResourceGroup getById(int id) {
        return resourceGroupDAO.getById(id);
    }

    @Override
    public List<OpenResourceGroup> queryWithPage(OpenResourceGroupReq req) {
        return resourceGroupDAO.queryWithPage(req);
    }

    @Override
    public List<String> getGroupAlias() {
        return resourceGroupDAO.getGroupAlias();
    }

    @Override
    public List<OpenResourceGroup> getAll() {
        return resourceGroupDAO.getAll();
    }
}
