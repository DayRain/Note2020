package com.ph.controller;

import com.ph.pojo.User;
import com.ph.service.UserService;
import com.ph.service.serviceImp.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @Autowired
    UserService userService;
    @ResponseBody
    @RequestMapping("/add")
    public User addUser(User user){
        userService.insertUser(user);
        return userService.queryUser(user);
    }
}
