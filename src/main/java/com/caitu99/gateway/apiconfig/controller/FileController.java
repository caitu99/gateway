package com.caitu99.gateway.apiconfig.controller;

import com.caitu99.gateway.apiconfig.model.CarmenApi;
import com.caitu99.gateway.apiconfig.model.CarmenApiDataMigrate;
import com.caitu99.gateway.apiconfig.model.CarmenApiMethodMapping;
import com.caitu99.gateway.apiconfig.model.CarmenServiceMethod;
import com.caitu99.gateway.apiconfig.model.CarmenUser;
import com.caitu99.gateway.apiconfig.service.ICarmenApiMethodMappingService;
import com.caitu99.gateway.apiconfig.service.ICarmenApiService;
import com.caitu99.gateway.apiconfig.service.ICarmenDataMigrateService;
import com.caitu99.gateway.apiconfig.service.ICarmenServiceMethodService;
import com.caitu99.gateway.apiconfig.service.ICarmenUserService;
import com.caitu99.gateway.cache.RedisOperate;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 接口配置信息迁移 Created by chenyun on 15/7/16.
 */
@Controller
public class FileController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FileController.class);

    @Resource
    private ICarmenServiceMethodService serviceMethodService;

    @Resource
    private ICarmenDataMigrateService dataMigrateService;

    @Resource
    private ICarmenApiMethodMappingService apiMethodMappingService;

    @Resource
    private ICarmenApiService iCarmenApiService;

    @Resource
    private RedisOperate redisOperate;

    @Resource
    private ICarmenUserService carmenUserService;

    /**
     * 导出接口配置信息
     *
     * @param ids      导出接口编号字符串
     * @param fromEnv  源环境
     * @param toEnv    目标环境
     * @param response 响应
     */
    @RequestMapping("/download")
    public void download(String ids, Integer fromEnv, Integer toEnv, HttpServletResponse response) {
        String[] idArray = ids.split(",");
        List<CarmenApiDataMigrate> dataMigrateList = new ArrayList<>();
        for (String id : idArray) {
            long idChange = Long.parseLong(id);
            CarmenApiMethodMapping mapping = apiMethodMappingService.getByApiId(idChange,
                                                                                fromEnv.byteValue());
            if (mapping != null) {
                idChange = mapping.getServiceMethodId();
            } else {
                logger.error("{}:没有对应配置信息", idChange);
            }
            CarmenServiceMethod serviceMethod = serviceMethodService.getById(idChange);
            if (serviceMethod != null) {
                CarmenApiDataMigrate dataMigrate =
                        dataMigrateService
                                .migrateData(serviceMethod.getName(), serviceMethod.getMethod(),
                                             serviceMethod.getVersion(), fromEnv.byteValue(),
                                             toEnv.byteValue());
                dataMigrateList.add(dataMigrate);
            }
        }
        String fileName = "record.xml";
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        response.setContentType("text/x-plain");
        response.setCharacterEncoding("utf-8");
        try {
            Marshaller.marshal(dataMigrateList, response.getWriter());
        } catch (Exception e) {
            logger.error("unexpected exception {}", e);
        }

        for (String id : idArray) { // 更新这批id的migrateFlag字段为2（正在发布状态）
            CarmenApi carmenApi = new CarmenApi();
            carmenApi.setId(Long.valueOf(id));
            carmenApi.setMigrateFlag((byte) 2); // 2代表待发布
            iCarmenApiService.update(carmenApi);
        }

    }


    /**
     * 查询配置信息
     *
     * @param ids      导出接口编号字符串
     * @param env      环境
     * @param response 响应
     */
    @RequestMapping("/export")
    public void export(String ids, Integer env, HttpServletResponse response) {
        String[] idArray = ids.split(",");
        List<CarmenApiDataMigrate> dataMigrateList = new ArrayList<>();
        for (String id : idArray) {
            long idChange = Long.parseLong(id);
            CarmenApiMethodMapping mapping =
                    apiMethodMappingService.getByApiId(idChange, env.byteValue());
            if (mapping != null) {
                idChange = mapping.getServiceMethodId();
            } else {
                logger.error("{}:没有对应配置信息", idChange);
            }
            CarmenServiceMethod serviceMethod = serviceMethodService.getById(idChange);
            if (serviceMethod != null) {
                CarmenApiDataMigrate dataMigrate =
                        dataMigrateService
                                .dataExport(serviceMethod.getName(), serviceMethod.getMethod(),
                                            serviceMethod.getVersion(), env.byteValue());
                dataMigrateList.add(dataMigrate);
            }
        }
        String fileName = "recordExport.xml";
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        response.setContentType("text/x-plain");
        response.setCharacterEncoding("utf-8");
        try {
            Marshaller.marshal(dataMigrateList, response.getWriter());
        } catch (Exception e) {
            logger.error("unexpected exception {}", e);
        }


    }

    /**
     * 批量删除接口
     * @param ids 接口编号字符串
     * @param env 环境
     */
    @RequestMapping("/batchdelete")
    public void batchPhysicalDelete(String ids,Integer env,HttpServletRequest request ,
                                    HttpServletResponse response){
        try {
            HttpSession session = request.getSession();
            Object userObject= session.getAttribute("username");
            String userName=null;
            if (userObject!=null) {
                String userKey = userObject.toString();
                userName = redisOperate.getStringByKey(userKey);
                List<CarmenUser> carmenUserList = carmenUserService.getByUserName(userName);
                //返回不为null而是空list
                if (carmenUserList == null || carmenUserList.size()==0) {
                    logger.error("{} is not config in carmen_user", userName);
                    return;
                } else {
                    if (!carmenUserList.get(0).getUserGroup().equals(1)) { //判断用户是否具有管理员权限,管理员group=1
                        logger.error("{} not have authority ", userName);
                        return;
                    }
                }
            }else {
                logger.error("{} is not login", userName);
                return;
            }
            String[] idArray = ids.split(",");
            List<CarmenApiDataMigrate> dataMigrateList = new ArrayList<>();
            for (String id : idArray) {
                long idChange = Long.parseLong(id);
                CarmenApiMethodMapping mapping =
                        apiMethodMappingService.getByApiId(idChange, env.byteValue());
                if (mapping != null) {
                    idChange = mapping.getServiceMethodId();
                } else {
                    logger.error("{}:没有对应配置信息", idChange);
                }
                CarmenServiceMethod serviceMethod = serviceMethodService.getById(idChange);
                if (serviceMethod != null) {//分两步处理:1.查询出全部信息 2.根据id清除所有关联数据
                    CarmenApiDataMigrate dataMigrate =
                            dataMigrateService.dataExport(serviceMethod.getName(), serviceMethod.getMethod(),
                                                          serviceMethod.getVersion(), env.byteValue());
                    dataMigrateList.add(dataMigrate);
                    dataMigrateService.physicalDataDelete(dataMigrate);
                }
            }
            String fileName = "recordDeleted.xml";
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);
            response.setContentType("text/x-plain");
            response.setCharacterEncoding("utf-8");
            Marshaller.marshal(dataMigrateList, response.getWriter());

            logger.debug("{} delete {} success",userName,ids);

        }catch (Exception e){
            logger.error("batchDelete unexpected exception {}",e);
        }
    }


    /**
     * 数据上传接口
     *
     * @param file 上传文件
     * @return 返回的界面
     */
    @RequestMapping("/upload")
    public ModelAndView upload(@RequestParam(value = "file", required = false) MultipartFile file) {
        if (null == file || "".equals(file)) {
            return new ModelAndView("upload", "results", "empty");
        }
        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            List<CarmenApiDataMigrate> dataMigrateList =
                    (List<CarmenApiDataMigrate>) Unmarshaller.unmarshal(ArrayList.class, reader);

            for (CarmenApiDataMigrate dataMigrate : dataMigrateList) {
                dataMigrateService.migrateDataImport(dataMigrate);
            }
        } catch (Exception e) {
            logger.error("unexpected exception {}", e);
            return new ModelAndView("upload", "results", "fail");
        }

        return new ModelAndView("upload", "results", "success");
    }
}
