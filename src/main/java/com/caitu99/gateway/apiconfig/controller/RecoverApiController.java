package com.caitu99.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSON;
import com.caitu99.gateway.apiconfig.model.CarmenApi;
import com.caitu99.gateway.apiconfig.service.ICarmenApiService;
import com.caitu99.gateway.cache.RedisOperate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by dingdongsheng on 15/9/10.
 */

@Controller
public class RecoverApiController {
    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(RecoverApiController.class);

    // Resource 默认按照名称进行装配
    @Resource
    RedisOperate redisOperate;
    @Resource
    ICarmenApiService iCarmenApiService;

    @RequestMapping("/recoverapi")
    public ModelAndView recoverApi(@RequestParam("env") String env,
                                   HttpServletRequest request) {
        String userName = null;
        try {
            String userKey = request.getSession().getAttribute("username").toString();
            userName = redisOperate.getStringByKey(userKey);
            redisOperate.set("username", userName, 60*60); // 一小时
        } catch (Exception e) {
            logger.warn("fail to get session", e);
        }

        List<CarmenApi> allDeletedRecord = null;
        try {
            allDeletedRecord = iCarmenApiService.getAllDeletedRecord();
            if(null != allDeletedRecord) {
                Collections.sort(allDeletedRecord, new Comparator<CarmenApi>() {
                    @Override
                    public int compare(CarmenApi arg1, CarmenApi arg2) {
                        if (StringUtils.isEmpty(arg1.getCreateTime()) || StringUtils.isEmpty(arg2.getCreateTime())) { // 防止脏数据
                            return 0;
                        }
                        return arg2.getCreateTime().compareTo(arg1.getCreateTime()); // 按时间逆序排序
                    }
                });
            }
        } catch (Exception e) {
            logger.error("fail to get deleted api",e);
        }

        if(null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "Fail to get user name");
        }
        Map<String, Object> results = new HashMap<>();
        results.put("apilists", allDeletedRecord);
        results.put("user", userName);
        results.put("env", env);
        return new ModelAndView("recoverapi", "results", results);
    }

    @RequestMapping(value = "/executeapis", produces="application/json;charset=utf-8")
    @ResponseBody
    public String executeApis(@RequestParam("ids") String ids) {
        String status = "success";
        String[] idArray = ids.split(",");
        try {
            for (String id : idArray) {
                CarmenApi carmenApi = new CarmenApi();
                carmenApi.setId(Long.valueOf(id));
                carmenApi.setIsDelete("n");
                iCarmenApiService.update(carmenApi);
            }
        } catch (Exception e) {
            status = "fail";
            logger.warn("fail to convert from jsonString to object", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }
}
