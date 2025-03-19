package com.luoxi.hrabe.service.impl;

import com.luoxi.hrabe.ABE2OD.LSSS;
import com.luoxi.hrabe.ABE2OD.param.Ciphertext;
import com.luoxi.hrabe.ABE2OD.utils;
import com.luoxi.hrabe.HRABE.HRABE;
import com.luoxi.hrabe.HRABE.param.MPK_h;
import com.luoxi.hrabe.Util.AesUtil;
import com.luoxi.hrabe.Util.CiphertextSerializer;
import com.luoxi.hrabe.Util.MPK_hSerializer;
import com.luoxi.hrabe.Util.ThreadLocalUtil;
import com.luoxi.hrabe.mapper.MessageMapper;
import com.luoxi.hrabe.pojo.Message;
import com.luoxi.hrabe.pojo.Public_param;
import com.luoxi.hrabe.service.FileService;
import com.luoxi.hrabe.service.Public_paramService;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    String uploadDir="src/main/resources/FileStorage";

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private Public_paramService public_paramService;

    @Override
    public void uploadFile(MultipartFile file, String password, String policy) throws Exception {

        // 1. 读取原始文件内容
        byte[] fileBytes = file.getBytes();

        // 2. 使用 password 进行 AES 对称加密文件
        byte[] encryptedFileBytes = AesUtil.encrypt(fileBytes, password);

        // 3. 生成文件路径，并存储加密后的文件
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        String baseName = originalFileName;

        // 提取文件扩展名
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex != -1) {
            fileExtension = originalFileName.substring(dotIndex); // 带 `.`
            baseName = originalFileName.substring(0, dotIndex); // 不带 `.`
        }

        String filePath = uploadDir + "/" + originalFileName;
        int count = 1;

        // 检查是否存在重名文件，若存在则添加 `(1)`, `(2)` 后缀
        while (Files.exists(Paths.get(filePath))) {
            filePath = uploadDir + "/" + baseName + "(" + count + ")" + fileExtension;
            count++;
        }

        Files.write(Paths.get(filePath), encryptedFileBytes); // 存储加密后的文件

        // 4. HRABE 加密 password
        LSSS lsss = new LSSS(policy);
        String passwordBinary = utils.stringToBinary(password);

        Pairing pairing = PairingFactory.getPairing("a.properties");
        Public_param publicParam = public_paramService.findPublicParam();
        MPK_h mpk_h = MPK_hSerializer.String2MPK(publicParam.getMpk(), pairing);

        Ciphertext encryptedPassword = HRABE.Enc_h(mpk_h, passwordBinary, lsss, pairing);
        String encKey = CiphertextSerializer.Ciphertext2String(encryptedPassword);

        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");

        // 5. 存储文件信息到数据库
        Message message = new Message();
        message.setUserId(userId);
        message.setEncKey(encKey);
        message.setFileName(Paths.get(filePath).getFileName().toString()); // 存储最终文件名
        message.setFileType(file.getContentType());
        message.setFileSize(String.valueOf(file.getSize()));
        message.setFilePath(filePath);
        message.setTime(LocalDateTime.now());
        message.setPolicy(policy);

        messageMapper.insertMessage(message);
    }

}
