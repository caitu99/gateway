package com.caitu99.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSON;
import com.caitu99.gateway.apiconfig.model.CarmenUser;
import com.caitu99.gateway.apiconfig.service.ICarmenUserService;
import com.caitu99.gateway.cache.RedisOperate;
import com.caitu99.gateway.console.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dingdongsheng on 15/9/5.
 */
@Controller
public class CacheManageController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(CacheManageController.class);

    // Resource 默认按照名称进行装配
    @Resource
    RedisOperate redisOperate;
    @Resource
    ICarmenUserService iCarmenUserService;

    /**
     * 管理cache
     * @param env 环境变量 1 dev, 2 test, 3 product
     * @param request
     * @return 管理页面cache
     */
    @RequestMapping("/cachemanage")
    public ModelAndView cacheManage(@RequestParam("env") String env,
                                    HttpServletRequest request) {
        String userName = null;
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("env", env);
        try {
            // 获取用户名
            String userKey = request.getSession().getAttribute("username").toString();
            userName = redisOperate.getStringByKey(userKey);
            redisOperate.set("username", userName, 60*60); // 一小时
            hashMap.put("data", userName);
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, String> allCacheNames = cacheManager.readNodes();
            hashMap.put("allCacheNames", allCacheNames);

        } catch (Exception e) {
            logger.warn("fail to get session", e);
        }
        if(null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "userName is null.");
        }
        Boolean isAdmin = isAdministrator(userName);
        hashMap.put("isAdmin", isAdmin);
        return new ModelAndView("cachemanage", "results", hashMap);
    }

    public Boolean isAdministrator(String userName) {

        try {
            List<CarmenUser> user = iCarmenUserService.getByUserName(userName);
            for(CarmenUser carmenUser : user) {
                if(1 == carmenUser.getUserGroup()) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("can not get uesrs.", e);
        }
        return false;
    }

    /**
     * 清除对应实例的缓存
     * @param names 实例名
     * @return 成功返回success，失败返回fail
     */
    @RequestMapping(value = "/clearcache", produces="application/json;charset=utf-8")
    @ResponseBody
    public String clearCache(@RequestParam("names") String names) {
        String status = "fail";
        String[] allNames = names.split(",");
        List<String> instance = Arrays.asList(allNames);
        try {
            CacheManager cacheManager = CacheManager.getInstance();
            cacheManager.updateNodes(instance);
            status = "success";
        } catch (Exception e) {
            logger.error("can not clear cache.", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }

        return status;
    }

    /**
     * 加载对应实例的缓存
     * @param names 实例名
     * @return 成功返回success，失败返回fail
     */
    @RequestMapping(value = "/preloadcache", produces="application/json;charset=utf-8")
    @ResponseBody
    public String preLoadCache(@RequestParam("names") String names) {
        String status = "fail";
        String[] allNames = names.split(",");
        List<String> instance = Arrays.asList(allNames);
        try {
            CacheManager cacheManager = CacheManager.getInstance();
            //添加缓存预加载
            cacheManager.updatePreNodes(instance);
            status = "success";
        } catch (Exception e) {
            logger.error("can not clear cache.", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }

        return status;
    }
}
