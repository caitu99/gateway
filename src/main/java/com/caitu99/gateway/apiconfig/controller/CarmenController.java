package com.caitu99.gateway.apiconfig.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.gateway.apiconfig.model.CarmenApi;
import com.caitu99.gateway.apiconfig.service.ICarmenApiMethodMappingService;
import com.caitu99.gateway.apiconfig.service.ICarmenApiParamService;
import com.caitu99.gateway.apiconfig.service.ICarmenApiService;
import com.caitu99.gateway.apiconfig.service.ICarmenParamMappingService;
import com.caitu99.gateway.apiconfig.service.ICarmenServiceMethodParamService;
import com.caitu99.gateway.apiconfig.service.ICarmenServiceMethodService;
import com.caitu99.gateway.apiconfig.service.ICarmenStructService;
import com.caitu99.gateway.cache.RedisOperate;
import com.caitu99.gateway.monitor.entry.MonitorDataController;
import com.caitu99.gateway.utils.HttpUtils;


/**
 * 此类给apiaconfig页面提供Ajax请求方法并返回json串或者字符串。
 * <p>
 * Created by dingdongsheng on 15/7/17.
 */

@Controller
public class CarmenController {
    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(CarmenController.class);

    @Value("${baseplatform.pullUrl}")
    private String pullUrl; // 对接基础平台监控的数据推送url，配置在/properties/constant.properties

    @Resource
    ICarmenApiService iCarmenApiService;
    @Resource
    ICarmenApiParamService iCarmenApiParamService;
    @Resource
    ICarmenServiceMethodService iCarmenServiceMethodService;
    @Resource
    ICarmenServiceMethodParamService iCarmenServiceMethodParamService;
    @Resource
    ICarmenStructService iCarmenStructService;
    @Resource
    ICarmenApiMethodMappingService iCarmenApiMethodMappingService;
    @Resource
    ICarmenParamMappingService iCarmenParamMappingService;
    @Autowired
    MonitorDataController monitorDataController;
    @Resource
    RedisOperate redisOperate;

    //private static Client monitorClient;

    @Value("${base.business}")
    private String business; // 存放到监控平台时区分业务含义

    @Value("${api.hawk.url}")
    private String hawkUrl; // 对接基础平台监控的数据推送url，配置在/properties/constant.properties

    @Value("${application.name}")
    private String applicationName; // 对接基础平台监控的数据拉取url, 配置在/properties/constant.properties


    @PostConstruct
    public void initHawk() {
        /*com.youzan.hawk.collect.utils.Config config = new com.youzan.hawk.collect.utils.Config();
        config.setApplication(applicationName);
        config.setUrl(hawkUrl);
        monitorClient = ClientFactory.instance(config);*/
    }

    /**
     * 获取API相关信息
     *
     * @param namespace 命名空间
     * @param name      方法名字
     * @param version   版本号
     * @return String，失败返回"fail"字符串，成功返回对象的JSON字符串
     */
    @RequestMapping(value = "/getapi", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getApi(@RequestParam("namespace") String namespace,
                         @RequestParam("name") String name,
                         @RequestParam("version") String version,
                         @RequestParam("env") byte env) {
        String result = "fail";

        try {
            CarmenApi carmenApi = iCarmenApiService.getRecordByCondition(namespace, name, version, env);
            if (null != carmenApi) {
                result = JSON.toJSONString(carmenApi);
                return result;
            }
        } catch (Exception e) {
            logger.warn("fail to get api.", e);
        }

        try {
            result = JSON.toJSONString(result);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return result;
    }

    /**
     * 按照env获取API列表
     *
     * @param env 环境变量  1：开发，2：测试，3：生产
     * @return API List 对象
     */
    @RequestMapping(value = "/apilistbyenv", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getApiListByEnv(@RequestParam("env") Byte env) {

        String status = "fail";
        try {
            List<CarmenApi> carmenApi = iCarmenApiService.getRecordByEnv(env);
            String result = JSON.toJSONString(carmenApi);
            return result;
        } catch (Exception e) {
            logger.warn("fail to get api list", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }

    /**
     * 获取监控数据
     *
     * @param stime  开始时间的时间戳
     * @param etime  结束时间的时间戳
     * @param metric 查询的key
     * @param group  API数据那个group，目前没有实际使用，备用字段
     * @param host   主机IP
     * @param api    API名字
     * @return 成功返回监控数据，否则返回fail
     */
    @RequestMapping(value = "/querymonitor", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String queryMonitor(@RequestParam("stime") String stime,
                               @RequestParam("etime") String etime,
                               @RequestParam("metric") String metric,
                               @RequestParam(value = "group", required = false) String group,
                               @RequestParam(value = "host", required = false) String host,
                               @RequestParam(value = "api", required = false) String api) {

        /*String status = "fail";

        String[] elements = metric.split(",");

        Query.QueryBuilder queryBuilder = Query.getBuilder();
        queryBuilder.metric("caitu99.gateway");

        for (int i = 0; i < elements.length; i++) {
            queryBuilder.field(elements[i]);
        }

        queryBuilder.timeRange(Long.valueOf(stime), Long.valueOf(etime));
        if (StringUtils.isEmpty(api)) { // 空串
            queryBuilder.tag("api", "*");
        } else {
            queryBuilder.tag("api", api);
        }

        if (StringUtils.isEmpty(host)) { // 空串
            queryBuilder.tag("host", "*");
        } else {
            queryBuilder.tag("host", host);
        }
        Query query = queryBuilder.build();
        try {
            QueryResult queryResult = monitorClient.query(query);
            String result = parseHawkResult(queryResult);
            return result;
        } catch (Exception e) {
            logger.warn("fail to query monitor data",e);
        }

        return status;*/
        return monitorDataController.getMonitoData(stime,etime,metric,group,host,api);
    }

    public String parseHawkResult(/*QueryResult result*/) {
        /*String status = "fail";
        int code = result.getCode();
        if(1001 == code) { // 成功返回
            String jsonResultStr = JSON.toJSONString(result.getResult());
            return jsonResultStr;
        } else {
            logger.debug("每分钟查询失败， 错误码：" + code);
        }
        return status;*/
    	return "";
        //return "";
    }

    /**
     * 统一登录页面的回调函数，处理accessToken并取得用户名
     *
     * @param code     accessToken
     * @param request  servlet request
     * @param response servlet response
     * @return apiconfig页面
     */

    @RequestMapping(value = "/apicallback")
    public ModelAndView getApiConfig(@RequestParam(value = "code", required = false) String code, HttpServletRequest request, HttpServletResponse response) {

        if (null == code) { //code如果没接收到参数，表示第一次统一登录没有返回code，需再次重定向到统一登录界面。
            return new ModelAndView("redirect:/apilist");
        }
        String url = "http://login.qima-inc.com/oauth2/user";
        String params = "access_token=" + code;
        String result = "";
        try {
            result = HttpUtils.sendGet(url, params); // 向统一登录页面请求用户信息
        } catch (Exception e) {
            logger.error("fail to request for " + url, e);
        }
        String userName = "";
        if (!"".equals(result)) { // 结果不为空
            JSONObject jsonObject = null;
            try {
                jsonObject = JSON.parseObject(result);
            } catch (Exception e) {
                logger.debug("error", e);
            }
            String codeStr = jsonObject.getString("code");
            if ("1000".equals(codeStr)) { // 1000表示返回成功状态
                JSONObject user = jsonObject.getJSONObject("value");
                if (null != user) {
                    userName = user.getString("username");
                    String nameKey = userName + System.currentTimeMillis();
                    request.getSession().setAttribute("username", nameKey);
                    redisOperate.set(nameKey, userName, 60*60); // 一小时
                }
            }

        }
        return new ModelAndView("redirect:/apilist", "data", userName);
    }

    /**
     * 根据id删除api记录
     *
     * @param id API的id号
     * @return String 成功返回success, 错误返回fail
     */
    @RequestMapping(value = "/deleteapi", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String deleteApi(@RequestParam("id") Integer id) {
        String status = "success";

        try {
            iCarmenApiService.deleteById(id);
        } catch (Exception e) {
            status = "fail";
            logger.warn("fail to delete record by id.", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }

    /**
     * 统一错误处理界面
     *
     * @return
     */
    @RequestMapping("/unifyerror")
    public ModelAndView unifyError() {

        return new ModelAndView("unifyError");
    }
}
