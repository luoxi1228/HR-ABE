package com.luoxi.hrabe.mapper;

import com.luoxi.hrabe.pojo.User_login;
import org.apache.ibatis.annotations.*;

@Mapper
public interface User_loginMapper {
    //根据用户id查询用户
    @Select("select * from user_login where user_id=#{userId}")
    User_login findById(String userId);

    //添加
    @Insert("INSERT INTO user_login (user_id, user_name, password, attributes, user_pic) VALUES (#{userId},#{userName}, #{password}, #{attributes}, #{userPic})")
    void add(String userId, String userName,String password, String attributes,String userPic);

    //更新用户信息
    @Update("update user_login set user_name=#{userName},attributes=#{attributes} where user_id=#{userId}")
    void update(User_login user);

    //更新用户头像
    @Update("update user_login set user_pic=#{userPic} where user_id=#{userId}")
    void updatePic(String userPic, String userId);

    //更新密码
    @Update("update user_login set password=#{password} where user_id=#{userId}")
    void updatePwd(String password, String userId);

    //清空
    @Delete("delete from user_login")
    void clear();
}
