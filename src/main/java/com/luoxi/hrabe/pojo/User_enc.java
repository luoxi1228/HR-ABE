package com.luoxi.hrabe.pojo;

public class User_enc {
    private String userId;
    private String tk1;
    private String tk2;
    private String hk;
    private String dk;

    public User_enc(String userId, String tk1, String tk2, String hk, String dk) {
        this.userId = userId;
        this.tk1 = tk1;
        this.tk2 = tk2;
        this.hk = hk;
        this.dk = dk;
    }

    public User_enc() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTk1() {
        return tk1;
    }

    public void setTk1(String tk1) {
        this.tk1 = tk1;
    }

    public String getTk2() {
        return tk2;
    }

    public void setTk2(String tk2) {
        this.tk2 = tk2;
    }

    public String getHk() {
        return hk;
    }

    public void setHk(String hk) {
        this.hk = hk;
    }

    public String getDk() {
        return dk;
    }

    public void setDk(String dk) {
        this.dk = dk;
    }
}
