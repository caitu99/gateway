package com.caitu99.gateway.apiconfig.controller;

import com.caitu99.gateway.apiconfig.model.CarmenUser;
import com.caitu99.gateway.apiconfig.service.ICarmenUserService;
import com.caitu99.gateway.cache.RedisOperate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class ManualController {


    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(CreateApiController.class);

    // Resource 默认按照名称进行装配
    @Resource
    RedisOperate redisOperate;
    @Resource
    ICarmenUserService iCarmenUserService;

    /**
     * 返回用户手册页面
     * @param request
     * @return 用户手册页面
     */
    @RequestMapping("/manual")
    public ModelAndView manual(@RequestParam("env") String env,
                               HttpServletRequest request) {
        String userName = null;
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("env", env);
        try {
            String userKey = request.getSession().getAttribute("username").toString();
            userName = redisOperate.getStringByKey(userKey);
            redisOperate.set("username", userName, 60*60); // 一小时
            hashMap.put("data", userName);
        } catch (Exception e) {
            logger.warn("fail to get session", e);
        }
        if(null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "userName is null.");
        }

        Boolean isAdmin = isAdministrator(userName);
        hashMap.put("isAdmin", isAdmin);
        return new ModelAndView("manual", "results", hashMap);
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
