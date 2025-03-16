package com.luoxi.hrabe.Util;

import com.luoxi.hrabe.ABE2OD.*;
import com.luoxi.hrabe.ABE2OD.param.DK;
import com.luoxi.hrabe.ABE2OD.param.HK;
import com.luoxi.hrabe.ABE2OD.param.TK;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.Base64;

public class Util {
    // 将 Element 转换为 Base64 字符串
    public static String elementToString(Element element) {
        if (element == null) return null;
        return Base64.getEncoder().encodeToString(element.toBytes());
    }

    // 从 Base64 字符串还原 Element
    public static Element stringToElement(String data, Pairing pairing) {
        if (data == null || pairing == null) return null;
        byte[] bytes = Base64.getDecoder().decode(data);
        return pairing.getZr().newElementFromBytes(bytes);
    }

    // 将 TK 对象序列化为 Base64 字符串
    public static String TK2String(TK tk) {
        byte[] serializedData = Serl_Deserl.serializeTK(tk);
        return Base64.getEncoder().encodeToString(serializedData);
    }

    // 将 Base64 字符串解码回TK
    public static TK String2TK(String base64String, Pairing pairing) {
        byte[] TK_byte = Base64.getDecoder().decode(base64String);
        return Serl_Deserl.deserializeTK(TK_byte, pairing);
    }

    // 将 HK 对象序列化为 Base64 字符串
    public static String HK2String(HK hk) {
        byte[] serializedData = Serl_Deserl.serializeHK(hk);
        return Base64.getEncoder().encodeToString(serializedData);
    }

    // 将 Base64 字符串解码回HK
    public static HK String2HK(String base64String, Pairing pairing) {
        byte[] HK_byte = Base64.getDecoder().decode(base64String);
        return Serl_Deserl.deserializeHK(HK_byte, pairing);
    }

    //将 DK 对象序列化为 Base64 字符串
    public static String DK2String(DK dk) {
        return elementToString(dk.getBeta());
    }

    //将 Base64 字符串解码回DK
    public static DK String2DK(String base64String, Pairing pairing) {
        DK dk = new DK();
        dk.setBeta(stringToElement(base64String, pairing));
        return dk;
    }



}
