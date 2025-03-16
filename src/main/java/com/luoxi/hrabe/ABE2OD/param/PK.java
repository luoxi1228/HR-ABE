package com.luoxi.hrabe.ABE2OD.param;

import it.unisa.dia.gas.jpbc.Element;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 公钥定义
 * */
@AllArgsConstructor
@NoArgsConstructor
public class PK {
    private Element g;           // G1群元素
    private Element eggalpha;    // GT群元素
    private Element ga;          // G1群元素

    public Element getG() {
        return g;
    }

    public void setG(Element g) {
        this.g = g;
    }

    public Element getEggalpha() {
        return eggalpha;
    }

    public void setEggalpha(Element eggalpha) {
        this.eggalpha = eggalpha;
    }

    public Element getGa() {
        return ga;
    }

    public void setGa(Element ga) {
        this.ga = ga;
    }

    public void showPK(){
        System.out.println("PK: ");
        System.out.println("g: " + g);
        System.out.println("eggalpha: " + eggalpha);
        System.out.println("ga: " + ga);
    }
}
