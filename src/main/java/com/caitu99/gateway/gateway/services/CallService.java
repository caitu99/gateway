package com.caitu99.gateway.gateway.services;

import com.caitu99.gateway.AppConfig;
import com.caitu99.gateway.apiconfig.model.*;
import com.caitu99.gateway.gateway.cache.*;
import com.caitu99.gateway.gateway.exception.ValidateException;
import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.utils.RegexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class CallService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private CarmenApiCache carmenApiCache;

    @Autowired
    private CarmenApiParamCache carmenApiParamCache;

    @Autowired
    private CarmenApiMethodMappingCache carmenApiMethodMappingCache;

    @Autowired
    private CarmenParamMappingCache carmenParamMappingCache;

    @Autowired
    private CarmenServiceMethodCache carmenServiceMethodCache;


    public CarmenApi validateParams(RequestEvent event, Map<String, Object> paramsMap,
                                    Map<String, Object> filesMap, Map<String, Byte> paramsNeedMap)
            throws ExecutionException, ValidateException {

        String namespace = event.getNamespace();
        String methodName = event.getMethod();
        String version = event.getVersion();
        byte env = appConfig.env;

        CarmenApi carmenApi = carmenApiCache.get(event.getId(), namespace, methodName, version, env);

        long apiId = carmenApi.getId();

        List<CarmenApiParam> apiParamsList = carmenApiParamCache.get(event.getId(), apiId, env);

        /*
         * check parameters
         */
        for (CarmenApiParam param : apiParamsList) {
            String paraName = param.getParamName();
            String type = param.getParamType();

            byte isNeed = param.getIsRequired();
            boolean isFile = false;
            Object value;

            if (paraName.indexOf("[]") > 0) {
                if (event.getMultiFiles() != null)
                    value = event.getMultiFiles().get(paraName);
                else
                    value = null;

                isFile = true;
            } else {
                value = event.getValue(paraName);
            }

            if (value == null && isNeed == 1) { //必填项校验
                throw new ValidateException(5007, paraName + " cannot be empty");
            }
            if (value != null && type.equalsIgnoreCase("number")) { //数据类型校验,添加入参非必填的校验
                boolean numberCheck = RegexUtils.isNumber(value.toString());
                if (!numberCheck) {
                    throw new ValidateException(5008, paraName + " type error");
                }
            }
            if (isFile) {
                filesMap.put(paraName, value);
            } else {
                paramsMap.put(paraName, value);
            }
            paramsNeedMap.put(paraName, isNeed);
        }

        return carmenApi;
    }

    public void getParamsValueMap(RequestEvent event, CarmenApi carmenApi,
                                  Map<String, Object> paramsMap,
                                  Map<String, Object> filesMap,
                                  Map<String, Byte> paramsNeedMap,
                                  Map<String, Object> paramsMapChange,
                                  Map<String, Object> filesMapChange)
            throws ExecutionException, ValidateException {

        String namespace = event.getNamespace();
        String methodName = event.getMethod();
        String version = event.getVersion();
        byte env = appConfig.env;
        CarmenApiMethodMapping mapping = carmenApiMethodMappingCache.get(event.getId(), carmenApi.getId(), env);
        if (mapping == null) {
            throw new ValidateException(5009, "no ApiMethod mapping");
        }

        List<CarmenParamMapping> paramMappingList = carmenParamMappingCache.get(event.getId(), namespace, methodName, version, env);
        if (paramMappingList == null || paramMappingList.size() == 0) {
            throw new ValidateException(5010, "no parameter mapping");
        }

        /*
         * api parameter and service parameter mapping
         */
        for (CarmenParamMapping paramMapping : paramMappingList) {
            /*
                get value by parameter type, paramMapping.getDataFrom:
                    1. normal parameter
                    2. context parameter, from access token or app
                    3. default parameter, these params have default
             */

            // skip default value
            if(paramMapping.getApiParamName().equals("default"))
                continue;

            boolean isApiParam = false;
            boolean isFileParam = false;
            String apiParamName = paramMapping.getApiParamName();

            if (apiParamName.indexOf("[]") > 0) {
                isFileParam = true;
            }

            Object res;
            if (paramMapping.getDataFrom() == 2) {
                res = event.getIntParams().get(apiParamName);
            } else if (paramMapping.getDataFrom() == 3) {
                res = apiParamName;
            } else {
                if (isFileParam)
                    res = filesMap.get(apiParamName);
                else
                    res = paramsMap.get(apiParamName);
                isApiParam = true;
            }

            /*
                get parameter value, if the value is needed, then throw exception
             */
            String paramRef = paramMapping.getMethodParamRef();
            if (res != null) {
                if (isFileParam)
                    filesMapChange.put(paramRef, res);
                else
                    paramsMapChange.put(paramRef, res);
            } else {
                if (isApiParam) {
                    Byte isNeed = paramsNeedMap.get(apiParamName);
                    if (isNeed == null) {
                        throw new ValidateException(5011, apiParamName + " is not configured");
                    } else if (isNeed == 1) {
                        throw new ValidateException(5012, apiParamName + " error mapping");
                    }
                } else {
                    throw new ValidateException(5012, apiParamName + " error mapping");
                }
            }
        }
    }

    public CarmenServiceMethod getServiceMethod(UUID eventId, CarmenApi carmenApi) throws ExecutionException {
        byte env = appConfig.env;
        CarmenApiMethodMapping mapping = carmenApiMethodMappingCache.get(eventId, carmenApi.getId(), env);
        return carmenServiceMethodCache.get(eventId, mapping.getServiceMethodId());
    }

}
