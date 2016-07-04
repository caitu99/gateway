package com.caitu99.gateway.oauth.oauthex;

public final class OAuthConstants {

    // for oauth description
    public class OAuthDescription {
        public static final String INVALID_CLIENT_DESCRIPTION = "校验团队信息失败";
        public static final String INVALID_RESPONSE_TYPE = "请求响应类型不正确";
        public static final String INVALID_ACCESS_CODE = "非法访问码";
        public static final String INVALID_GRANT_TYPE = "非法授权类型";
        public static final String INVALID_REDIRECT_URI = "跳转链接不正确";
        public static final String INVALID_USERNAME_OR_PASSWORD = "账号密码不正确";
        public static final String EXCEED_THE_MAXIMUM = "您的账户因尝试登录次数过多被锁定，请半小时后重试.";
        public static final String INVALID_REDIRECT_URI_NOT_FOUND = "授权时需要提供跳转链接";
        public static final String INVALID_AUTHORIZATION_CODE = "非法授权码";
        public static final String INVALID_TOKEN = "非法访问码";
        public static final String INVALID_TOKEN_EXPIRED = "访问码已过期";
        public static final String INVALID_FRESH_TOKEN = "非法刷新码";
        public static final String INVALID_FRESH_FREQUENCY = "重复刷新访问码，请稍后再试";
        public static final String INVALID_LOGIN_FREQUENCY = "登录太频繁，请稍后再试";
        public static final String INVALID_RESOURCE = "请求非法资源";
        public static final String RESOURCE_UNAVAILABLE = "请求资源不可用";
        public static final String INVALID_NO_SIGN = "请求没有签名";
        public static final String INVALID_TIME_STAMP = "无效的时间参数";
        public static final String INVALID_SIGN_FOR_VALID = "签名校验失败";
    }

    // for oauth response
    public class OAuthResponse {
        // for token
        public static final int INVALID_CLIENT = 5101;
        public static final int INVALID_CLIENT_NO = 5102;
        public static final int INVALID_TIME_STAMP = 5103;
        public static final int INVALID_SIGN = 5104;
        public static final int INVALID_SIGN_FOR_VALID = 5105;
        public static final int INVALID_API_METHOD = 5106;
        public static final int NO_RESOURCE = 5106;
        public static final int INVALID_CLIENT_INFO = 5108;
        public static final int NO_TOKEN = 5109;
        public static final int NO_OR_EXPIRED_TOKEN = 5110;
        public static final int INVALID_TOKEN = 5111;
        public static final int INVALID_RESOURCE = 5112;
        public static final int INVALID_FREQUENCY = 5113;
        public static final int NO_PRIVILEGE = 5114;
        public static final int INVALID_CODE = 5120;
        public static final int INVALID_REQUEST = 5121;
        public static final int INVALID_TYPE = 5122;
        public static final int NO_VCODE   = 5123;
        public static final int WRONG_VCODE = 5124;
        public static final int ENCODE_ERROR = 5125;            //编码错误
        public static final int DECODE_ERROR = 5126;        //解码错误
        public static final int WRONG_USERNOTVALID = 5127;  //企业用户不允许登录
        public static final int USERNAME_OR_PASSWORD_NULL = 5128; //用户名或密码为空
        public static final int WRONG_USERNAME_OR_PASSWORD = 5129; //用户名或密码为空
        public static final int NOT_EXIST_USER = 5130;          //用户名或密码错误
        public static final int COMM_USER_NOT_ALLOWED  = 5131;  //普通用户不允许使用账号密码登录
        public static final int INVALID_OPENID  = 5132;  //该用户未用微信登录过,不可登录
    }
}
