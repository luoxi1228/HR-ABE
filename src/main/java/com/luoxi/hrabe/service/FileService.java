package com.luoxi.hrabe.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void uploadFile(MultipartFile file, String password,String policy) throws Exception;
}
