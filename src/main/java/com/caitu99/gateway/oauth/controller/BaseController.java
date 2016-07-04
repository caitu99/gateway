package com.caitu99.gateway.oauth.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    @ExceptionHandler(Exception.class)
    public Object exception(Exception e, HttpServletRequest request) {

        // for ajax request exception
        if (request != null && "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
            JSONObject error = new JSONObject();
            error.put("code", "-1");
            error.put("message", e.getMessage());
            return new ResponseEntity<String>(error.toJSONString(), HttpStatus.valueOf(200));
        }

        ModelAndView mav = new ModelAndView("/error");
        mav.addObject("statusCode", -1);
        mav.addObject("message", e.toString());

        return mav;
    }

}
