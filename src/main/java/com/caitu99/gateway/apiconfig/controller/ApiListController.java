package com.caitu99.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSON;
import com.caitu99.gateway.apiconfig.model.*;
import com.caitu99.gateway.apiconfig.service.*;
import com.caitu99.gateway.cache.RedisOperate;
import com.caitu99.gateway.frequency.model.CarmenFreqConfig;
import com.caitu99.gateway.frequency.service.ICarmenFreqConfigService;
import com.caitu99.gateway.oauth.model.OpenOauthClients;
import com.caitu99.gateway.oauth.service.IOpenOauthClientsService;
import com.caitu99.gateway.utils.AppUtils;
import com.caitu99.gateway.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by dingdongsheng on 15/9/5.
 */

@Controller
public class ApiListController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(ApiListController.class);

    // Resource 默认按照名称进行装配
    @Resource
    ICarmenApiService iCarmenApiService;
    @Resource
    ICarmenFreqConfigService iCarmenFreqConfigService;
    @Resource
    ICarmenApiParamService iCarmenApiParamService;
    @Resource
    IOpenResourceService iOpenResourceService;
    @Resource
    IOpenResourceGroupService iOpenResourceGroupService;
    @Resource
    IOpenOauthClientsService iOpenOauthClientsService;
    @Resource
    ICarmenUserService iCarmenUserService;
    @Resource
    RedisOperate redisOperate;



    // 对接基础平台监控的数据推送url，配置在/properties/constant.properties
    @Value("${baseplatform.pullUrl}")
    private String pullUrl;


    /**
     * 获取API的列表
     * @param env 环境变量  1：开发，2：测试，3：生产
     * @param request ServletRequest
     * @param response ServletResponse
     * @return 正常返回apilist的视图
     */
    @RequestMapping("/apilist")
    public ModelAndView getApiList(@RequestParam(value="env", defaultValue = "1") byte env,
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
        Map<String, String> groupMap = new HashMap<>();
        try {
            carmenApi = iCarmenApiService.getRecordByEnv(env);
            if(null != carmenApi) {
                Collections.sort(carmenApi, new Comparator<CarmenApi>() {
                    @Override
                    public int compare(CarmenApi arg1, CarmenApi arg2) {
                        if(StringUtils.isEmpty(arg1.getCreateTime()) || StringUtils.isEmpty(arg2.getCreateTime())){ // 防止脏数据
                            return 0;
                        }
                        return arg2.getCreateTime().compareTo(arg1.getCreateTime()); // 按时间逆序排序
                    }
                });
            }
            List<OpenResourceGroup> groupList = iOpenResourceGroupService.getAll();
            for (OpenResourceGroup group : groupList) {
                if(StringUtils.isEmpty(group.getAlias()) || StringUtils.isEmpty(group.getName())) {
                    continue;
                }
                groupMap.put(group.getAlias(), group.getName());
            }

        } catch (Exception e) {
            logger.error("can not get api config", e);
        }
        if(null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "Fail to get user name");
        }
        Boolean isAdmin = isAdministrator(userName);
        Map<String, Object> results = new HashMap<>();
        results.put("apilists", carmenApi);
        results.put("user", userName);
        results.put("env", env);
        results.put("groupList", groupMap);
        results.put("isAdmin", isAdmin);
        return new ModelAndView("apilist", "results", results);
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
     * 按group获取API接口
     * @param group
     * @param env
     * @return
     */
    @RequestMapping(value = "/getapibygroup", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getGroup(@RequestParam("group") String group,
                    @RequestParam("env") Byte env) {

        String status = "fail";
        try {
            List<CarmenApi> carmenApiList = null;
            if("all".equals(group)) {
                carmenApiList = iCarmenApiService.getRecordByEnv(env);
            } else {
                carmenApiList = iCarmenApiService.getRecordByEnvGroup(env, group);
            }
            if(null != carmenApiList) {
                Collections.sort(carmenApiList, new Comparator<CarmenApi>() {
                    @Override
                    public int compare(CarmenApi arg1, CarmenApi arg2) {
                        if(StringUtils.isEmpty(arg1.getCreateTime()) || StringUtils.isEmpty(arg2.getCreateTime())){ // 防止脏数据
                            return 0;
                        }
                        return arg2.getCreateTime().compareTo(arg1.getCreateTime()); // 按时间逆序排序
                    }
                });
            }
            String result = JSON.toJSONString(carmenApiList);
            return result;
        } catch (Exception e) {
            logger.error("fail to get api list by group", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }


    /**
     * 根据id删除clientid 的记录
     * @param id id
     * @return 成功返回success，失败返回fail
     */
    @RequestMapping(value = "/deleteclienttable", produces="application/json;charset=utf-8")
    @ResponseBody
    public String deleteClientTable(@RequestParam("id") String id){
        String status = "success";

        try {
            iCarmenFreqConfigService.deleteById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            status = "fail";
            logger.warn("fail to delete", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }


    /**
     * 获取api参数信息
     * @param namespace 命名空间
     * @param name 方法名字
     * @param version api版本
     * @param env 环境，1：开发环境，2：测试，3：生产
     * @return String，成功返回json字符串，错误返回"fail"字符串
     */
    @RequestMapping(value = "/getapiparam", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getApiParam(@RequestParam("namespace") String  namespace,
                              @RequestParam("name") String name,
                              @RequestParam("version")  String version,
                              @RequestParam("env") byte env) {
        String status = "success";
        // 获取API的ID
        CarmenApi carmenApi = null;
        try {
            carmenApi = iCarmenApiService.getRecordByCondition(namespace, name, version, env);

            if(null != carmenApi) {
                Long apiId = carmenApi.getId();
                //根据API的id号获取参数
                List<CarmenApiParam> carmenApiParam = iCarmenApiParamService.getByApiId(apiId, env);
                String result = null;
                try {
                    result = JSON.toJSONString(carmenApiParam);
                    return result;
                } catch (Exception e) {
                    status = "fail";
                    logger.warn("fail to convert apiParams to json", e);
                }
            } else {
                status = "fail";
            }
        } catch (Exception e) {
            logger.error("fail to get apiParam.", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }

    /**
     * 测试配置好的API接口
     * @param requestUrl
     * @param requestType
     * @param params
     * @return
     */
    @RequestMapping(value = "/testapi", produces="application/json;charset=utf-8")
    @ResponseBody
    public String testApi(@RequestParam("requestUrl") String requestUrl,
                          @RequestParam("requestType") String requestType,
                          @RequestParam("params") String params) {
        String status = "fail";

        try {
            if("1".equals(requestType) && !"".equals(params)) { // 1表示POST请求
                String result = HttpUtils.doPost(requestUrl, params);
                //result = JSON.toJSONString(result);
                return result;
            } else {
                String result = HttpUtils.sendGet(requestUrl, params);
                //result = JSON.toJSONString(result);
                return result;
            }

        } catch (Exception e) {
            status = status + " info :" + e.toString() ;
            logger.error("fail to test api", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }

    /**
     * 更新API信息
     * @param updateObject 待更新或者新增的对象
     * @return String 成功返回success，失败返回fail
     */
    @RequestMapping(value = "/updateapi", produces="application/json;charset=utf-8")
    @ResponseBody
    public String updateApi(@RequestParam("updateObject") String updateObject) {

        String status = "success";
        try {
            updateObject = URLDecoder.decode(updateObject, "UTF-8");
            CarmenApi update = JSON.parseObject(updateObject, CarmenApi.class); // js传来的json装换为JAVA对象实例
            Long id = update.getId();

            if (0 != id) { // id不为0表示数据库已经存在
                update.setModifyTime(new Date());
                update.setModifier(update.getCreator());
                update.setCreateTime(null);
                iCarmenApiService.update(update);
            } else { // id为0表示数据库不存在
                update.setCreateTime(new Date());
                update.setModifyTime(new Date());
                iCarmenApiService.insert(update);
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

    /**
     * 查询resource表的内容
     * @param uri namespace和name的组合，以点号连接
     * @param version API的版本号
     * @return String 失败返回fail， 成功返回openResource的json字符串。
     */
    @RequestMapping(value = "/queryresource", produces="application/json;charset=utf-8")
    @ResponseBody
    public String queryResource(@RequestParam("uri") String uri,
                                @RequestParam("version") String version,
                                @RequestParam("apiId") String apiId) {
        String status = "fail";
        try {
            OpenResource openResource = iOpenResourceService.getByUriVersion(uri, version);
            List<OpenResourceGroup> openResourceGroupList = iOpenResourceGroupService.getAll();
            List<OpenOauthClients> openOauthClientses = iOpenOauthClientsService.getAll();
            List<CarmenFreqConfig> listValueByApi = iCarmenFreqConfigService.getListValueByApi(Long.valueOf(apiId));

            if(null != openResource) {
                Map<String, Object> results = new HashMap<>();
                results.put("openResource", openResource);
                results.put("groupAlias", openResourceGroupList);
                results.put("openOauthClientses", openOauthClientses);
                results.put("listValueByApi", listValueByApi);
                String result = JSON.toJSONString(results);
                return result;
            }
        } catch (Exception e) {
            logger.error("fail to get openResource by uri&version or convert object to json.", e);
        }

        try {
            List<OpenResourceGroup> openResourceGroupList = iOpenResourceGroupService.getAll();
            List<OpenOauthClients> openOauthClientses = iOpenOauthClientsService.getAll();
            List<CarmenFreqConfig> listValueByApi = iCarmenFreqConfigService.getListValueByApi(Long.valueOf(apiId));
            Map<String, Object> results = new HashMap<>();
            results.put("status", status);
            results.put("groupAlias", openResourceGroupList);
            results.put("openOauthClientses", openOauthClientses);
            results.put("listValueByApi", listValueByApi);
            status = JSON.toJSONString(results);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }

    /**
     * 新增client 表记录
     * @param updateArray 待更新的行
     * @param addArray 待新增的行
     * @return 成功返回success，失败返回fail
     */
    @RequestMapping(value = "/updateclienttable", produces="application/json;charset=utf-8")
    @ResponseBody
    public String updateClientTable(@RequestParam("updateArray") String updateArray,
                                    @RequestParam("addArray") String addArray) {
        String status = "success";

        try {

            List<CarmenFreqConfig> updateObjects = JSON.parseArray(updateArray, CarmenFreqConfig.class);
            List<CarmenFreqConfig> addObjects = JSON.parseArray(addArray, CarmenFreqConfig.class);

            for (CarmenFreqConfig update : updateObjects) {

                iCarmenFreqConfigService.update(update);
            }
            for (CarmenFreqConfig add : addObjects) {

                iCarmenFreqConfigService.insert(add);
            }
        } catch (Exception e) {
            status = "fail" + e.toString();
            logger.warn("fail to update", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }

    /**
     * 更新资源表
     * @param update 待更新对象
     * @return 成功返回success，失败返回fail
     */
    @RequestMapping(value = "/updateresource", produces="application/json;charset=utf-8")
    @ResponseBody
    public String updateResource(@RequestParam("update") String update) {
        String status = "fail";
        try {
            update = URLDecoder.decode(update, "UTF-8");
            OpenResource openResource = JSON.parseObject(update, OpenResource.class);

            openResource.setIsInner((byte) 0);
            openResource.setIsWrite((byte) 0);
            openResource.setUpdateTime(new Date());
            if (0 != openResource.getId()) {
                iOpenResourceService.update(openResource);
            } else {
                openResource.setCreateTime(new Date());
                iOpenResourceService.insert(openResource);

            }
            status = "success";
        } catch (Exception e) {
            status += e.toString();
            logger.error("fail to update resource.", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }

    /**
     * 在open_resource_group表中插入数据
     * @param insert 待插入
     * @return 插入成功返回success，插入失败返回fail
     */
    @RequestMapping(value = "/insertresourcegroup", produces="application/json;charset=utf-8")
    @ResponseBody
    public String insertResourceGroup(@RequestParam("insert") String insert) {
        String status = "fail";

        try {
            insert = URLDecoder.decode(insert, "UTF-8");
            OpenResourceGroup openResourceGroup = JSON.parseObject(insert, OpenResourceGroup.class);
            openResourceGroup.setCreateTime(new Date());
            iOpenResourceGroupService.insert(openResourceGroup);
            status = "success";
        } catch (Exception e) {
            status = status + e.toString();
            logger.error("fail to insert resource group.", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }

    /**
     * 更新carmenapi表的valid_flag字段
     * @param update 待更新对象
     * @return 成功返回success，失败返回fail
     */
    @RequestMapping(value = "/disableapi", produces="application/json;charset=utf-8")
    @ResponseBody
    public String disableApi(@RequestParam("update") String update) {
        String status = "fail";

        try {
            CarmenApi carmenApi = JSON.parseObject(update, CarmenApi.class);
            carmenApi.setModifyTime(new Date());
            iCarmenApiService.update(carmenApi);
            status = "success";
        } catch (Exception e) {
            logger.error("fail to disable the api.", e);
        }


        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json",e);
        }
        return status;

    }


    /**
     * 修改白名单，增加API或者删除API
     * @param operate add or delete
     * @param api api字符串
     * @return 失败返回fail， 成功返回shell中相应地返回值
     */
    @RequestMapping(value = "/modifywhitelist", produces="application/json;charset=utf-8")
    @ResponseBody
    public String modifyWhitelist(@RequestParam("operate") String operate,
                                  @RequestParam("api") String api) {
        Process process = null;

        String command = "./modifyWhiteList.sh " + operate + " " + api;
        logger.info("test current path " + System.getProperty("user.dir"));
        String result = executeShellCommand(command);
        try {
            result = JSON.toJSONString(result);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return result;

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



    /**
     * 获取access token
     * @param username 微小店的用户名
     * @param password 微小店的密码
     * @return 返回access token  或者 失败信息。
     */
    @RequestMapping(value = "/getaccesstoken", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getAccessToken(@RequestParam("username") String username,
                                 @RequestParam("password") String password) {
        String status = "fail";

        String hostIp = null;
        String ip = "127.0.0.1";
        try {
            hostIp = InetAddress.getByName("login.qima-inc.com").getHostAddress();
            ip = AppUtils.getHostAddressByConnection(hostIp, 80);
        } catch (Exception e) {
            logger.error("fail to get ip address", e);
        }
        String url = "http://" + ip + ":8091/oauth/token/";
        String client_id = "1";
        String client_secret = "832103d85bad45c495c2a82e5d1928f9";
        String grant_type = "password";
        String params = "client_id=" + client_id + "&client_secret=" + client_secret + "&grant_type=" + grant_type +
                "&username=" + username + "&password=" + password + "&type=0";
        try {
            String result = HttpUtils.doPost(url, params);
            return result;
        } catch (Exception e) {
            logger.error("fail to get access token.", e);
        }
        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }

        return status;
    }

}
