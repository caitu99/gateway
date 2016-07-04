package com.caitu99.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSON;
import com.caitu99.gateway.apiconfig.model.*;
import com.caitu99.gateway.apiconfig.service.*;
import com.caitu99.gateway.cache.RedisOperate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by dingdongsheng on 15/9/5.
 */

@Controller
public class CreateApiController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(CreateApiController.class);

    // Resource 默认按照名称进行装配
    @Resource
    ICarmenApiService iCarmenApiService;
    @Resource
    ICarmenServiceMethodService iCarmenServiceMethodService;
    @Resource
    ICarmenApiMethodMappingService iCarmenApiMethodMappingService;
    @Resource
    ICarmenParamMappingService iCarmenParamMappingService;
    @Resource
    ICarmenStructService iCarmenStructService;
    @Resource
    ICarmenServiceMethodParamService iCarmenServiceMethodParamService;
    @Resource
    ICarmenApiParamService iCarmenApiParamService;
    @Resource
    ICarmenDataMigrateService icarmenDataMigrateService;
    @Resource
    ICarmenUserService iCarmenUserService;
    @Resource
    RedisOperate redisOperate;
    @Resource
    IOpenResourceGroupService iOpenResourceGroupService;


    /**
     * 新建API
     * @param env 环境变量  1：开发，2：测试，3：生产
     * @param edit 默认值0表示此次是新建任务，1表示此次是编辑已有任务
     * @return 返回页面
     */
    @RequestMapping("/createapi")
    public ModelAndView createApi(@RequestParam(value = "env", defaultValue="1") Byte env,
                                  @RequestParam("group") String group,
                                  @RequestParam(value = "edit", required = false, defaultValue = "0") Integer edit,
                                  @RequestParam(value = "apiId", required = false) Long apiId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        String userName = null;
        try {
            String userKey = request.getSession().getAttribute("username").toString();
            userName = redisOperate.getStringByKey(userKey);
            redisOperate.set("username", userName); // 一小时
        } catch (Exception e) {
            logger.warn("fail to get session", e);
        }
        if(null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "Fail to get user name");
        }

        Map<String, Object> results = new HashMap<>();
        results.put("user", userName);
        results.put("edit","0");
        results.put("apiType","1");
        results.put("env", env);
        List<OpenResourceGroup> openResourceGroupList = new ArrayList<>();
        try {
            openResourceGroupList = iOpenResourceGroupService.getAll();
        } catch (Exception e) {
            logger.info("fail to get group info", e);
        }
        results.put("grouplist", openResourceGroupList);
        if(1 == edit) {
            CarmenApiMethodMapping carmenApiMethodMapping = null;
            try {
                carmenApiMethodMapping = iCarmenApiMethodMappingService.getByApiId(apiId, env);

                if(null == carmenApiMethodMapping) { // 如果找不到直接跳转到错误页面
                    return new ModelAndView("redirect:/unifyerror", "cause", "Fail to get methodId by apiId.");
                }
                Long methodId = carmenApiMethodMapping.getServiceMethodId();
                CarmenServiceMethod carmenServiceMethod = iCarmenServiceMethodService.getById(methodId);
                if(null == carmenServiceMethod) {
                    return new ModelAndView("redirect:/unifyerror", "cause", "Fail to get method object by methodId.");
                }
                String service = carmenServiceMethod.getName();
                String method = carmenServiceMethod.getMethod();
                String version = carmenServiceMethod.getVersion();
                CarmenApiDataMigrate carmenApiDataMigrate =  icarmenDataMigrateService.dataExport(service, method, version, env);
                results.put("api", carmenApiDataMigrate.getApi());
                if(2 == carmenApiDataMigrate.getApi().getApiType()) { // 2表示PHP
                    results.put("apiType","2");
                }
                // 对方法参数排序
                List<CarmenServiceMethodParam> methodParamLists = carmenApiDataMigrate.getServiceMethodParamList();
                Collections.sort(methodParamLists, new Comparator<CarmenServiceMethodParam>() {
                    @Override
                    public int compare(CarmenServiceMethodParam arg0, CarmenServiceMethodParam arg1) {
                        if(StringUtils.isEmpty(arg0.getSequence()) || StringUtils.isEmpty(arg1.getSequence())) {
                            return 0;
                        }
                        return arg0.getSequence() - arg1.getSequence();
                    }
                });
                // 对API参数排序
                List<CarmenApiParam> apiParamList = carmenApiDataMigrate.getApiParamList();
                Collections.sort(apiParamList, new Comparator<CarmenApiParam>() {
                    @Override
                    public int compare(CarmenApiParam arg0, CarmenApiParam arg1) {
                        if(StringUtils.isEmpty(arg0.getSequence()) || StringUtils.isEmpty(arg1.getSequence())) {
                            return 0;
                        }
                        return arg0.getSequence() - arg1.getSequence();
                    }
                });
                // 对参数参数映射排序
                List<CarmenParamMapping> paramMappingList = carmenApiDataMigrate.getParamMappingList();
                Collections.sort(paramMappingList, new Comparator<CarmenParamMapping>() {
                    @Override
                    public int compare(CarmenParamMapping arg0, CarmenParamMapping arg1) {
                        if(StringUtils.isEmpty(arg0.getSequence()) || StringUtils.isEmpty(arg1.getSequence())) {
                            return 0;
                        }
                        return arg0.getSequence() - arg1.getSequence();
                    }
                });
                results.put("apiParamList", carmenApiDataMigrate.getApiParamList());
                results.put("method", carmenApiDataMigrate.getServiceMethod());
                results.put("methodParamList", methodParamLists);
                results.put("structure", carmenApiDataMigrate.getStructList());
                results.put("methodMapping", carmenApiDataMigrate.getApiMethodMapping());
                results.put("paramMapping", carmenApiDataMigrate.getParamMappingList());
                results.put("edit","1");
                Boolean isAdmin = isAdministrator(userName);
                results.put("isAdmin", isAdmin);
                results.put("group", group);
                return new ModelAndView("createapi", "results", results);
            } catch (Exception e) {
                logger.error("fail to get api when status is edited.", e);
            }

        }

        Boolean isAdmin = isAdministrator(userName);
        results.put("isAdmin", isAdmin);
        results.put("group", group);
        return new ModelAndView("createapi", "results", results);
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
     * 查询method映射是否存在
     * @param apiNamespace API命名空间
     * @param apiName API名字
     * @param apiVersion API版本
     * @param namespace 方法命名空间
     * @param name 方法名
     * @param version 方法版本
     * @param env 环境，1：开发环境，2：测试，3：生产
     * @return 有映射返回success，没有映射返回fail
     */
    @RequestMapping(value = "/checkapimethodmapping", produces="application/json;charset=utf-8")
    @ResponseBody
    public String checkApiMethodMapping(@RequestParam("apiNamespace") String  apiNamespace,
                                        @RequestParam("apiName") String apiName,
                                        @RequestParam("apiVersion")  String apiVersion,
                                        @RequestParam("namespace") String  namespace,
                                        @RequestParam("name") String name,
                                        @RequestParam("version")  String version,
                                        @RequestParam("env") byte env) {

        String result = "fail";
        try {
            CarmenApi carmenApi = iCarmenApiService.getRecordByCondition(apiNamespace,apiName,apiVersion,env);
            CarmenServiceMethod carmenServiceMethod = iCarmenServiceMethodService.getRecordByCondition(namespace,name, version, env);
            if(null != carmenApi && null !=carmenServiceMethod) { // 前面两步已经配置好信息才能继续
                Long apiId = carmenApi.getId();
                Long methodId = carmenServiceMethod.getId();
                CarmenApiMethodMapping mappingId = iCarmenApiMethodMappingService.getByIds(apiId, methodId, env);
                if(null != mappingId) {
                    result = "success";
                }
            }
        } catch (Exception e) {
            logger.error("fail to check the ");
        }


        try {
            result = JSON.toJSONString(result);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return result;
    }



    /**
     * 获取method信息
     * @param namespace 服务名
     * @param name 方法名
     * @param version api版本
     * @param env 环境，1：开发环境，2：测试，3：生产
     * @return String 失败返回"fail", 成功返回对象的json串。
     */
    @RequestMapping(value = "/getapimethod", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getApiMethod(@RequestParam("namespace") String  namespace,
                               @RequestParam("name") String name,
                               @RequestParam("version")  String version,
                               @RequestParam("env") byte env) {
        String status = "fail";

        try {
            CarmenServiceMethod carmenServiceMethod = iCarmenServiceMethodService.getRecordByCondition(namespace,name, version, env);
            if(null != carmenServiceMethod) {
                String result = JSON.toJSONString(carmenServiceMethod);
                return result;
            }
        } catch (Exception e) {
            logger.warn("fail to convert method result to json", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }




    @RequestMapping(value = "/saveResult", produces="application/text;charset=utf-8")
    @ResponseBody
    public String saveResult(@RequestParam("parseApiResult") String parseApiResult,
                             @RequestParam("apiParamUpdate") String apiParamUpdate,
                             @RequestParam("apiParamAdd") String apiParamAdd,
                             @RequestParam("methodResult") String methodResult,
                             @RequestParam("methodParamUpdate") String methodParamUpdate,
                             @RequestParam("methodParamAdd") String methodParamAdd,
                             @RequestParam("structureUpdate") String structureUpdate,
                             @RequestParam("structureAdd") String structureAdd,
                             @RequestParam("paramMappingUpdate") String paramMappingUpdate,
                             @RequestParam("paramMappingAdd") String paramMappingAdd,
                             @RequestParam("methodMappingId") String methodMappingId,
                             @RequestParam("env") Byte env) {

        String status = "success";

        try {
            saveAllData( parseApiResult, apiParamUpdate, apiParamAdd, methodResult, methodParamUpdate, methodParamAdd, structureUpdate, structureAdd, paramMappingUpdate, paramMappingAdd, methodMappingId, env);
        } catch (Exception e) {
            status = e.toString();
            logger.error("fail to save all data.", e);
        }
        return status;
    }
    @Transactional(value = "txManagerCarmen",rollbackFor=Exception.class)
    public void saveAllData(String parseApiResult,
                            String apiParamUpdate,
                            String apiParamAdd,
                            String methodResult,
                            String methodParamUpdate,
                            String methodParamAdd,
                            String structureUpdate,
                            String structureAdd,
                            String paramMappingUpdate,
                            String paramMappingAdd,
                            String methodMappingId,
                            Byte env) {
        try {
            parseApiResult = URLDecoder.decode(parseApiResult, "UTF-8");
            apiParamUpdate = URLDecoder.decode(apiParamUpdate, "UTF-8");
            apiParamAdd = URLDecoder.decode(apiParamAdd, "UTF-8");
            methodResult = URLDecoder.decode(methodResult, "UTF-8");
            methodParamUpdate = URLDecoder.decode(methodParamUpdate, "UTF-8");
            methodParamAdd = URLDecoder.decode(methodParamAdd, "UTF-8");
            structureUpdate = URLDecoder.decode(structureUpdate, "UTF-8");
            paramMappingAdd = URLDecoder.decode(paramMappingAdd, "UTF-8");
            paramMappingUpdate = URLDecoder.decode(paramMappingUpdate, "UTF-8");
            paramMappingUpdate = URLDecoder.decode(paramMappingUpdate, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.info("fail to decode params", e);
        }
        CarmenApi carmenApi = JSON.parseObject(parseApiResult, CarmenApi.class);
        List<CarmenApiParam> carmenApiParamUpdate = "".equals(apiParamUpdate) ? new ArrayList<>() : JSON.parseArray(apiParamUpdate, CarmenApiParam.class); // 如果输入为空串，空列表
        List<CarmenApiParam> carmenApiParamAdd = "".equals(apiParamAdd) ? new ArrayList<>() : JSON.parseArray(apiParamAdd, CarmenApiParam.class); // 如果输入为空串，空列表

        CarmenServiceMethod carmenServiceMethod = JSON.parseObject(methodResult, CarmenServiceMethod.class);
        List<CarmenServiceMethodParam> carmenServiceMethodUpdate = "".equals(methodParamUpdate) ? new ArrayList<>() : JSON.parseArray(methodParamUpdate, CarmenServiceMethodParam.class); // 如果输入为空串，空列表
        List<CarmenServiceMethodParam> carmenServiceMethodAdd = "".equals(methodParamAdd) ? new ArrayList<>() : JSON.parseArray(methodParamAdd, CarmenServiceMethodParam.class); // 如果输入为空串，空列表

        List<CarmenStruct> carmenStructUpdate = "".equals(structureUpdate) ? new ArrayList<>() : JSON.parseArray(structureUpdate, CarmenStruct.class);
        List<CarmenStruct> carmenStructAdd = "".equals(structureAdd) ? new ArrayList<>() : JSON.parseArray(structureAdd, CarmenStruct.class);

        List<CarmenParamMapping> carmenParamMappingUpdate = "".equals(paramMappingUpdate) ? new ArrayList<>() : JSON.parseArray(paramMappingUpdate, CarmenParamMapping.class);
        List<CarmenParamMapping> carmenParamMappingAdd = "".equals(paramMappingAdd) ? new ArrayList<>() : JSON.parseArray(paramMappingAdd, CarmenParamMapping.class);


        // 1. 更新或者插入API配置信息
        Long id = carmenApi.getId();
//        if(2 == carmenApi.getApiType()) { // 如果配置的PHP接口，appName与namespace一致
//            carmenApi.setAppName(carmenApi.getNamespace());
//        }
        Long apiId = 0L;
        if(0 != id) { // id不为0表示数据库已经存在
            apiId = carmenApi.getId();
            carmenApi.setModifyTime(new Date());
            carmenApi.setModifier(carmenApi.getCreator());
            carmenApi.setCreateTime(null);
            carmenApi.setEnv(env);
            iCarmenApiService.update(carmenApi);
        } else { // id为0表示数据库不存在
            carmenApi.setCreateTime(new Date());
            carmenApi.setModifyTime(new Date());
            carmenApi.setEnv(env);
            apiId = iCarmenApiService.insert(carmenApi);
        }

        // 2. 更新或者插入API的参数
        for (CarmenApiParam update : carmenApiParamUpdate) {
            update.setModifier(update.getCreator());
            update.setModifyTime(new Date());
            update.setCreator(null);
            update.setApiId(apiId); // 塞进API id
            update.setEnv(env);
            iCarmenApiParamService.update(update);
        }
        for (CarmenApiParam add : carmenApiParamAdd) {
            add.setCreateTime(new Date());
            add.setModifyTime(new Date());
            add.setApiId(apiId); // 塞进API id
            add.setEnv(env);
            iCarmenApiParamService.insert(add);
        }

        // 3. 更新方法配置
        Long methodId = carmenServiceMethod.getId();
        Long newMethodId = 0L;
        if(0 != methodId) { // id不为0表示是数据库已有的数据，只需更新
            newMethodId = carmenServiceMethod.getId();
            carmenServiceMethod.setModifyTime(new Date());
            carmenServiceMethod.setModifier(carmenServiceMethod.getCreator());
            carmenServiceMethod.setCreator(null);
            carmenServiceMethod.setEnv(env);
            iCarmenServiceMethodService.update(carmenServiceMethod);
        } else {
            carmenServiceMethod.setCreateTime(new Date());
            carmenServiceMethod.setModifyTime(new Date());
            carmenServiceMethod.setEnv(env);
            newMethodId = iCarmenServiceMethodService.insert(carmenServiceMethod);
        }

        // 4. 更新方法参数
        for (CarmenServiceMethodParam update : carmenServiceMethodUpdate) {
            update.setModifier(update.getCreator());
            update.setModifyTime(new Date());
            update.setCreator(null);
            update.setServiceMethodId(newMethodId); // 塞进methodId
            update.setEnv(env);
            iCarmenServiceMethodParamService.update(update);
        }
        for (CarmenServiceMethodParam add : carmenServiceMethodAdd) {
            add.setCreateTime(new Date());
            add.setModifyTime(new Date());
            add.setServiceMethodId(newMethodId); // 塞进methodId
            add.setEnv(env);
            iCarmenServiceMethodParamService.insert(add);
        }

        // 5. 更新结构
        for (CarmenStruct update : carmenStructUpdate) {
            update.setModifier(update.getCreator());
            update.setModifyTime(new Date());
            update.setCreator(null);
            update.setServiceMethodId(newMethodId);
            update.setEnv(env);
            iCarmenStructService.update(update);
        }
        for (CarmenStruct add : carmenStructAdd) {
            add.setCreateTime(new Date());
            add.setModifyTime(new Date());
            add.setServiceMethodId(newMethodId);
            add.setEnv(env);
            iCarmenStructService.insert(add);
        }

        // 6. 更新方法映射
        if(0 == id) {  // 如果API的id为0，表示插入新的映射关系
            CarmenApiMethodMapping insert = new CarmenApiMethodMapping();
            insert.setCreateTime(new Date());
            insert.setApiId(apiId);
            insert.setServiceMethodId(newMethodId);
            insert.setCreator(carmenApi.getCreator());
            insert.setEnv(env);
            iCarmenApiMethodMappingService.insert(insert);
        } else {  // 更新映射关系
            CarmenApiMethodMapping update = new CarmenApiMethodMapping();
            update.setCreateTime(new Date());
            update.setCreator(carmenApi.getCreator());
            update.setEnv(env);
            update.setId(Long.valueOf(methodMappingId));
            iCarmenApiMethodMappingService.update(update);
        }


        // 7. 更新参数映射
        for (CarmenParamMapping update : carmenParamMappingUpdate) {
            update.setModifier(update.getCreator());
            update.setModifyTime(new Date());
            update.setCreator(null);
            //从API配置与方法配置中获取值
            update.setServiceMethodId(newMethodId);
            update.setApiNamespace(carmenApi.getNamespace());
            update.setApiName(carmenApi.getName());
            update.setVersion(carmenApi.getVersion());
            update.setEnv(env);
            iCarmenParamMappingService.update(update);
        }
        for (CarmenParamMapping add : carmenParamMappingAdd) {
            add.setCreateTime(new Date());
            add.setModifyTime(new Date());
            //从API配置与方法配置中获取值
            add.setServiceMethodId(newMethodId);
            add.setApiNamespace(carmenApi.getNamespace());
            add.setApiName(carmenApi.getName());
            add.setVersion(carmenApi.getVersion());
            add.setEnv(env);
            iCarmenParamMappingService.insert(add);
        }

    }



    /**
     * 根据ID删除API Param
     * @param id API Param表的id号
     * @return 成功返回success， 失败返回fail
     */
    @RequestMapping(value = "/deleteapiparam", produces="application/json;charset=utf-8")
    @ResponseBody
    public  String deleteApiParam(@RequestParam("id") String id) {
        String status = "success";

        try {
            iCarmenApiParamService.deleteById(Long.valueOf(id));
        } catch (Exception e) {
            logger.warn("fail to delete api param" , e);
            status = "fail";
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }

    /**
     * 根据ID删除API方法记录
     * @param id api method 的id
     * @return String 成功返回success, 失败返回fail
     */
    @RequestMapping(value = "/deleteapimethod", produces="application/json;charset=utf-8")
    @ResponseBody
    public String deleteApiMethod(@RequestParam("id") String id) {
        String status = "success";
        try {
            iCarmenServiceMethodService.deleteById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            status = "fail";
            logger.warn("fail to delete apimethod", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }

    /**
     * 删除方法的参数
     * @param id 方法参数配置表的id
     * @return String 成功返回success，失败返回fail
     */
    @RequestMapping(value = "/deleteapimethodparam", produces="application/json;charset=utf-8")
    @ResponseBody
    public String deleteApiMethodParam(@RequestParam("id") String id) {
        String status = "success";
        try {
            iCarmenServiceMethodParamService.deleteById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            status = "fail";
            logger.warn("fail to delete apimethod", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }

    /**
     * 删除结构信息
     * @param id 结构表的id
     * @return 成功返回 success，失败返回fail
     */
    @RequestMapping(value = "/deleteapistructure", produces="application/json;charset=utf-8")
    @ResponseBody
    public String deleteApiStructure( @RequestParam("id") String id) {
        String status = "success";
        try {
            iCarmenStructService.deleteById(Long.valueOf(id));
        } catch (NumberFormatException e) {
            status = "fail";
            logger.warn("fail to delete apimethod", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }

    /**
     * 根据id删除param映射关系
     * @param id 参数映射表的id
     * @return 成功返回success，失败返回fail
     */
    @RequestMapping(value = "/deleteapiparammapping", produces="application/json;charset=utf-8")
    @ResponseBody
    public String deleteApiParamMapping(@RequestParam("id") String id) {
        String status = "success";
        try {
            iCarmenParamMappingService.deleteById(Long.valueOf(id));
        } catch (Exception e) {
            status = "fail";
            logger.warn("fail to delete param mapping.", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }
        return status;
    }
}
