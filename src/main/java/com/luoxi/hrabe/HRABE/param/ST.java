package com.luoxi.hrabe.HRABE.param;

import java.io.PushbackInputStream;
import java.util.List;

public class ST {
    private String index;
    private String sign;
    private List<UL> ul;

    public ST(String sign, List<UL> ul,String index) {
        this.index = index;
        this.sign = sign;
        this.ul = ul;
    }

    public ST() {
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public List<UL> getUl() {
        return ul;
    }

    public void setUl(List<UL> ul) {
        this.ul = ul;
    }
    public void showST(){
        System.out.println("---------------ST---------------");
        System.out.println("index:"+index);
        System.out.println("sign:"+sign);
        System.out.println("ul数量:"+ul.size());
        System.out.println("--------------------------------");
    }
}
