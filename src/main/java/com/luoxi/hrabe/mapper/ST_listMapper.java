package com.luoxi.hrabe.mapper;

import com.luoxi.hrabe.pojo.ST_list;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ST_listMapper {
    //最新状态查询
    @Select("SELECT * FROM st_list WHERE index1 = 1")
    ST_list findNewST();

    //更新状态
    @Update("UPDATE st_list SET sign = #{sign} WHERE index1 = 1 ")
    void updateST(String sign);

    //添加初始状态
    @Insert("INSERT into st_list(index1,sign) VALUES (1, #{sign})")
    void addST(String sign);


}
