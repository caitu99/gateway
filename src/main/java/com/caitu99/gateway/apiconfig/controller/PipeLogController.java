package com.caitu99.gateway.apiconfig.controller;

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
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dingdongsheng on 15/9/5.
 */

@Controller
public class PipeLogController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(PipeLogController.class);

    // Resource 默认按照名称进行装配
    @Resource
    RedisOperate redisOperate;

    /**
     * 查看API的pipeline调用
     * @param request
     * @return 日志观察页面
     */
    @RequestMapping("/pipelog")
    public ModelAndView pipeLog(@RequestParam("env") String env,
                                HttpServletRequest request) {

        String userName = null;
        Map<String, String> results = new HashMap<>();
        results.put("env", env);
        try {
            String userKey = request.getSession().getAttribute("username").toString();
            userName = redisOperate.getStringByKey(userKey);
            redisOperate.set("username", userName, 60*60); // 一小时
            results.put("data", userName);
        } catch (Exception e) {
            logger.warn("fail to get session", e);
        }
        if(null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "userName is null.");
        }
        return new ModelAndView("pipelog", "results", results);
    }

    /**
     * 查询API审计的log
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param api api名字
     * @param id 对象的id
     * @return 日志，否则返回空提示。
     */
    @RequestMapping(value = "/querylog", produces="application/text;charset=utf-8")
    @ResponseBody
    public String queryLog(@RequestParam("startTime") String startTime,
                           @RequestParam("endTime") String endTime,
                           @RequestParam(value = "api", required = false, defaultValue = "empty") String api,
                           @RequestParam(value = "id", required = false, defaultValue = "empty") String id) {
        String status = "fail";

        try {
            Calendar date = Calendar.getInstance();
            int year = date.get(Calendar.YEAR);
            int month = date.get(Calendar.MONTH) + 1;
            int day = date.get(Calendar.DAY_OF_MONTH);
            String logfile = "pipe." + year + "-";
            if(month < 10) {
                logfile = logfile + "0" + month + "-";
            } else {
                logfile = logfile + month + "-";
            }
            if(day < 10) {
                logfile = logfile + "0" + day + ".log";
            } else {
                logfile = logfile + day + ".log";
            }
            String command = "python /data/logs/carmen/logview.py /data/logs/carmen/" + logfile;
            if (StringUtils.isEmpty(api)) {
                api = "empty";
            }
            if (StringUtils.isEmpty(id)) {
                id = "empty";
            }

            command = command + " " + startTime + " " + endTime + " " + api + " " + id;

            String result = executeShellCommand(command);
            status = result;
        } catch (Exception e) {
            logger.error("fail to get log for pipeline.", e);
        }
        return status;
    }

    /**
     * 执行shell command
     * @param command 命令行字符串
     * @return 执行失败返回fail，执行成功，返回shell的返回值
     */
    public String executeShellCommand(String command) {
        String status = "fail";
        Runtime run = Runtime.getRuntime();
        String result = "";
        BufferedReader br = null;
        BufferedInputStream in = null;

        try {
            Process p = run.exec(command);
            if(p.waitFor() != 0){
                result += "no process ID";
                return status;
            }
            in = new BufferedInputStream(p.getInputStream());
            br = new BufferedReader(new InputStreamReader(in));
            String lineStr;
            while ((lineStr = br.readLine()) != null) {
                result += lineStr;
            }
        } catch (Exception e) {
            logger.error("fail to execute shell", e);
            return status;
        }finally{
            if(br!=null){
                try {
                    br.close();
                    in.close();
                } catch (IOException e) {
                    logger.error("fail to clean resource", e);
                }
            }
            logger.info("ShellUtil.ExeShell=>"+result);
            return  result;
        }
    }


}
