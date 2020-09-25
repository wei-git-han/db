package com.css.app.db.config.controller;


import com.alibaba.fastjson.JSONObject;
import com.css.addbase.apporgan.entity.BaseAppUser;
import com.css.addbase.apporgan.service.BaseAppUserService;
import com.css.base.entity.SSOUser;
import com.css.base.utils.Response;
import com.css.base.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
@RequestMapping("/api/sso")
public class SsoAdmin {
    private static final ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<HttpServletRequest>();
    private static String ssoUserInfoURL;
    public static final ThreadLocal<String> tokenThreadLocal = new ThreadLocal<String>();
    @Autowired
    public BaseAppUserService baseAppUserService;

    @ResponseBody
    @RequestMapping("/authen")
    public void getUserInfo(String username) {
        JSONObject jsonObject = new JSONObject();
        String uuId = UUIDUtils.random();
        BaseAppUser baseAppUser = baseAppUserService.queryByAccount(username);
        if (baseAppUser != null) {
            baseAppUser.setToken(uuId);
            baseAppUserService.update(baseAppUser);
        }
        jsonObject.put("result", "success");
        jsonObject.put("access_token", uuId);
        Response.json(jsonObject);
    }

    public static String getToken() {
        return tokenThreadLocal.get();
    }

    /*
     * 获取单点当前登录人信息
     */
    @ResponseBody
    @RequestMapping("/getSUser")
    public void  getSUser() {
        JSONObject jsonObject = new JSONObject();
        HttpSession session = requestThreadLocal.get().getSession(true);
        session.setMaxInactiveInterval(5 * 60 * 60);//单位为 秒
        SSOUser user = (SSOUser) session.getAttribute(getToken());
        if (user != null && user.getUseruuid() != null) {
            jsonObject.put("user", user);
        } else {

//            HttpURLConnection con = (HttpURLConnection) new URL(ssoUserInfoURL).openConnection();
//            con.setDoInput(true);
//            con.setDoOutput(true);
//            con.setRequestMethod("POST");
//            con.setRequestProperty("content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//            OutputStream os =  con.getOutputStream();
//            String body ="access_token="+ getToken();
//            os.write(body.getBytes());
//            os.flush();
//            InputStream is = con.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            String line  = reader.readLine();
//            reader.close();
//            user=JSONObject.parseObject(line,SSOUser.class);
            String account = user.getAccount();
            BaseAppUser baseAppUser = baseAppUserService.queryByAccount(account);
            if (baseAppUser != null) {
                requestThreadLocal.get().getSession(true).setAttribute(baseAppUser.getToken(), user);
            }
            jsonObject.put("user", user);

        }
        Response.json(jsonObject);
    }


    public SSOUser getToken(String token){
        SSOUser ssoUser = new SSOUser();
        BaseAppUser baseAppUser = baseAppUserService.queryToken(token);
        if(baseAppUser != null){

           ssoUser.setUserId(baseAppUser.getUserId());
        }
        return ssoUser;
    }


}