package com.caitu99.gateway.apiconfig.service;


import com.caitu99.gateway.apiconfig.model.CarmenApiDataMigrate;

/**
 * Created by chenyun on 15/7/14.
 */
public interface ICarmenDataMigrateService {

    /**
     * 导出接口配置信息
     */
    CarmenApiDataMigrate migrateData(String service, String serviceMethod, String version,
                                     byte fromEnv,
                                     byte toEnv);

    /**
     * 导入配置信息
     */
    void migrateDataImport(CarmenApiDataMigrate dataMigrate);


    /**
     * 一次性导出信息
     */
    CarmenApiDataMigrate dataExport(String service, String serviceMethod, String version,
                                    byte env);

    /**
     * 删除一个接口的全部关联信息
     * @param dataMigrate
     */
    void physicalDataDelete(CarmenApiDataMigrate dataMigrate);

}
