package com.luoxi.hrabe.HRABE.param;

import com.luoxi.hrabe.ABE2OD.param.MSK;

public class MSK_h {
    private MSK msk;
    private String sk;

    public MSK_h(MSK msk, String sk) {
        this.msk = msk;
        this.sk = sk;
    }

    public MSK_h() {
    }

    public MSK getMsk() {
        return msk;
    }

    public void setMsk(MSK msk) {
        this.msk = msk;
    }

    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    public void showMSK(){
        System.out.println("------------MSK---------");
        msk.showMSK();
        System.out.println("sk:"+this.sk);
        System.out.println("-------------------------");
    }
}
