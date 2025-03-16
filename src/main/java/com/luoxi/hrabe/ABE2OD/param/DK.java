package com.luoxi.hrabe.ABE2OD.param;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 解密密钥
 * */

public class DK {
    private Element beta; //Zr

    public Element getBeta() {
        return beta;
    }

    public void setBeta(Element beta) {
        this.beta = beta;
    }
    // Zr群元素
    public void showDK(){
        System.out.println("--DK--");
        System.out.println("beta: "+beta);
    }
}
