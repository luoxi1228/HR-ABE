package com.luoxi.hrabe.ABE2OD.param;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 帮助密钥
 * */
public class HK {
    private Element gamma_1;  // Zr群元素
    private Element gamma_2;  // Zr群元素

    public HK(Element gamma_1, Element gamma_2) {
        this.gamma_1 = gamma_1;
        this.gamma_2 = gamma_2;
    }

    public HK() {
    }

    public Element getGamma_1() {
        return gamma_1;
    }

    public void setGamma_1(Element gamma_1) {
        this.gamma_1 = gamma_1;
    }

    public Element getGamma_2() {
        return gamma_2;
    }

    public void setGamma_2(Element gamma_2) {
        this.gamma_2 = gamma_2;
    }


    public void  showHK() {
        System.out.println("--HK--");
        System.out.println( "gamma_1: " + gamma_1);
        System.out.println( "gamma_2: " + gamma_2);
    }
}
