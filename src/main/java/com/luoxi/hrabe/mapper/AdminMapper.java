package com.luoxi.hrabe.mapper;

import com.luoxi.hrabe.pojo.Admin;
import com.luoxi.hrabe.pojo.User_login;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminMapper {

    //根据用户id查询用户
    @Select("select * from admin where admin_id=#{adminId}")
    Admin findById(String adminId);

}
