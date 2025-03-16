package com.luoxi.hrabe.mapper;

import com.luoxi.hrabe.pojo.User_login;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface User_loginMapper {
    //根据用户id查询用户
    @Select("select * from user_login where user_id=#{userId}")
    User_login findById(String userId);

    //添加
    @Insert("INSERT INTO user_login (user_id, user_name, password, attributes, user_pic) VALUES (#{userId},#{userName}, #{password}, #{attributes}, #{userPic})")
    void add(String userId, String userName,String password, String attributes,String userPic);

}
