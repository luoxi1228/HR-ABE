package com.luoxi.hrabe.mapper;

import com.luoxi.hrabe.pojo.Public_param;
import org.apache.ibatis.annotations.*;

@Mapper
public interface Public_paramMapper {
    //查询公共参数
    @Select("select * from public_param where ind = 1")
    Public_param findPulicParm();

    //更新公共参数
    @Update("UPDATE public_param SET mpk = #{mpk}, msk = #{msk} WHERE ind = 1")
    void updatePublicParam(@Param("mpk") String mpk, @Param("msk") String msk);

}
