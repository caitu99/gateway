package com.caitu99.gateway.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.caitu99.gateway.AppConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caitu99.gateway.cache.RedisOperate;
import com.caitu99.gateway.cache.RedisOperateImp;


public class ApiConfigFilter implements Filter {

    private AppConfig appConfig = SpringContext.getBean(AppConfig.class);

	private static RedisOperate redisOperate = SpringContext.getBean(RedisOperateImp.class);
    private static final Logger logger = LoggerFactory.getLogger(ApiConfigFilter.class);

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
    	HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        logger.info(req.getServletPath());

        String addr = request.getRemoteAddr();

        logger.debug("remote address: {}", addr);

        if (StringUtils.isNotEmpty(appConfig.allowAccess) && !appConfig.allowAccess.contains(addr)) {
            res.sendRedirect("/");
        } else {
            HttpSession httpSession= req.getSession(true);
            String name = "caitu99";
            String nameKey = "gateway_login_caitu99";
            httpSession.setAttribute("username", nameKey);
            redisOperate.set(nameKey, name);
        }

        chain.doFilter(request, response);
    }

    public void init(FilterConfig config) throws ServletException {
    }

}
