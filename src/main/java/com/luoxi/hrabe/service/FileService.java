package com.luoxi.hrabe.service;

import com.luoxi.hrabe.pojo.Message;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    void uploadFile(MultipartFile file, String policy) throws Exception;

    //byte[] dowenFile(String fileName) throws Exception;

    byte[] downloadFile(String userId, String fileName)throws Exception;

    List<Message> findMessageById(String userId);

    //删除消息
    void deleteMessage(String userId,String fileName);

    //查看所有消息
    List<Message> getAllMessage();
}
