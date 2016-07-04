package com.caitu99.gateway.gateway;


public class Constants {

    // for token
    public static final String ACCESS_TOKEN = "access_token";
    public static final String METHOD = "method";

    // for secret
    public static final String SIGN = "sign";
    public static final String CLIENTID = "clientid";

    public static final String ST_ALL = "st_all";
    public static final String ST_PRE_PIPE = "st_pre_pipe";
    public static final String ST_CALL = "st_java_call";
    public static final String ST_JAVA_RESULT = "st_java_result";
    public static final String ST_REDIS_TOKEN = "st_redis_call";
    public static final String ST_POST = "st_post";

    public static final String ERROR_RESPONSE = "{\n\t\"code\": %d,\n\t\"message\": \"%s\",\n\t\"data\": \"\"\n}";

    public static final String CONTEXT_ACCESS_TOKEN = "access_token";
    public static final String CONTEXT_USER_ID = "user_id";
    public static final String CONTEXT_REQUEST_IP = "request_ip";
    public static final String CONTEXT_CLIENT_ID = "client_id";
    public static final String CONTEXT_CLIENT_NAME = "client_name";
    public static final String CONTEXT_CLIENT_SECRET = "client_secret";
    public static final String CONTEXT_CLIENT_TYPE = "client_type";
    public static final String CONTEXT_CLIENT_NUM = "client_num";
    public static final String CONTEXT_CLIENT_SOURCE = "client_source";
}
