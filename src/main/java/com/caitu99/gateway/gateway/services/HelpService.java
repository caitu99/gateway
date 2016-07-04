package com.caitu99.gateway.gateway.services;

import javax.servlet.http.HttpServletRequest;

public class HelpService {

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.endsWith(":1")) {
            ip = "127.0.0.1";
        }
        return ip;
    }

}
