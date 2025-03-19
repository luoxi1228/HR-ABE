package com.luoxi.hrabe.service.impl;

import com.luoxi.hrabe.HRABE.HRABE;
import com.luoxi.hrabe.HRABE.param.*;
import com.luoxi.hrabe.Util.Md5Util;
import com.luoxi.hrabe.Util.ThreadLocalUtil;
import com.luoxi.hrabe.Util.Util;
import com.luoxi.hrabe.mapper.User_encMapper;
import com.luoxi.hrabe.mapper.User_loginMapper;
import com.luoxi.hrabe.pojo.User_enc;
import com.luoxi.hrabe.pojo.User_login;
import com.luoxi.hrabe.service.User_loginService;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    public void register(String userId,String userName, String password,String attributes,String userPic) throws Exception {
        //加密
        String md5String = Md5Util.getMD5String(password);
        //添加
        user_loginMapper.add(userId,userName,md5String,attributes,userPic);


    }

    @Override
    public void update(User_login user) {
        user_loginMapper.update(user);
    }

    @Override
    public void updatePic(String userPic) {
        Map<String,Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");
        user_loginMapper.updatePic(userPic,userId);
    }

    @Override
    public void updatePwd(String newPwd) {
        Map<String,Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");
        user_loginMapper.updatePwd(Md5Util.getMD5String(newPwd),userId);
    }


}
