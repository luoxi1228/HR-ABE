package com.luoxi.hrabe.controller;

import com.luoxi.hrabe.Util.JwtUtil;
import com.luoxi.hrabe.Util.Md5Util;
import com.luoxi.hrabe.mapper.ST_listMapper;
import com.luoxi.hrabe.mapper.UL_listMapper;
import com.luoxi.hrabe.pojo.Result;
import com.luoxi.hrabe.pojo.UL_list;
import com.luoxi.hrabe.pojo.User_enc;
import com.luoxi.hrabe.pojo.User_login;
import com.luoxi.hrabe.service.ST_listService;
import com.luoxi.hrabe.service.UL_listService;
import com.luoxi.hrabe.service.User_encService;
import com.luoxi.hrabe.service.User_loginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private User_loginService user_loginService;

    @Autowired
    private User_encService user_encService;

    @Autowired
    private UL_listService ul_listService;

    @Autowired
    private ST_listService st_listService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/register")
    public Result register(@RequestBody User_login user) throws Exception {
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
        User_enc u1=user_encService.findById(userId);

        if (u == null && u1 == null) {
            user_loginService.register(userId, userName, password, attributes, userPic); //登录注册
            user_encService.addUser_enc(userId, attributes); // 存储用户的密钥信息
            //将用户添加到用户列表
            User_enc u2=user_encService.findById(userId);
            UL_list ul=new UL_list(userId,attributes,u2.getTk1(), u2.getTk2(), u2.getHk());
            ul_listService.addUL(ul);
            //更新状态
            st_listService.updateUL();

            return Result.success();
        } else {
            return Result.error("用户ID已被占用");
        }
    }

    @PostMapping("/login")
    public Result<String> login(String userId, String password) {
        //根据用户名查询用户
        User_login u = user_loginService.findById(userId);
        //判断该用户是否存在
        if (u == null) {
            return Result.error("用户ID错误");
        }

        //判断密码是否正确  loginUser对象中的password是密文
        if (Md5Util.getMD5String(password).equals(u.getPassword())) {
            //登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", u.getUserId());
            claims.put("username", u.getUserName());
            String token = JwtUtil.genToken(claims);
            //把token存储到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,1, TimeUnit.HOURS);
            return Result.success(token);
        }
        return Result.error("密码错误");
    }


}

