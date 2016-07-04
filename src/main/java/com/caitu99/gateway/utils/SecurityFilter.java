/**
 *
 */
package com.caitu99.gateway.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SecurityFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);


    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest q = (HttpServletRequest) request;
        final String path = q.getServletPath();
        logger.info("a request reached: " + path);

        chain.doFilter(request, response);

    }

    public void init(FilterConfig config) throws ServletException {
    }

}
