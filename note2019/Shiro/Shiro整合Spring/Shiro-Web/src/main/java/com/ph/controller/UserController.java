package com.ph.controller;

import com.ph.pojo.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @ResponseBody
    @RequestMapping(value = "/subLogin",method = RequestMethod.POST)
    public String login(User user){

        System.out.println(user);
        Subject subject = SecurityUtils.getSubject();
        //主题提交请求
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(),user.getPassword());
        try {
            subject.login(token);
            if(subject.isAuthenticated()){
                return "successful";
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
        return "error";
    }
}
