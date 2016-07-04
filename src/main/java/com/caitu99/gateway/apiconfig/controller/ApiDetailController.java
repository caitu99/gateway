package com.caitu99.gateway.apiconfig.controller;

import com.caitu99.gateway.apiconfig.model.CarmenApi;
import com.caitu99.gateway.apiconfig.model.CarmenApiParam;
import com.caitu99.gateway.apiconfig.model.CarmenUser;
import com.caitu99.gateway.apiconfig.service.ICarmenApiParamService;
import com.caitu99.gateway.apiconfig.service.ICarmenApiService;
import com.caitu99.gateway.apiconfig.service.ICarmenUserService;
import com.caitu99.gateway.cache.RedisOperate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dingdongsheng on 15/9/5.
 */
@Controller
public class ApiDetailController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(ApiDetailController.class);

    // Resource 默认按照名称进行装配
    @Resource
    RedisOperate redisOperate;
    @Resource
    ICarmenApiService iCarmenApiService;
    @Resource
    ICarmenApiParamService iCarmenApiParamService;
    @Resource
    ICarmenUserService iCarmenUserService;

    @RequestMapping("/apidetail")
    public ModelAndView apiDetail(@RequestParam(value = "id", required = false) String id,
                                  @RequestParam(value = "env", required = false) String env,
                                  HttpServletRequest request) {
        String userName = null;
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("env", env);
        try {
            String userKey = request.getSession().getAttribute("username").toString();
            userName = redisOperate.getStringByKey(userKey);
            redisOperate.set("username", userName, 60*60); // 一小时
            if(StringUtils.isEmpty(id) || StringUtils.isEmpty(env)) { // 有为空的参数

                hashMap.put("data", userName);
            } else {
                CarmenApi carmenApi = iCarmenApiService.getById(Long.valueOf(id));
                List<CarmenApiParam> carmenApiParam = iCarmenApiParamService.getByApiId(carmenApi.getId(), Byte.valueOf(env));
                hashMap.put("carmenApi", carmenApi);
                hashMap.put("carmenApiParam", carmenApiParam);
                hashMap.put("data", userName);
            }

        } catch (Exception e) {
            logger.error("fail to get api detail", e);
        }
        if(null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "userName is null.");
        }
        Boolean isAdmin = isAdministrator(userName);
        hashMap.put("isAdmin", isAdmin);
        return new ModelAndView("apidetail", "results", hashMap);
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
}
