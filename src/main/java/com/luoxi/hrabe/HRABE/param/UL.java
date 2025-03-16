package com.luoxi.hrabe.HRABE.param;

import com.luoxi.hrabe.ABE2OD.param.HK;
import com.luoxi.hrabe.ABE2OD.param.TK;

public class UL {
    private String ID;
    private String A;
    private TK tk1;
    private TK tk2;
    private HK hk;

    public UL(String ID, String a, TK tk1, TK tk2, HK hk) {
        this.ID = ID;
        this.A = a;
        this.tk1 = tk1;
        this.tk2 = tk2;
        this.hk = hk;
    }

    public UL() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getA() {
        return A;
    }

    public void setA(String a) {
        this.A = a;
    }

    public TK getTk1() {
        return tk1;
    }

    public void setTk1(TK tk1) {
        this.tk1 = tk1;
    }

    public TK getTk2() {
        return tk2;
    }

    public void setTk2(TK tk2) {
        this.tk2 = tk2;
    }

    public HK getHk() {
        return hk;
    }

    public void setHk(HK hk) {
        this.hk = hk;
    }

    public void showUL(){
        System.out.println("ID: " + ID);
        System.out.println("A: " + A);
        tk1.showTK();
        tk2.showTK();
        hk.showHK();
    }
}
