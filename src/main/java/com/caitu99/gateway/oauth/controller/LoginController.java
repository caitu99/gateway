package com.caitu99.gateway.oauth.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.caitu99.gateway.oauth.model.OauthUser;
import com.caitu99.gateway.oauth.model.OpenOauthClients;
import com.caitu99.gateway.oauth.service.impl.OpenOauthClientsService;
import com.caitu99.gateway.utils.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.caitu99.gateway.oauth.Constants;
import com.caitu99.gateway.oauth.exception.PrivilegeException;
import com.caitu99.gateway.oauth.service.impl.OAuthService;
import com.caitu99.gateway.utils.CaptchaUtils;

@Controller
public class LoginController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private static final SimplePropertyPreFilter clientFilter = new SimplePropertyPreFilter(OpenOauthClients.class, "clientId", "clientSecret");

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private OpenOauthClientsService clientsService;

    /**
     * when user has no session
     * @param request
     * @param model
     * @return
     * @throws PrivilegeException
     */
    @RequestMapping(value = "/oauth/login")
    public Object login_(HttpServletRequest request, Model model) throws Exception {
        HttpSession session = request.getSession();

        if (session.getAttribute(Constants.CLIENT_NAME) == null) {
            logger.warn("the authorization link time out");
            throw new PrivilegeException("授权链接已过时，请重新授权！");
        }

        if (session.getAttribute(Constants.USER_ID) == null) {
            model.addAttribute("client", session.getAttribute(Constants.CLIENT_NAME));
            model.addAttribute("query", session.getAttribute(Constants.CLIENT_QUERY));
            return new ModelAndView("login");
        }

        return new ModelAndView(
                "oauth/authorize?" +
                URLDecoder.decode((String) session.getAttribute(Constants.CLIENT_QUERY), Constants.APP_ENCODING)
        );
    }

    /**
     * validate user
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/oauth/login/authorize", produces="application/json;charset=utf-8")
    @ResponseBody
    public Object login(HttpServletRequest request, Model model) throws UnsupportedEncodingException {
        HttpSession session = request.getSession();
        Integer userId = null;
        Map<String, Object> objectMap = new HashMap<>();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String captcha = request.getParameter("captcha");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) ||  StringUtils.isEmpty(captcha)) {
        	objectMap.put("result", false);
            objectMap.put("to", "/oauth/login?" + request.getQueryString());
            return JSON.toJSONString(objectMap);
        }

        Object cc = session.getAttribute(Constants.DEFAULT_CAPTCHA_PARAM);
        if (cc == null || !captcha.equalsIgnoreCase((String) cc)) {
        	objectMap.put("result", false);
            objectMap.put("to", "/oauth/login?" + request.getQueryString());
            return JSON.toJSONString(objectMap);
        }

        OauthUser user = null;
        try {
            user = oAuthService.login(username, password);
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            logger.info("login error", e);
        }
        if (user == null) {
        	objectMap.put("result", false);
            objectMap.put("to", "/oauth/login?" + request.getQueryString());
            return JSON.toJSONString(objectMap);
        }

        userId = user.getId();
        session.setAttribute(Constants.USER_ID, userId);
        session.setAttribute(Constants.USER_ACCOUNT, user.getAccount());

        /**
         * redirect to /oauth/authorize when every thing is ok
         */
        objectMap.put("result", true);
        objectMap.put("to", "/oauth/authorize?" +
                URLDecoder.decode((String) session.getAttribute(Constants.CLIENT_QUERY), Constants.APP_ENCODING));
        return JSON.toJSONString(objectMap);
    }

    /**
     * wap login
     * @param session
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/oauth/enterprise/login", method = RequestMethod.POST, produces="application/json;charset=utf-8")
    @ResponseBody
    public String wapLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String userName = request.getParameter("account");
        String password = request.getParameter("password");
        password = AppUtils.MD5(password);

        OauthUser user = oAuthService.login(userName, password);

        if (user == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 5129);
            jsonObject.put("message", "用户名或密码错误");
            return jsonObject.toJSONString();
        }

        OpenOauthClients clients = clientsService.getByUserId(user.getId());

        if (clients == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 5130);
            jsonObject.put("message", "非企业账号");
            return jsonObject.toJSONString();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("message", "");
        jsonObject.put("data", clients);
        return JSON.toJSONString(jsonObject, clientFilter);
    }

    
    
    /**
     * app business login
     * @param session
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/oauth/business/login", method = RequestMethod.POST, produces="application/json;charset=utf-8")
    @ResponseBody
    public String businessLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String userName = request.getParameter("account");
        String password = request.getParameter("password");
        password = AppUtils.MD5(password);

        OauthUser user = oAuthService.login(userName, password);

        if (user == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 5129);
            jsonObject.put("message", "用户名或密码错误");
            return jsonObject.toJSONString();
        }
        
        if(3 != user.getType()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 5131);
            jsonObject.put("message", "非商务人员帐号");
            return jsonObject.toJSONString();
        }

        //clientId 1002 use  caitu99_backstage
        OpenOauthClients clients = clientsService.getByClientId("1002");
        
        if (clients == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 5130);
            jsonObject.put("message", "非企业账号");
            return jsonObject.toJSONString();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("message", "");
        jsonObject.put("data", clients);
        return JSON.toJSONString(jsonObject, clientFilter);
    }
    
    
    /**
     * get captcha
     * @param session
     * @param response
     * @throws IOException
     */
    @RequestMapping("/oauth/captcha")
    public void getCaptcha(HttpSession session, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        CaptchaUtils tool = new CaptchaUtils();
        StringBuffer code = new StringBuffer();
        BufferedImage image = tool.genRandomCodeImage(code);
        session.removeAttribute(Constants.DEFAULT_CAPTCHA_PARAM);
        session.setAttribute(Constants.DEFAULT_CAPTCHA_PARAM, code.toString());
        ImageIO.write(image, "JPEG", response.getOutputStream());
    }

}