package com.luoxi.hrabe.Util;

import com.luoxi.hrabe.HRABE.param.ST;
import com.luoxi.hrabe.HRABE.param.UL;
import com.luoxi.hrabe.pojo.UL_list;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StUtil {


    public static UL convert(UL_list ulList) {
        Pairing pairing = PairingFactory.getPairing("a.properties");

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

    public static List<UL> convertList(List<UL_list> ulList) {
        if (ulList == null) return Collections.emptyList();
        return ulList.stream().map(StUtil::convert).collect(Collectors.toList());
    }
}
