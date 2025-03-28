package com.luoxi.hrabe.mapper;

import com.luoxi.hrabe.pojo.Message;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO message (user_id, enc_key,file_name,file_type,file_size,file_path,time,policy) " +
            "VALUES (#{userId}, #{encKey}, #{fileName}, #{fileType}, #{fileSize},#{filePath},#{time},#{policy})")
    void insertMessage(Message message);

    @Select("select * from message where file_name=#{fileName};")
    Message findByName(String fileName);

    //查询用户上传的消息
    @Select("select * from message where user_id=#{userId}")
    List<Message> findById(String userId);

    //删除消息
    @Delete("delete from message where user_id=#{userId} and file_name=#{fileName} ")
    void deleteByName(String userId,String fileName);

    //查看所有
    @Select("select * from message")
    List<Message> findAll();


}
