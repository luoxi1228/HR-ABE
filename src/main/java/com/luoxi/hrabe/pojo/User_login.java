package com.luoxi.hrabe.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class User_login {
    private String userId;
    private String userName;
    private String password;
    private String attributes;
    private String userPic;//用户头像地址
}
