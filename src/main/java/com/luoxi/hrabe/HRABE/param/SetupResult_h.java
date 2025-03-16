package com.luoxi.hrabe.HRABE.param;

import java.util.List;

public class SetupResult_h {
    private MPK_h mpk_h;
    private MSK_h msk_h;
    private List<UL> ul_h;
    private ST st_h;

    public SetupResult_h(MPK_h mpk_h, MSK_h msk_h,ST st_h, List<UL> ul_h) {
        this.mpk_h = mpk_h;
        this.msk_h = msk_h;
        this.ul_h = ul_h;
        this.st_h = st_h;
    }

    public MPK_h getMpk_h() {
        return mpk_h;
    }

    public void setMpk_h(MPK_h mpk_h) {
        this.mpk_h = mpk_h;
    }

    public MSK_h getMsk_h() {
        return msk_h;
    }

    public void setMsk_h(MSK_h msk_h) {
        this.msk_h = msk_h;
    }

    public List<UL> getUl_h() {
        return ul_h;
    }

    public void setUl_h(List<UL> ul_h) {
        this.ul_h = ul_h;
    }

    public ST getSt_h() {
        return st_h;
    }

    public void setSt_h(ST st_h) {
        this.st_h = st_h;
    }

    public void show(){
        mpk_h.showMPK();
        msk_h.showMSK();
        st_h.showST();
        System.out.println(ul_h.toString());
    }
}
