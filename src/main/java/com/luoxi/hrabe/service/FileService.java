package com.luoxi.hrabe.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface FileService {
    void uploadFile(MultipartFile file, String password,String policy) throws Exception;

    //byte[] dowenFile(String fileName) throws Exception;

    void downloadFileWithStatus(String fileName, SseEmitter emitter)throws Exception;
}
