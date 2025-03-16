package com.luoxi.hrabe.ABE2OD.param;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Base64;

public class HKJson {
    public String gamma_1;  // Zr群元素
    public String gamma_2;  // Zr群元素

    public HKJson(HK hk) {
        this.gamma_1=encodeElement(hk.getGamma_1());
        this.gamma_2=encodeElement(hk.getGamma_2());
    }

    // 将 Element 转换为 Base64 编码字符串
    private String encodeElement(Element element) {
        if (element == null) return null;
        return Base64.getEncoder().encodeToString(element.toBytes());
    }
}
