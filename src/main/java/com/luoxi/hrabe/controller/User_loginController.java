package com.luoxi.hrabe.controller;

import com.luoxi.hrabe.pojo.Result;
import com.luoxi.hrabe.pojo.User_login;
import com.luoxi.hrabe.service.User_loginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class User_loginController {
    @Autowired
    private User_loginService user_loginService;

    @PostMapping("/register")
    public Result register(@RequestBody User_login user) {
        String userId = user.getuserId();
        String userName = user.getUserName();
        String password = user.getPassword();
        String attributes = user.getattributes();
        String userPic = user.getUserPic();

        System.out.println("userId length: " + userId.length() + ", value: " + userId);

        User_login u = user_loginService.findById(userId);
        if (u == null) {
            user_loginService.register(userId, userName, password, attributes, userPic);
            return Result.success();
        } else {
            return Result.error("用户ID已被占用");
        }
    }


}
