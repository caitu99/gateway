package com.caitu99.gateway.gateway.pipes.impl;

import com.caitu99.gateway.apiconfig.model.CarmenApi;
import com.caitu99.gateway.apiconfig.model.CarmenServiceMethod;
import com.caitu99.gateway.exception.CarmenException;
import com.caitu99.gateway.gateway.Constants;
import com.caitu99.gateway.gateway.exception.CallException;
import com.caitu99.gateway.gateway.exception.PipelineException;
import com.caitu99.gateway.gateway.excutor.PerfMonitor;
import com.caitu99.gateway.gateway.excutor.Pipeline;
import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.gateway.model.RequestState;
import com.caitu99.gateway.gateway.services.CallService;
import com.caitu99.gateway.utils.SpringContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ServicePipe extends AbstractPipe {

    private final static Logger logger = LoggerFactory.getLogger(ServicePipe.class);

    private static AtomicInteger asyncRequestCount = new AtomicInteger(0);

    private static CloseableHttpAsyncClient httpAsyncClient = null;

    static {
        try {
            IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setIoThreadCount(PerfMonitor.getCpuCount() * 2).build();
            ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
            PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ioReactor);
            cm.setDefaultMaxPerRoute(1000);
            cm.setMaxTotal(1000);

            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(240000)
                    .setConnectTimeout(60000).build();

            httpAsyncClient = HttpAsyncClients.custom().setConnectionManager(cm)
                    .setDefaultRequestConfig(requestConfig)
                    .build();

            httpAsyncClient.start();
        } catch (Exception e) {
            logger.error("set io reactor error", e);
        }
    }

    private CallService callService;

    public ServicePipe() {
        callService = SpringContext.getBean(CallService.class);
    }

    public static AtomicInteger getAsyncRequestCount() {
        return asyncRequestCount;
    }

    @Override
    public void onEvent(RequestEvent event) {
        try {
            logger.debug("begin of call: {}", event);

            Map<String, Object> paramsMap = new HashMap<>();
            Map<String, Object> filesMap = new HashMap<>();
            Map<String, Byte> paramsNeedMap = new HashMap<>();

            // validate parameter
            CarmenApi carmenApi = callService.validateParams(event, paramsMap, filesMap, paramsNeedMap);

            // get parameter value map
            Map<String, Object> paraMapChange = new HashMap<>();
            Map<String, Object> filesMapChange = new HashMap<>();
            callService.getParamsValueMap(event, carmenApi, paramsMap, filesMap, paramsNeedMap,
                    paraMapChange, filesMapChange);
            paraMapChange.put("debug", "gateway");

            // call php service
            CarmenServiceMethod serviceMethod = callService.getServiceMethod(event.getId(), carmenApi);

            if (serviceMethod == null) {
                throw new PipelineException(5018, "namespace method is null");
            }

            String url = carmenApi.getAddressUrl();
            if (!url.endsWith("/")) {
                url = url + "/";
            }

            String service = StringUtils.replace(serviceMethod.getName(), ".", "/");
            String requestUrl = url + service + "/" + serviceMethod.getMethod() + "/" + serviceMethod.getVersion();

            if (carmenApi.getRequestType().equals("1")) {
                // build parameters
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                for (Map.Entry<String, Object> item : paraMapChange.entrySet()) {
                    nameValuePairs.add(new BasicNameValuePair(item.getKey(),
                            item.getValue().toString()));
                }

                HttpPost httpPost = new HttpPost(requestUrl);

                // check file
                if (filesMapChange.size() > 0) {
                    String boundary = UUID.randomUUID().toString();
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    builder.setBoundary("-------------" + boundary);
                    builder.setCharset(Charset.forName("utf-8"));

                    // normal value
                    for (NameValuePair nameValuePair : nameValuePairs) {
                        builder.addTextBody(nameValuePair.getName(), nameValuePair.getValue());
                    }

                    // file value
                    for (Map.Entry<String, Object> item : filesMapChange.entrySet()) {
                        List<CommonsMultipartFile> files = (List<CommonsMultipartFile>) item.getValue();
                        for (CommonsMultipartFile file : files) {
                            InputStream inputStream = file.getInputStream();
                            InputStreamBody streamBody = new InputStreamBody(inputStream, ContentType.create("image/unknown"), file.getFileItem().getName());
                            builder.addPart(item.getKey(), streamBody);
                        }
                    }

                    HttpEntity entity = builder.build();

                    // write to a stream
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    entity.writeTo(outputStream);
                    byte[] bytes = outputStream.toByteArray();

                    // cannot use MultipartEntity directly, so ...
                    HttpEntity byteEntity = new NByteArrayEntity(bytes, ContentType.MULTIPART_FORM_DATA);
                    
                    /** 获取APP版本号 */
                    String version = event.getRequest().getHeader("version");
                    if(org.apache.commons.lang.StringUtils.isNotBlank(version)){
                    	httpPost.setHeader("version",version);
                    }
                    /** 获取APP版本号 */
                    
                    httpPost.setHeader("Content-Type", "multipart/form-data;boundary=-------------" + boundary);
                    httpPost.setEntity(byteEntity);
                } else {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, Charset.forName("utf-8"));
                    httpPost.setEntity(urlEncodedFormEntity);
                }

                // send post
                httpAsyncClient.execute(httpPost, new HttpAsyncCallback(event));
            } else {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, Object> entry : paraMapChange.entrySet()) {
                    sb.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue().toString(), "utf-8"))
                            .append("&");
                }

                // remove last '&'
                if (sb.charAt(sb.length() - 1) == '&') {
                    sb.deleteCharAt(sb.length() - 1);
                }

                String s = requestUrl + "?" + sb.toString();
                HttpGet httpGet = new HttpGet(s);
                
                /** 获取APP版本号 */
                String version = event.getRequest().getHeader("version");
                if(org.apache.commons.lang.StringUtils.isNotBlank(version)){
                	httpGet.setHeader("version",version);
                }
                /** 获取APP版本号 */
                
                httpAsyncClient.execute(httpGet, new HttpAsyncCallback(event));
            }

        } catch (CarmenException e) {
            event.setException(e);
            logger.info("exception happened when calling service: {}", event.getId(), e);
        } catch (Exception e) {
            event.setException(e);
            logger.error("exception happened when calling service: {}", event.getId(), e);
        } finally {
            logger.info("complete call: {}", event.getId());
            onNext(event);
        }

    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    protected void onNext(RequestEvent event) {
        if (event.getException() != null) {
            event.setState(RequestState.ERROR);
            Pipeline.getInstance().process(event);
        }
    }

    private class HttpAsyncCallback implements FutureCallback<HttpResponse> {
        private RequestEvent event;

        public HttpAsyncCallback(RequestEvent event) {
            asyncRequestCount.incrementAndGet();
            this.event = event;
            this.event.getTicTac().tic(Constants.ST_CALL);
        }

        @Override
        public void completed(HttpResponse result) {
            asyncRequestCount.decrementAndGet();
            this.event.getTicTac().tac(Constants.ST_CALL);
            this.event.getTicTac().tac(Constants.ST_ALL);
            try {
                String resultStr = EntityUtils.toString(result.getEntity());
                event.setResultStr(resultStr);
                event.setState(RequestState.RESULT);
                Pipeline.getInstance().process(event);
            } catch (IOException e) {
                event.setException(e);
                event.setState(RequestState.ERROR);
                Pipeline.getInstance().process(event);
            }
        }

        @Override
        public void failed(Exception ex) {
            asyncRequestCount.decrementAndGet();
            logger.error("http client call error: {}", event.getId(), ex);
            this.event.getTicTac().tac(Constants.ST_CALL);
            this.event.getTicTac().tac(Constants.ST_ALL);
            event.setException(new CallException(5016, "async http client exception :" + ex.toString()));
                    event.setState(RequestState.ERROR);
            Pipeline.getInstance().process(event);
        }

        @Override
        public void cancelled() {
            asyncRequestCount.decrementAndGet();
            logger.error("http client call cancelled: {}", event.getId());
            this.event.getTicTac().tac(Constants.ST_CALL);
            this.event.getTicTac().tac(Constants.ST_ALL);
            event.setException(new CallException(5015, "request is cancelled"));
            event.setState(RequestState.ERROR);
            Pipeline.getInstance().process(event);
        }
    }

}
