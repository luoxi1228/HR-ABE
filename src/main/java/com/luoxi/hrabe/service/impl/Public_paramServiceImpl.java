package com.luoxi.hrabe.service.impl;

import com.luoxi.hrabe.HRABE.HRABE;
import com.luoxi.hrabe.HRABE.param.MPK_h;
import com.luoxi.hrabe.HRABE.param.MSK_h;
import com.luoxi.hrabe.HRABE.param.SetupResult_h;
import com.luoxi.hrabe.Util.MPK_hSerializer;
import com.luoxi.hrabe.Util.MSK_hSerializer;
import com.luoxi.hrabe.mapper.Public_paramMapper;
import com.luoxi.hrabe.mapper.ST_listMapper;
import com.luoxi.hrabe.pojo.Public_param;
import com.luoxi.hrabe.service.Public_paramService;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Public_paramServiceImpl implements Public_paramService {
    @Autowired
    private Public_paramMapper publicParamMapper;

    @Autowired
    private ST_listMapper stListMapper;

    @Override
    public Public_param findPublicParam() {
        return publicParamMapper.findPublicParm();
    }

    @Override
    public void setupPublicParam() throws Exception {
        //用户注册后初始化其TK,HK,DK

        Pairing pairing = PairingFactory.getPairing("a.properties");

        SetupResult_h setupResult = HRABE.Setup_h(pairing);
        //setupResult.show();

        //初始化的参数
        MPK_h mpk_h = setupResult.getMpk_h();
        MSK_h msk_h = setupResult.getMsk_h();

        String mpk = MPK_hSerializer.MPK2String(mpk_h);
        String msk = MSK_hSerializer.MSK2String(msk_h);
        //初始化MSK，MPK
        if(publicParamMapper.findPublicParm() != null){
            publicParamMapper.updatePublicParam(mpk, msk);
        }else {
            publicParamMapper.addPublic(mpk, msk);
            System.out.println("初始化公共参数");
        }
        //初始化ST
        if (stListMapper.findNewST() != null) {
            stListMapper.updateST("sign");
        } else {
            stListMapper.addST("sign");
        }
    }
}
