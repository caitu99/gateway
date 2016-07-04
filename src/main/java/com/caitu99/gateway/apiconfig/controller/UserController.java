package com.caitu99.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSON;
import com.caitu99.gateway.apiconfig.model.CarmenUser;
import com.caitu99.gateway.apiconfig.service.ICarmenUserService;
import com.caitu99.gateway.cache.RedisOperate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Executable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dingdongsheng on 15/9/5.
 */

@Controller
public class UserController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    // Resource 默认按照名称进行装配
    @Resource
    ICarmenUserService iCarmenUserService;
    @Resource
    RedisOperate redisOperate;


    /**
     * 用户管理页面
     * @param request
     * @return 用户管理页面
     */
    @RequestMapping("/user")
    public ModelAndView user(@RequestParam("env") String env,
                             HttpServletRequest request) {
        String userName = null;
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("env", env);
        try {
            String userKey = request.getSession().getAttribute("username").toString();
            userName = redisOperate.getStringByKey(userKey);
            redisOperate.set("username", userName, 60*60); // 一小时
            List<CarmenUser> allList = iCarmenUserService.getAllList();
            hashMap.put("data", userName);
            hashMap.put("allList", allList);
        } catch (Exception e) {
            logger.warn("fail to get session", e);
        }
        if(null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "userName is null.");
        }
        return new ModelAndView("user", "results", hashMap);
    }

    /**
     * 获取用户信息
     * @param userGroup 用户组
     * @param userName 用户名
     * @return 成功返回用户信息，失败返回fail
     */
    @RequestMapping(value = "/getusers", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getUsers(@RequestParam(value = "userGroup", defaultValue = "") String userGroup,
                           @RequestParam("userName") String userName) {

        String status = "fail";

        try {
            List<CarmenUser> user = iCarmenUserService.getByUserName(userName);
            String result = JSON.toJSONString(user.get(0));
            return result;
        } catch (Exception e) {
            logger.error("can not get uesrs.", e);
        }


        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }

        return status;
    }

    /**
     * 判断当前登录用户是不是管理员
     * @param userName 当前登录用户名
     * @return 是管理员返回true，不是返回false
     */
    @RequestMapping(value = "/isadministrator", produces="application/text;charset=utf-8")
    @ResponseBody
    public Boolean isAdministrator(@RequestParam("userName") String userName) {

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
     * 删除用户
     * @param id 用户id
     * @return 成功返回success，失败返回fail
     */
    @RequestMapping(value = "deleteuser", produces="application/json;charset=utf-8")
    @ResponseBody
    public String deleteUser(@RequestParam("id") String id) {
        String status = "fail";

        try {
            iCarmenUserService.deleteById(Integer.valueOf(id));
            status = "success";
        } catch (NumberFormatException e) {
            logger.error("can not delete user.", e);
        } catch (Exception e) {
            logger.error("no session", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }

        return status;
    }

    /**
     * 增加普通用户或者管理员
     * @param userName 用户名
     * @param userGroup 用户所属组
     * @param request
     * @return 成功返回success，失败返回fail
     */
    @RequestMapping(value = "/adduser", produces="application/json;charset=utf-8")
    @ResponseBody
    public String addUser(@RequestParam("userName") String userName,
                          @RequestParam("userGroup") String userGroup,
                          HttpServletRequest request) {
        String status = "fail";


        try {
            CarmenUser user = new CarmenUser();
            user.setUserName(userName);
            user.setUserGroup(Integer.valueOf(userGroup));
            user.setCreateTime(new Date());
            String userKey = request.getSession().getAttribute("username").toString();
            userName = redisOperate.getStringByKey(userKey);
            user.setCreator(userName);

            iCarmenUserService.insert(user);
            status = "success";
        } catch (NumberFormatException e) {
            logger.error("can not delete user.", e);
        } catch (Exception e) {
            logger.error("no session", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }

        return status;
    }

}
