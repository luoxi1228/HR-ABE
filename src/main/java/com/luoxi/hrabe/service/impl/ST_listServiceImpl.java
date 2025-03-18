package com.luoxi.hrabe.service.impl;

import com.luoxi.hrabe.HRABE.HRABE;
import com.luoxi.hrabe.HRABE.param.MSK_h;
import com.luoxi.hrabe.HRABE.param.ST;
import com.luoxi.hrabe.HRABE.param.UL;
import com.luoxi.hrabe.Util.MSK_hSerializer;
import com.luoxi.hrabe.Util.Util;
import com.luoxi.hrabe.mapper.ST_listMapper;
import com.luoxi.hrabe.pojo.Public_param;
import com.luoxi.hrabe.pojo.UL_list;
import com.luoxi.hrabe.service.Public_paramService;
import com.luoxi.hrabe.service.ST_listService;
import com.luoxi.hrabe.service.UL_listService;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ST_listServiceImpl implements ST_listService {

    Pairing pairing = PairingFactory.getPairing("a.properties");

    @Autowired
    private Public_paramService public_paramService;
    @Autowired
    private UL_listService ul_listService;

    @Autowired
    private ST_listMapper st_listMapper;

    @Override
    public void updateUL() throws Exception {
        Public_param publicParam = public_paramService.findPublicParam();
        List<UL_list> ulLists=ul_listService.getAllULList();
        List<UL> ulLists1=convertList(ulLists);
        MSK_h msk_h = MSK_hSerializer.String2MSK( publicParam.getMsk(),pairing);

        ST st = HRABE.Update(msk_h,ulLists1,"1",pairing);
        String sign=st.getSign();
        st_listMapper.updateST(sign);
    }

    public UL convert(UL_list ulList) {

        if (ulList == null) return null;

        UL ul = new UL();
        ul.setID(ulList.getUserId());  // userId → ID
        ul.setA(ulList.getAttributes()); // attributes → A

        // 假设 TK 和 HK 需要从 String 反序列化
        ul.setTk1(Util.String2TK(ulList.getTk1(),pairing));
        ul.setTk2(Util.String2TK(ulList.getTk2(),pairing));
        ul.setHk(Util.String2HK(ulList.getHk(),pairing));

        return ul;
    }

    public List<UL> convertList(List<UL_list> ulList) {
        if (ulList == null) return Collections.emptyList();
        return ulList.stream().map(this::convert).collect(Collectors.toList());
    }
}
