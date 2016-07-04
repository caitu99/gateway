package com.caitu99.gateway.apiconfig.service;


import com.caitu99.gateway.apiconfig.dto.req.OpenResourceReq;
import com.caitu99.gateway.apiconfig.model.OpenResource;

import java.util.List;

/**
 * Created by chenyun on 15/7/31.
 */
public interface IOpenResourceService {


    final String prefix = "open_resource_";

    int insert(OpenResource config);

    void update(OpenResource config);

    void deleteById(int id);

    OpenResource getById(int id);

    OpenResource getByUri(String uri);

    OpenResource getByUriVersion(String uri, String version);

    List<OpenResource> queryWithPage(OpenResourceReq req);

    void deleteByUri(String uri);

    List<String>getResourceUriList();
}
