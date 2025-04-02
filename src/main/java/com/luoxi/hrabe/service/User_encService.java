package com.luoxi.hrabe.service;

import com.luoxi.hrabe.pojo.User_enc;

public interface User_encService {

    //查询信息
    User_enc findById(String userId);

    //添加信息
    void addUser_enc(String userId,String attributes) throws Exception;

    //根据id删除用户
    void deleteUser_enc(String userId);

    //更新用户
    void updateUser_enc(String userId,String attributes) throws Exception;
}
