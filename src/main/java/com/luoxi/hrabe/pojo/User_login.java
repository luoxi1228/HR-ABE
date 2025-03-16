package com.luoxi.hrabe.pojo;

public class User_login {
    private String userId;
    private String userName;
    private String password;
    private String attributes;
    private String userPic;//用户头像地址

    public User_login(String userId, String userName, String password, String attributes, String userPic) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.attributes = attributes;
        this.userPic = userPic;
    }

    public User_login() {
    }

    public String getuserId() {
        return userId;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getattributes() {
        return attributes;
    }

    public void setattributes(String attributes) {
        this.attributes = attributes;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }
}
