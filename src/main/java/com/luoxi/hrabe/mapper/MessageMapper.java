package com.luoxi.hrabe.mapper;

import com.luoxi.hrabe.pojo.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO message (user_id, enc_key,file_name,file_type,file_size,file_path,time,policy) " +
            "VALUES (#{userId}, #{encKey}, #{fileName}, #{fileType}, #{fileSize},#{filePath},#{time},#{policy})")
    void insertMessage(Message message);
}
