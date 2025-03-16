package com.luoxi.hrabe.ABE2OD.param;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 部分转换密文
 * */
public class PTC {
    private Element C0;        // GT群元素
    private String C1;        // 哈希值
    private Element CP1;      // GT群元素
    private Element CP2;      // GT群元素

    public PTC(Element c0, String c1, Element CP1, Element CP2) {
        C0 = c0;
        C1 = c1;
        this.CP1 = CP1;
        this.CP2 = CP2;
    }

    public PTC() {
    }

    public Element getC0() {
        return C0;
    }

    public void setC0(Element c0) {
        C0 = c0;
    }

    public String getC1() {
        return C1;
    }

    public void setC1(String c1) {
        C1 = c1;
    }

    public Element getCP1() {
        return CP1;
    }

    public void setCP1(Element CP1) {
        this.CP1 = CP1;
    }

    public Element getCP2() {
        return CP2;
    }

    public void setCP2(Element CP2) {
        this.CP2 = CP2;
    }

    public void showPTC(){
        System.out.println("--PTC--");
        System.out.println("C0: " + C0);
        System.out.println("C1: " + C1);
        System.out.println("CP1: " + CP1);
        System.out.println("CP2: " + CP2);
    }
}
