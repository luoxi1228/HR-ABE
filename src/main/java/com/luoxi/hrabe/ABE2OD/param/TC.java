package com.luoxi.hrabe.ABE2OD.param;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 转换密文
 * */

public class TC {
    private Element T0;       // GT群元素
    private String T1;       // 哈希值
    private Element T2;     // GT群元素

    public Element getT0() {
        return T0;
    }

    public void setT0(Element t0) {
        T0 = t0;
    }

    public String getT1() {
        return T1;
    }

    public void setT1(String t1) {
        T1 = t1;
    }

    public Element getT2() {
        return T2;
    }

    public void setT2(Element t2) {
        T2 = t2;
    }

    public void showTC(){
        System.out.println("--TC--");
        System.out.println("T0: " + T0);
        System.out.println("T1: " + T1);
        System.out.println("T2: " + T2);

    }
}
