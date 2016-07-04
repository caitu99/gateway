package com.caitu99.gateway.oauth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);


    @RequestMapping("/")
    public String index() {
        return "/index";
    }

    /**
     * used for oauth callback test
     * @param req
     * @param model
     * @return
     */
    @RequestMapping("/callback")
    public String callback(HttpServletRequest req, Model model) {
        String query = req.getQueryString();
        model.addAttribute("query", query);
        return "callback";
    }

}