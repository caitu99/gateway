package com.caitu99.gateway.apiconfig.service.imp;

/**
 * Created by chenyun on 15/7/9.
 */

import com.caitu99.gateway.apiconfig.model.*;
import com.caitu99.gateway.apiconfig.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;

/**
 * 数据迁移工具
 */
@Service
public class CarmenDataMigrateService implements ICarmenDataMigrateService {

    private static final Logger logger = LoggerFactory.getLogger(CarmenDataMigrateService.class);

    @Resource
    private ICarmenApiService apiService;
    @Resource
    private ICarmenApiParamService apiParamService;
    @Resource
    private ICarmenServiceMethodService serviceMethodService;
    @Resource
    private ICarmenServiceMethodParamService serviceMethodParamService;
    @Resource
    private ICarmenStructService structService;
    @Resource
    private ICarmenParamMappingService paramMappingService;
    @Resource
    private ICarmenApiMethodMappingService apiMethodMappingService;


    /**
     * 迁移一个内部服务的数据拷贝
     */
    @Override
    public CarmenApiDataMigrate migrateData(String service, String serviceMethod, String version,
                                            byte fromEnv,
                                            byte toEnv) {
        CarmenApiDataMigrate dataMigrate = new CarmenApiDataMigrate();
        long start1 = System.currentTimeMillis();
        CarmenServiceMethod carmenServiceMethod =
                serviceMethodService.getRecordByCondition(service, serviceMethod, version, fromEnv);
        long end1 = System.currentTimeMillis();
        logger.debug("serviceMethodService begin:{} end:{} cost{}", start1, end1, end1 - start1);
        carmenServiceMethod.setEnv(toEnv);
        dataMigrate.setServiceMethod(carmenServiceMethod);

        long serviceMethodId = carmenServiceMethod.getId();

        long start2 = System.currentTimeMillis();
        CarmenApiMethodMapping apiMethodMapping =
                apiMethodMappingService.getByServiceMethodId(serviceMethodId, fromEnv);
        long end2 = System.currentTimeMillis();

        logger.debug("apiMethodMappingService begin:{} end:{} cost{}", start2, end2, end2 - start2);

        long apiId = apiMethodMapping.getApiId();

        apiMethodMapping.setEnv(toEnv);
        dataMigrate.setApiMethodMapping(apiMethodMapping);

        long start3 = System.currentTimeMillis();
        CarmenApi api = apiService.getById(apiId);
        long end3 = System.currentTimeMillis();
        logger.debug("apiService begin:{} end:{} cost{}", start3, end3, end3 - start3);

        api.setEnv(toEnv);
        dataMigrate.setApi(api);

        long start4 = System.currentTimeMillis();

        List<CarmenApiParam> apiParamList = apiParamService.getByApiId(apiId, fromEnv);

        long end4 = System.currentTimeMillis();

        logger.debug("apiParamService begin:{} end:{} cost{}", start4, end4, end4 - start4);

        for (CarmenApiParam apiParam : apiParamList) {
            apiParam.setEnv(toEnv);
        }
        dataMigrate.setApiParamList(apiParamList);

        long start5 = System.currentTimeMillis();
        List<CarmenServiceMethodParam> serviceMethodParamList =
                serviceMethodParamService.getByServiceMethodId(serviceMethodId, fromEnv);
        long end5 = System.currentTimeMillis();

        logger.debug("serviceMethodParamService begin:{} end:{} cost{}", start5, end5,
                     end5 - start5);

        for (CarmenServiceMethodParam serviceMethodParam : serviceMethodParamList) {
            serviceMethodParam.setEnv(toEnv);
        }
        dataMigrate.setServiceMethodParamList(serviceMethodParamList);

        long start6 = System.currentTimeMillis();
        List<CarmenStruct> structList =
                structService.getByServiceMethodId(serviceMethodId, fromEnv);
        long end6 = System.currentTimeMillis();
        logger.debug("structService begin:{} end:{} cost{}", start6, end6, end6 - start6);

        for (CarmenStruct struct : structList) {
            struct.setEnv(toEnv);
        }
        dataMigrate.setStructList(structList);

        long start7 = System.currentTimeMillis();
        List<CarmenParamMapping> paramMappingList =
                paramMappingService.getByServiceMethodId(serviceMethodId, fromEnv);
        long end7 = System.currentTimeMillis();
        logger.debug("paramMappingService begin:{} end:{} cost{}", start7, end7, end7 - start7);

        for (CarmenParamMapping paramMapping : paramMappingList) {
            paramMapping.setEnv(toEnv);
        }
        dataMigrate.setParamMappingList(paramMappingList);

        return dataMigrate;
    }

    @Override
    @Transactional(value = "txManagerCarmen", rollbackFor = Exception.class)
    public void migrateDataImport(CarmenApiDataMigrate dataMigrate) {

        CarmenApi api = dataMigrate.getApi();
        api.setId(null);
        long apiId = apiService.insert(api);
        List<CarmenApiParam> apiParamList = dataMigrate.getApiParamList();
        if (apiParamList != null) {
            for (CarmenApiParam apiParam : apiParamList) {
                apiParam.setId(null);
                apiParam.setApiId(apiId);
            }
            apiParamService.batchSave(apiParamList);
        }

        CarmenServiceMethod serviceMethod = dataMigrate.getServiceMethod();
        serviceMethod.setId(null);
        long serviceMethodId = serviceMethodService.insert(serviceMethod);
        List<CarmenServiceMethodParam>
                serviceMethodParamList =
                dataMigrate.getServiceMethodParamList();
        if (serviceMethodParamList != null) {
            for (CarmenServiceMethodParam serviceMethodParam : serviceMethodParamList) {
                serviceMethodParam.setId(null);
                serviceMethodParam.setServiceMethodId(serviceMethodId);
            }
            serviceMethodParamService.batchSave(serviceMethodParamList);
        }

        /**
         * 方法映射
         */
        CarmenApiMethodMapping apiMethodMapping = dataMigrate.getApiMethodMapping();
        apiMethodMapping.setId(null);
        apiMethodMapping.setApiId(apiId);
        apiMethodMapping.setServiceMethodId(serviceMethodId);
        apiMethodMappingService.insert(apiMethodMapping);

        List<CarmenStruct> structList = dataMigrate.getStructList();
        if (structList != null) {
            for (CarmenStruct struct : structList) {
                struct.setId(null);
                struct.setServiceMethodId(serviceMethodId);
            }
            structService.batchSave(structList);

        }

        List<CarmenParamMapping> paramMappingList = dataMigrate.getParamMappingList();
        if (paramMappingList != null) {
            for (CarmenParamMapping paramMapping : paramMappingList) {
                paramMapping.setId(null);
                paramMapping.setServiceMethodId(serviceMethodId);
            }
            paramMappingService.batchSave(paramMappingList);
        }


    }

    @Override
    public CarmenApiDataMigrate dataExport(String service, String serviceMethod, String version,
                                           byte env) {
        CarmenApiDataMigrate dataMigrate = new CarmenApiDataMigrate();
        long start1 = System.currentTimeMillis();
        CarmenServiceMethod carmenServiceMethod =
                serviceMethodService.getRecordByCondition(service, serviceMethod, version, env);
        long end1 = System.currentTimeMillis();
        logger.debug("serviceMethodService begin:{} end:{} cost{}", start1, end1, end1 - start1);
        dataMigrate.setServiceMethod(carmenServiceMethod);

        long serviceMethodId = carmenServiceMethod.getId();

        long start2 = System.currentTimeMillis();
        CarmenApiMethodMapping apiMethodMapping =
                apiMethodMappingService.getByServiceMethodId(serviceMethodId, env);
        long end2 = System.currentTimeMillis();

        logger.debug("apiMethodMappingService begin:{} end:{} cost{}", start2, end2, end2 - start2);

        long apiId = apiMethodMapping.getApiId();

        dataMigrate.setApiMethodMapping(apiMethodMapping);

        long start3 = System.currentTimeMillis();
        CarmenApi api = apiService.getById(apiId);
        long end3 = System.currentTimeMillis();
        logger.debug("apiService begin:{} end:{} cost{}", start3, end3, end3 - start3);

        dataMigrate.setApi(api);

        long start4 = System.currentTimeMillis();

        List<CarmenApiParam> apiParamList = apiParamService.getByApiId(apiId, env);

        long end4 = System.currentTimeMillis();

        logger.debug("apiParamService begin:{} end:{} cost{}", start4, end4, end4 - start4);

        dataMigrate.setApiParamList(apiParamList);

        long start5 = System.currentTimeMillis();
        List<CarmenServiceMethodParam> serviceMethodParamList =
                serviceMethodParamService.getByServiceMethodId(serviceMethodId, env);
        long end5 = System.currentTimeMillis();

        logger.debug("serviceMethodParamService begin:{} end:{} cost{}", start5, end5,
                     end5 - start5);

        dataMigrate.setServiceMethodParamList(serviceMethodParamList);

        long start6 = System.currentTimeMillis();
        List<CarmenStruct> structList =
                structService.getByServiceMethodId(serviceMethodId, env);
        long end6 = System.currentTimeMillis();
        logger.debug("structService begin:{} end:{} cost{}", start6, end6, end6 - start6);

        dataMigrate.setStructList(structList);

        long start7 = System.currentTimeMillis();
        List<CarmenParamMapping> paramMappingList =
                paramMappingService.getByServiceMethodId(serviceMethodId, env);
        long end7 = System.currentTimeMillis();
        logger.debug("paramMappingService begin:{} end:{} cost{}", start7, end7, end7 - start7);

        dataMigrate.setParamMappingList(paramMappingList);

        return dataMigrate;
    }

    /**
     * 删除一个接口的全部关联信息
     * 清理7个表数据
     * @param dataMigrate
     */
    @Override
    @Transactional(value = "txManagerCarmen", rollbackFor = Exception.class)
    public void physicalDataDelete(CarmenApiDataMigrate dataMigrate) {
        {

            CarmenApi api = dataMigrate.getApi();
            if (api!=null) {
                long apiId=api.getId();
                apiService.physicalDelete(apiId);
            }

            List<CarmenApiParam> apiParamList = dataMigrate.getApiParamList();
            if (apiParamList != null) {
                for (CarmenApiParam apiParam : apiParamList) {
                    long apiParamId=apiParam.getId();
                    apiParamService.physicalDelete(apiParamId);
                }
            }

            CarmenServiceMethod serviceMethod = dataMigrate.getServiceMethod();
            if (serviceMethod!=null) {
                long serviceMethodId=serviceMethod.getId();
                serviceMethodService.physicalDelete(serviceMethodId);
            }


            List<CarmenServiceMethodParam> serviceMethodParamList = dataMigrate.getServiceMethodParamList();

            if (serviceMethodParamList != null) {
                for (CarmenServiceMethodParam serviceMethodParam : serviceMethodParamList) {
                    long serviceMethodParamId=serviceMethodParam.getId();
                    serviceMethodParamService.physicalDelete(serviceMethodParamId);
                }
            }

            /**
             * 方法映射
             */
            CarmenApiMethodMapping apiMethodMapping = dataMigrate.getApiMethodMapping();
            if (apiMethodMapping!=null) {
                long apiMethodMappingId=apiMethodMapping.getId();
                apiMethodMappingService.physicalDelete(apiMethodMappingId);
            }


            List<CarmenStruct> structList = dataMigrate.getStructList();
            if (structList != null) {
                for (CarmenStruct struct : structList) {
                    long structId=struct.getId();
                    structService.physicalDelete(structId);
                }

            }

            List<CarmenParamMapping> paramMappingList = dataMigrate.getParamMappingList();
            if (paramMappingList != null) {
                for (CarmenParamMapping paramMapping : paramMappingList) {
                    long paramMappingId=paramMapping.getId();
                    paramMappingService.physicalDelete(paramMappingId);
                }
            }


        }
    }
}
