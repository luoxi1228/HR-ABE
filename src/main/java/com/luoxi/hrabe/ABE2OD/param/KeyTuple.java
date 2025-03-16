package com.luoxi.hrabe.ABE2OD.param;

/**
 * 密钥元组结构
 * */

public class KeyTuple {
    private TK tk_1;
    private TK tk_2;
    private HK hk;
    private DK dk;

    public KeyTuple() {
        tk_1 = new TK();
        tk_2 = new TK();
        hk = new HK();
        dk = new DK();
    }

    public TK getTk_1() {
        return tk_1;
    }

    public void setTk_1(TK tk_1) {
        this.tk_1 = tk_1;
    }

    public TK getTk_2() {
        return tk_2;
    }

    public void setTk_2(TK tk_2) {
        this.tk_2 = tk_2;
    }

    public HK getHk() {
        return hk;
    }

    public void setHk(HK hk) {
        this.hk = hk;
    }

    public DK getDk() {
        return dk;
    }

    public void setDk(DK dk) {
        this.dk = dk;
    }
    // 展示 KeyTuple 内部所有属性的值
    public void showKeyTuple() {
        System.out.println("--KeyTuple--");
        System.out.println("TK 1: " + (tk_1 != null ? tk_1.toString() : "null"));
        System.out.println("TK 2: " + (tk_2 != null ? tk_2.toString() : "null"));
        System.out.println("HK: " + (hk != null ? hk.toString() : "null"));
        System.out.println("DK: " + (dk != null ? dk.toString() : "null"));
    }
}
