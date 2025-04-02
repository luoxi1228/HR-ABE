package com.luoxi.hrabe.service.impl;

import com.luoxi.hrabe.ABE2OD.LSSS;
import com.luoxi.hrabe.ABE2OD.param.*;
import com.luoxi.hrabe.ABE2OD.utils;
import com.luoxi.hrabe.HRABE.HRABE;
import com.luoxi.hrabe.HRABE.param.MPK_h;
import com.luoxi.hrabe.HRABE.param.ST;
import com.luoxi.hrabe.HRABE.param.UL;
import com.luoxi.hrabe.Util.*;
import com.luoxi.hrabe.controller.FileWebSocketHandler;
import com.luoxi.hrabe.mapper.MessageMapper;
import com.luoxi.hrabe.mapper.ST_listMapper;
import com.luoxi.hrabe.mapper.UL_listMapper;
import com.luoxi.hrabe.mapper.User_encMapper;
import com.luoxi.hrabe.pojo.Message;
import com.luoxi.hrabe.pojo.Public_param;
import com.luoxi.hrabe.pojo.UL_list;
import com.luoxi.hrabe.pojo.User_enc;
import com.luoxi.hrabe.service.FileService;
import com.luoxi.hrabe.service.Public_paramService;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class FileServiceImpl implements FileService {

    String uploadDir = "static/FileStorage/";

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private Public_paramService public_paramService;

    @Autowired
    private UL_listMapper ul_listMapper;
    @Autowired
    private User_encMapper user_encMapper;
    @Autowired
    private ST_listMapper st_listMapper;

    @Override
    public void uploadFile(MultipartFile file, String policy) throws Exception {

        // 1. 生成随机密码（32字节，256位，适合AES-256）
        SecureRandom secureRandom = new SecureRandom();
        byte[] passwordBytes = new byte[32];
        secureRandom.nextBytes(passwordBytes);
        String password = Base64.getEncoder().encodeToString(passwordBytes);

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
            fileExtension = originalFileName.substring(dotIndex + 1); // ✅ 去掉 `.`
            baseName = originalFileName.substring(0, dotIndex);
        }

        String filePath = uploadDir + originalFileName;
        int count = 1;

        // 检查是否存在重名文件，若存在则添加 `(1)`, `(2)` 后缀
        while (Files.exists(Paths.get(filePath))) {
            filePath = uploadDir  + baseName + "(" + count + ")." + fileExtension;
            count++;
        }

        Files.write(Paths.get(filePath), encryptedFileBytes); // 存储加密后的文件

        // 4. HRABE 加密 password
        LSSS lsss = new LSSS(policy);
        lsss.showLSSS();
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
        message.setFileType(fileExtension); // ✅ 只存储后缀名，如 `jpg`, `doc`
        message.setFileSize(file.getSize() + " Byte"); // ✅ 添加 `Byte` 单位
        message.setFilePath(filePath);
        message.setTime(LocalDateTime.now());
        message.setPolicy(policy);

        messageMapper.insertMessage(message);
    }

    // downloadFile 方法调整 sendStatus 调用
    @Override
    public byte[] downloadFile(String userId, String fileName) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<TC> future = null;

        try {
            FileWebSocketHandler.sendStatus("start", true, "开始处理文件", fileName);

            Message message = messageMapper.findByName(fileName);
            if (message == null) {
                FileWebSocketHandler.sendStatus("error", false, "文件不存在", fileName);
                throw new Exception("文件不存在");
            }

            String encKey = message.getEncKey();
            String filePath = message.getFilePath();

            // 获取公钥参数
            Pairing pairing = PairingFactory.getPairing("a.properties");
            Public_param publicParam = public_paramService.findPublicParam();
            MPK_h mpk_h = MPK_hSerializer.String2MPK(publicParam.getMpk(), pairing);

            // 获取用户密钥
            User_enc userEnc = user_encMapper.findById(userId);
            UL_list ul = ul_listMapper.findByUserId(userId);

            if (ul == null) {
                FileWebSocketHandler.sendStatus("userStatus", false, "用户已经被撤销", fileName);
                throw new Exception("用户已经被撤销");
            }

            FileWebSocketHandler.sendStatus("userStatus", true, "用户存在于UL列表", fileName);
            TK tk1 = Util.String2TK(ul.getTk1(), pairing);
            TK tk2 = Util.String2TK(ul.getTk2(), pairing);
            HK hk = Util.String2HK(ul.getHk(), pairing);
            DK dk = Util.String2DK(userEnc.getDk(), pairing);

            // 获取属性签名
            List<UL_list> list = ul_listMapper.findAll();
            List<UL> ulList = StUtil.convertList(list);
            ST st = new ST(st_listMapper.findNewST().getSign(), ulList, "1");

            Ciphertext ciphertext = CiphertextSerializer.String2Ciphertext(encKey);

            // 执行 Transform1
            FileWebSocketHandler.sendStatus("transform1", true, "开始执行 Transform1", fileName);
            PTC ptc = HRABE.Transform1_h(tk1, tk2, st, userId, ciphertext, mpk_h, pairing);
            if (ptc == null) {
                FileWebSocketHandler.sendStatus("transform1", false, "Transform1 失败", fileName);
                throw new Exception("Transform1 失败");
            }
            FileWebSocketHandler.sendStatus("transform1", true, "Transform1 执行成功", fileName);

            // 执行 Transform2（带超时控制）
            FileWebSocketHandler.sendStatus("transform2", true, "开始执行 Transform2", fileName);

            // 提交Transform2任务到线程池
            future = executor.submit(() -> HRABE.Transform2_h(ptc, hk, pairing));

            TC tc;
            try {
                // 设置10秒超时
                tc = future.get(10, TimeUnit.SECONDS);
                if (tc == null) {
                    FileWebSocketHandler.sendStatus("transform2", false, "Transform2 失败", fileName);
                    throw new Exception("Transform2 失败");
                }
                FileWebSocketHandler.sendStatus("transform2", true, "Transform2 执行成功", fileName);
            } catch (TimeoutException e) {
                FileWebSocketHandler.sendStatus("transform2", false, "Transform2 执行超时", fileName);
                throw new Exception("Transform2 操作超时，请重试");
            } finally {
                // 取消任务（如果还在运行）
                if (future != null && !future.isDone()) {
                    future.cancel(true);
                }
            }

            // 解密
            FileWebSocketHandler.sendStatus("decryption", true, "开始解密", fileName);
            String password = HRABE.Dec_h(dk, tc, mpk_h, pairing);
            if (password == null) {
                FileWebSocketHandler.sendStatus("decryption", false, "解密失败", fileName);
                throw new Exception("解密失败");
            }
            FileWebSocketHandler.sendStatus("decryption", true, "解密成功", fileName);

            // 读取并解密文件
            byte[] encryptedFileBytes = Files.readAllBytes(Paths.get(filePath));
            byte[] decryptedFileBytes = AesUtil.decrypt(encryptedFileBytes, password);

            // 发送解密成功状态
            FileWebSocketHandler.sendStatus("file", true, "解密成功，文件已准备好", fileName);

            return decryptedFileBytes;

        } catch (Exception e) {
            FileWebSocketHandler.sendStatus("error", false, e.getMessage(), fileName);
            throw e;
        } finally {
            executor.shutdownNow(); // 确保线程池被关闭
        }
    }

    @Override
    public List<Message> findMessageById(String userId) {
        return messageMapper.findById(userId);
    }

    @Override
    public void deleteMessage(String userId, String fileName) {
        // 删除数据库中的文件记录
        messageMapper.deleteByName(userId, fileName);

        // 构造文件路径
        String filePath = uploadDir + fileName;
        File file = new File(filePath);

        // 检查文件是否存在并尝试删除
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("文件删除成功");
            } else {
                System.out.println("文件删除失败");
            }
        } else {
            System.out.println("文件不存在");
        }
    }

    @Override
    public List<Message> getAllMessage() {
        return messageMapper.findAll();
    }

}
