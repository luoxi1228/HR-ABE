package com.luoxi.hrabe.service.impl;

import com.luoxi.hrabe.HRABE.HRABE;
import com.luoxi.hrabe.HRABE.param.*;
import com.luoxi.hrabe.Util.MPK_hSerializer;
import com.luoxi.hrabe.Util.MSK_hSerializer;
import com.luoxi.hrabe.Util.Util;
import com.luoxi.hrabe.mapper.User_encMapper;
import com.luoxi.hrabe.pojo.Public_param;
import com.luoxi.hrabe.pojo.User_enc;
import com.luoxi.hrabe.service.Public_paramService;
import com.luoxi.hrabe.service.User_encService;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class User_encServiceImpl implements User_encService {

    @Autowired
    private User_encMapper user_encMapper;

    @Autowired
    private Public_paramService public_paramService;

    @Override
    public User_enc findById(String userId) {
        return user_encMapper.findById(userId);
    }

    @Override
    public void addUser_enc(String userId,String attributes) throws Exception {
        //用户注册后初始化其TK,HK,DK

        Pairing pairing = PairingFactory.getPairing("a.properties");

        SetupResult_h setupResult=HRABE.Setup_h(pairing);
        //setupResult.show();

        Public_param publicParam = public_paramService.findPublicParam();

        MPK_h mpk_h= MPK_hSerializer.String2MPK(publicParam.getMpk(),pairing);
        MSK_h msk_h= MSK_hSerializer.String2MSK(publicParam.getMsk(),pairing);

        List<UL> ulList=setupResult.getUl_h();//用户列表

        JoinResult JR=HRABE.Join(mpk_h,msk_h,ulList,userId,attributes,pairing);
        String tk1= Util.TK2String(JR.getTk1());
        String tk2= Util.TK2String(JR.getTk2());
        String hk= Util.HK2String(JR.getHk());
        String dk= Util.DK2String(JR.getDk());

        User_enc user_enc=new User_enc(userId,tk1,tk2,hk,dk);

        user_encMapper.insert(user_enc);

    }

}
