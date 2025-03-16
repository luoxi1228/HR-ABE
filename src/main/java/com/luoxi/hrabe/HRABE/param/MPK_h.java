package com.luoxi.hrabe.HRABE.param;

import com.luoxi.hrabe.ABE2OD.param.PK;

public class MPK_h {
    private PK pk;
    private String vk;

    public MPK_h(PK pk, String vk) {
        this.pk = pk;
        this.vk = vk;
    }

    public MPK_h() {
    }

    public PK getPk() {
        return pk;
    }

    public void setPk(PK pk) {
        this.pk = pk;
    }

    public String getVk() {
        return vk;
    }

    public void setVk(String vk) {
        this.vk = vk;
    }

    public void showMPK(){
        System.out.println("------------MPK--------------");
        pk.showPK();
        System.out.println("vk:"+this.vk);
        System.out.println("------------------------------");
    }
}
