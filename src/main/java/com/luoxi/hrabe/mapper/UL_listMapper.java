package com.luoxi.hrabe.mapper;

import com.luoxi.hrabe.pojo.UL_list;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UL_listMapper {

    //查询用户
    @Select("select * from ul_list where user_id = #{userId}")
    UL_list findByUserId(String userId);

    //添加用户
    @Insert("insert into ul_list(user_id,attributes,tk1,tk2,hk) VALUES (#{userId},#{attributes}, #{tk1}, #{tk2}, #{hk})")
    void addUL(UL_list ul);

    //删除用户
    @Delete("DELETE FROM ul_list WHERE user_id = #{userId}")
    void deleteUL(@Param("userId") String userId);

    //获取列表信息
    @Select("SELECT user_id AS userId, attributes, tk1, tk2, hk FROM ul_list")
    List<UL_list> findAll();

    //清空
    @Delete("delete from ul_list")
    void clear();

    //更新用户列表
    @Update("update ul_list set attributes=#{attributes},tk1=#{tk1},tk2=#{tk2},hk=#{hk} where user_id=#{userId} ")
    void updateUL(UL_list ul);

}
