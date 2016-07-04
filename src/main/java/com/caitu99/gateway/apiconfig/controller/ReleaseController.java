package com.caitu99.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSON;
import com.caitu99.gateway.apiconfig.model.CarmenApi;
import com.caitu99.gateway.apiconfig.model.CarmenUser;
import com.caitu99.gateway.apiconfig.service.ICarmenApiService;
import com.caitu99.gateway.apiconfig.service.ICarmenUserService;
import com.caitu99.gateway.cache.RedisOperate;
import com.caitu99.gateway.frequency.service.ICarmenFreqConfigService;
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
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by dingdongsheng on 15/9/5.
 */
@Controller
public class ReleaseController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(ReleaseController.class);

    // Resource 默认按照名称进行装配
    @Resource
    ICarmenApiService iCarmenApiService;
    @Resource
    ICarmenUserService iCarmenUserService;
    @Resource
    RedisOperate redisOperate;

    @RequestMapping("/release")
    public ModelAndView release(@RequestParam(value="env", defaultValue = "1") byte env,
                                HttpServletRequest request,
                                HttpServletResponse response) {

        String userName = null;
        try {
            String userKey = request.getSession().getAttribute("username").toString();
            userName = redisOperate.getStringByKey(userKey);
            redisOperate.set("username", userName, 60*60); // 一小时
        } catch (Exception e) {
            logger.warn("fail to get session", e);
        }
        List<CarmenApi> carmenApi = null;
        try {
            carmenApi = iCarmenApiService.getRecordByEnv(env);
            if(null != carmenApi) {
                Collections.sort(carmenApi, new Comparator<CarmenApi>() {
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
            logger.error("can not get api config", e);
        }
        if(null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "Fail to get user name");
        }
        Map<String, Object> results = new HashMap<>();
        results.put("apilists", carmenApi);
        results.put("user", userName);
        results.put("env", env);
        Boolean isAdmin = isAdministrator(userName);
        results.put("isAdmin", isAdmin);
        return new ModelAndView("release", "results", results);
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
     * 更新API信息
     * @param ids 待更新或者新增的对象
     * @return String 成功返回success，失败返回fail
     */
    @RequestMapping(value = "/releaseapi", produces="application/json;charset=utf-8")
    @ResponseBody
    public String releaseApi(@RequestParam("ids") String ids,
                             @RequestParam("flag") int flag) {

        String status = "success";
        String[] idArray = ids.split(",");
        try {
            if(1 == flag) { // 确认发布
                for (String id : idArray) {
                    CarmenApi carmenApi = new CarmenApi();
                    carmenApi.setId(Long.valueOf(id));
                    carmenApi.setMigrateFlag((byte)3);
                    iCarmenApiService.update(carmenApi);
                }
            } else if(2 == flag) { //取消发布
                for (String id : idArray) {
                    CarmenApi carmenApi = new CarmenApi();
                    carmenApi.setId(Long.valueOf(id));
                    carmenApi.setMigrateFlag((byte)1);
                    iCarmenApiService.update(carmenApi);
                }
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
