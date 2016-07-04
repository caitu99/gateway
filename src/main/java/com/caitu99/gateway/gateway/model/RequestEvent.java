package com.caitu99.gateway.gateway.model;

import com.caitu99.gateway.gateway.Constants;
import com.caitu99.gateway.gateway.excutor.TicTac;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class RequestEvent {

    private UUID id = UUID.randomUUID();
    private DeferredResult<Object> result;
    private HttpServletRequest request;
    private String requestType;
    private String accessToken;
    private String sign;
    private Integer clientId; //id in Open_Oauth_Client
    private String clientName; //client_id in Open_Oauth_Client
    private String ip;
    private String namespace;
    private String method;
    private String version = "1.0";
    private long apiId;
    private Map<String, String> params = new HashMap<>();
    private Map<String, String> signParams = new HashMap<>();
    private Map<String, Object> intParams = new HashMap<>();
    private MultiValueMap<String, MultipartFile> multiFiles;
    private TicTac ticTac = new TicTac();

    private ValidateType validateType = ValidateType.NONE;
    private RequestState state = RequestState.NONE;
    private String resultStr;
    private Exception exception;

    public RequestEvent() {
    }

    public RequestEvent(DeferredResult<Object> result, HttpServletRequest request) {
        this.result = result;
        this.request = request;
        this.requestType = request.getMethod();
    }

    public UUID getId() {
        return id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getValue(String param) {
        switch (param) {
            case Constants.ACCESS_TOKEN:
                return this.getAccessToken();
            case Constants.METHOD:
                return this.getMethod();
            case Constants.SIGN:
                return this.getSign();
            case Constants.CONTEXT_REQUEST_IP:
                return this.getIp();
            default:
                String value = params.get(param);
                if (value == null) {
                    value = request.getHeader(param);
                }
                return value;
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public DeferredResult<Object> getResult() {
        return result;
    }

    public void setResult(DeferredResult<Object> result) {
        this.result = result;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, Object> getIntParams() {
        return intParams;
    }

    public void setIntParams(Map<String, Object> intParams) {
        this.intParams = intParams;
    }

    public MultiValueMap<String, MultipartFile> getMultiFiles() {
        return multiFiles;
    }

    public void setMultiFiles(MultiValueMap<String, MultipartFile> multiFiles) {
        this.multiFiles = multiFiles;
    }

    public String getResultStr() {
        return resultStr;
    }

    public void setResultStr(String resultStr) {
        this.resultStr = resultStr;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public long getApiId() {
        return apiId;
    }

    public void setApiId(long apiId) {
        this.apiId = apiId;
    }

    public ValidateType getValidateType() {
        return validateType;
    }

    public void setValidateType(ValidateType validateType) {
        this.validateType = validateType;
    }

    public RequestState getState() {
        return state;
    }

    public void setState(RequestState state) {
        this.state = state;
    }

    public TicTac getTicTac() {
        return ticTac;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Map<String, String> getSignParams() {
        return signParams;
    }

    public void setSignParams(Map<String, String> signParams) {
        this.signParams = signParams;
    }

    @Override
    public String toString() {
        return "[ " + id + " " + requestType + " " + this.getNamespace() + " " + this.getMethod() + " " + this.getVersion() + " " + " ] {" +
                "clientId='" + clientId + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", sign='" + sign + '\'' +
                ", params=" + params +
                //", ticTac=" + ticTac +
                ", validateType=" + validateType +
                ", requestState=" + state +
                //", resultStr='" + resultStr + '\'' +
                ", exception=" + (exception == null ? "" : exception.toString()) +
                ", clientIp = '" + (getIp() == null ? "" : getIp()) + '\'' +
                '}';
    }

}
