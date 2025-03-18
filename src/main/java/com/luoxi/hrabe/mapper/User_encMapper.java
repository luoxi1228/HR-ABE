package com.luoxi.hrabe.mapper;

import com.luoxi.hrabe.pojo.User_enc;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface User_encMapper {

    //根据用户id查询用户
    @Select("select * from user_enc where user_id=#{userId}")
    User_enc findById(String userId);

    //添加用户信息
    @Insert("INSERT INTO user_enc (user_id, tk1, tk2, hk, dk) VALUES (#{userId}, #{tk1}, #{tk2}, #{hk}, #{dk})")
    void insert(User_enc userEnc);


}
