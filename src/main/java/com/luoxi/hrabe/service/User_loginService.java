package com.luoxi.hrabe.service;

import com.luoxi.hrabe.pojo.User_login;
import org.springframework.web.multipart.MultipartFile;

public interface User_loginService {
    //根据用户id查询用户
    User_login findById(String userId);

    //注册
    void register(String userId,String userName, String password,String attributes,String userPic) throws Exception;

    void updateInfo(User_login user);

    void updatePic(MultipartFile file);

    void updatePwd(String newPwd);

    void deleteUser_login(String userId);
}
