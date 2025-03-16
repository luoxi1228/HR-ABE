package com.luoxi.hrabe.HRABE.param;

import com.luoxi.hrabe.ABE2OD.param.DK;
import com.luoxi.hrabe.ABE2OD.param.HK;
import com.luoxi.hrabe.ABE2OD.param.TK;
import lombok.experimental.PackagePrivate;

import java.util.List;

public class JoinResult {
    private List<UL> ul;
    private TK tk1;
    private TK tk2;
    private HK hk;
    private DK dk;

    public JoinResult(List<UL> ul, TK tk1, TK tk2, HK hk, DK dk) {
        this.ul = ul;
        this.tk1 = tk1;
        this.tk2 = tk2;
        this.hk = hk;
        this.dk = dk;
    }

    public List<UL> getUl() {
        return ul;
    }

    public void setUl(List<UL> ul) {
        this.ul = ul;
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

    public DK getDk() {
        return dk;
    }

    public void setDk(DK dk) {
        this.dk = dk;
    }
}
