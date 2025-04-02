package com.luoxi.hrabe.mapper;

import com.luoxi.hrabe.pojo.User_enc;
import org.apache.ibatis.annotations.*;

@Mapper
public interface User_encMapper {

    //根据用户id查询用户
    @Select("select * from user_enc where user_id=#{userId}")
    User_enc findById(String userId);

    //添加用户信息
    @Insert("INSERT INTO user_enc (user_id, tk1, tk2, hk, dk) VALUES (#{userId}, #{tk1}, #{tk2}, #{hk}, #{dk})")
    void insert(User_enc userEnc);

    //清空
    @Delete("delete from user_enc")
    void clear();

    //根据id删除
    @Delete("delete from user_enc where user_id=#{userId}")
    void deleteById(String userId);

    //更新用户信息
    @Update("update user_enc set tk1=#{tk1},tk2=#{tk2},hk=#{hk},dk=#{dk}  where user_id=#{userId}")
    void update(User_enc userEnc);


}
