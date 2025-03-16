package com.luoxi.hrabe.ABE2OD.param;

import it.unisa.dia.gas.jpbc.Element;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PTCJson {
    public String C0;
    public String C1;
    public String CP1;
    public String CP2;

    public PTCJson(PTC ptc) {
        this.C0 = encodeElement(ptc.getC0());
        this.C1 = encodeString(ptc.getC1());
        this.CP1 = encodeElement(ptc.getCP1());
        this.CP2 = encodeElement(ptc.getCP2());
    }

    // 将 Element 转换为 Base64 编码字符串
    private String encodeElement(Element element) {
        if (element == null) return null;
        return Base64.getEncoder().encodeToString(element.toBytes());
    }
    // 将字符串进行 Base64 编码
    public static String encodeString(String input) {
        if (input == null) return null;
        // 将字符串转为字节数组
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        // 进行 Base64 编码并返回
        return Base64.getEncoder().encodeToString(bytes);
    }
}

