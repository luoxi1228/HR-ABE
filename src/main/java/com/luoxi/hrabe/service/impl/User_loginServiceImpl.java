package com.luoxi.hrabe.service.impl;

import com.luoxi.hrabe.mapper.User_loginMapper;
import com.luoxi.hrabe.pojo.User_login;
import com.luoxi.hrabe.service.User_loginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class User_loginServiceImpl implements User_loginService {

    @Autowired
    private User_loginMapper user_loginMapper;

    //根据用户id查询用户
    @Override
    public User_login findById(String userId){
         User_login user_login = user_loginMapper.findById(userId);
         return user_login;
    }

    //注册
    @Override
    public void register(String userId,String userName, String password,String attributes,String userPic){
        user_loginMapper.add(userId,userName,password,attributes,userPic);
    }
}
