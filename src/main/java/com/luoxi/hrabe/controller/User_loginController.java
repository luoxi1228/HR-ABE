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
        String userId = user.getUserId();
        String userName = user.getUserName();
        String password = user.getPassword();
        String attributes = user.getAttributes();
        String userPic = user.getUserPic();

        // 用户ID格式校验 (必须以 H 开头，并跟随 6 位数字)
        if (userId == null || !userId.matches("^H\\d{6}$")) {
            return Result.error("用户ID格式错误，必须以'H'开头并跟随6位数字，例如: H123456");
        }

        // 密码长度校验 (必须大于等于 6 位)
        if (password == null || password.length() < 6) {
            return Result.error("密码长度必须至少6位");
        }

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

