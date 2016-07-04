package com.caitu99.gateway.apiconfig.service.imp;

import com.alibaba.fastjson.JSON;

import com.caitu99.gateway.apiconfig.dao.gateway.IResourceDAO;
import com.caitu99.gateway.apiconfig.dto.req.OpenResourceReq;
import com.caitu99.gateway.apiconfig.model.OpenResource;
import com.caitu99.gateway.apiconfig.service.IOpenResourceService;
import com.caitu99.gateway.cache.RedisOperate;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * Created by chenyun on 15/7/31.
 */
@Service
public class OpenResourceService implements IOpenResourceService {

    @Resource
    private IResourceDAO openResourceDAO;

    @Resource
    private RedisOperate redis;

    @Override
    public int insert(OpenResource config) {
        openResourceDAO.save(config);
        int id = config.getId();
        String key = prefix + "_" + config.getUri();
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(key);
        redis.del(singleRecordPrefix);
        return id;
    }

    @Override
    public void update(OpenResource config) {
        openResourceDAO.update(config);
        config = getById(config.getId());
            if (config!=null) {
                String key = prefix + "_" + config.getUri();
                String singleRecordPrefix = prefix + "_" + config.getId();
                redis.del(key);
                redis.del(singleRecordPrefix);
            }
    }

    @Override
    public void deleteById(int id) {
        OpenResource config = getById(id);
        if (config != null) {
            String key = prefix + "_" + config.getUri();
            redis.del(key);
        }
        String singleRecordPrefix = prefix + "_" + id;
        redis.del(singleRecordPrefix);
        openResourceDAO.deleteById(id);
    }

    @Override
    public OpenResource getById(int id) {
        return openResourceDAO.getById(id);
    }

    @Override
    public OpenResource getByUri(String uri) {
        OpenResource config = null;
        String key = prefix + "_" + uri;
        String context = redis.getStringByKey(key);
        if (context != null) {
            config = JSON.parseObject(context, OpenResource.class);
        } else {
            config = openResourceDAO.getByUri(uri);
            if (config != null) {
                redis.set(key, JSON.toJSONString(config));
            }
        }
        return config;
    }

    @Override
    public OpenResource getByUriVersion(String uri, String version) {
        return openResourceDAO.getByUriVersion(uri, version);
    }

    @Override
    public List<OpenResource> queryWithPage(OpenResourceReq req) {
        return openResourceDAO.queryWithPage(req);
    }

    @Override
    public void deleteByUri(String uri) {
        OpenResource config = getByUri(uri);
        if (config != null) {
            String key = prefix + "_" + config.getUri();
            String singleRecordPrefix = prefix + "_" + config.getId();
            redis.del(key);
            redis.del(singleRecordPrefix);
        }
        openResourceDAO.deleteByUri(uri);
    }

    @Override
    public List<String> getResourceUriList() {
        return openResourceDAO.getResourceUriList();
    }
}
