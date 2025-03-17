package com.luoxi.hrabe.service.impl;

import com.luoxi.hrabe.HRABE.HRABE;
import com.luoxi.hrabe.HRABE.param.*;
import com.luoxi.hrabe.Util.Md5Util;
import com.luoxi.hrabe.Util.Util;
import com.luoxi.hrabe.mapper.User_loginMapper;
import com.luoxi.hrabe.pojo.User_enc;
import com.luoxi.hrabe.pojo.User_login;
import com.luoxi.hrabe.service.User_loginService;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

        //用户注册后初始化其TK,HK,DK
        HRABE hrabe = new HRABE();

        Pairing pairing = PairingFactory.getPairing("a.properties");

        SetupResult_h setupResult=hrabe.Setup_h(pairing);
        //setupResult.show();

        //初始化的参数
        MPK_h mpk_h=setupResult.getMpk_h();
        MSK_h msk_h=setupResult.getMsk_h();
        List<UL> ulList=setupResult.getUl_h();//用户列表

        JoinResult JR=hrabe.Join(mpk_h,msk_h,ulList,userId,attributes,pairing);
        String tk1= Util.TK2String(JR.getTk1());
        String tk2= Util.TK2String(JR.getTk2());
        String hk= Util.HK2String(JR.getHk());
        String dk= Util.DK2String(JR.getDk());

        User_enc user_enc=new User_enc(userId,tk1,tk2,hk,dk);



    }
}
