package com.luoxi.hrabe.ABE2OD.param;

import it.unisa.dia.gas.jpbc.Element;

import java.util.HashMap;
import java.util.Map;
/**
 * 转换密钥*/
public class TK {
    private String attributes;
    private Element K;       // G1群元素
    private Element L;       // G1群元素
    private Map<String, Element> Ky;  // 属性到G1群元素的映射

    public TK() {
        this.Ky = new HashMap<String, Element>();
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public Element getK() {
        return K;
    }

    public void setK(Element k) {
        K = k;
    }

    public Element getL() {
        return L;
    }

    public void setL(Element l) {
        L = l;
    }

    public Map<String, Element> getKy() {
        return Ky;
    }

    public void setKy(Map<String, Element> ky) {
        Ky = ky;
    }

    public void showTK(){
        System.out.println("--TK--");
        System.out.println("attributes: " + attributes);
        System.out.println("K: " + K);
        System.out.println("L: " + L);
        System.out.println("Ky: " + Ky);
    }

}
