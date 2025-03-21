package com.luoxi.hrabe.service.impl;

import com.luoxi.hrabe.ABE2OD.LSSS;
import com.luoxi.hrabe.ABE2OD.Serl_Deserl;
import com.luoxi.hrabe.ABE2OD.param.*;
import com.luoxi.hrabe.ABE2OD.utils;
import com.luoxi.hrabe.HRABE.HRABE;
import com.luoxi.hrabe.HRABE.param.MPK_h;
import com.luoxi.hrabe.HRABE.param.ST;
import com.luoxi.hrabe.HRABE.param.UL;
import com.luoxi.hrabe.Util.*;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    String uploadDir="src/main/resources/FileStorage";

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
        message.setFileType(file.getContentType());
        message.setFileSize(String.valueOf(file.getSize()));
        message.setFilePath(filePath);
        message.setTime(LocalDateTime.now());
        message.setPolicy(policy);

        messageMapper.insertMessage(message);
    }

/*    @Override
    public byte[] dowenFile(String fileName) throws Exception {
        // 1. 获取文件信息
        Message message = messageMapper.findByName(fileName);
        if (message == null) {
            throw new RuntimeException("文件不存在");
        }

        String encKey = message.getEncKey();   // 获取文件密钥的密文
        String filePath = message.getFilePath(); // 获取加密文件路径

        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");//获取请求方的ID

        Pairing pairing = PairingFactory.getPairing("a.properties");
        Public_param publicParam = public_paramService.findPublicParam();

        String Mpk_str=publicParam.getMpk();
        MPK_h mpk_h=MPK_hSerializer.String2MPK(Mpk_str, pairing);

        User_enc userEnc=user_encMapper.findById(userId);
        UL_list ul=ul_listMapper.findByUserId(userId);
        if(ul==null){
            System.out.println("用户已经被撤销!!!!");
        }else{
            TK tk1= Util.String2TK(ul.getTk1(),pairing);
            TK tk2= Util.String2TK(ul.getTk2(),pairing);
            HK hk = Util.String2HK(ul.getHk(),pairing);
            DK dk = Util.String2DK(userEnc.getDk(),pairing);

            List<UL_list> list = ul_listMapper.findAll();
            List<UL> ulList=StUtil.convertList(list);

            String sign = st_listMapper.findNewST().getSign();

            ST st=new ST(sign,ulList,"1");
            st.showST();

            Ciphertext ciphertext=CiphertextSerializer.String2Ciphertext(encKey);
            ciphertext.showCipher();
            //transform1
            PTC  ptc = HRABE.Transform1_h(tk1,tk2,st,userId,ciphertext,mpk_h,pairing);
            assert ptc != null;
            ptc.showPTC();
            //transform2
            TC tc= HRABE.Transform2_h(ptc,hk,pairing);
            //解密
            String password =HRABE.Dec_h(dk,tc,mpk_h,pairing);
            System.out.println(password);
            // 3. 读取加密文件
            byte[] encryptedFileBytes = Files.readAllBytes(Paths.get(filePath));

            // 4. 用 AES 解密文件
            return AesUtil.decrypt(encryptedFileBytes, password);
        }
        return null;
    }*/

    @Override
    public void downloadFileWithStatus(String fileName, SseEmitter emitter) throws Exception {
        // 1. 获取文件信息
        Message message = messageMapper.findByName(fileName);
        if (message == null) {
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data("文件不存在"));
            return;
        }

        String encKey = message.getEncKey();
        String filePath = message.getFilePath();

        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");

        Pairing pairing = PairingFactory.getPairing("a.properties");
        Public_param publicParam = public_paramService.findPublicParam();

        String Mpk_str = publicParam.getMpk();
        MPK_h mpk_h = MPK_hSerializer.String2MPK(Mpk_str, pairing);

        User_enc userEnc = user_encMapper.findById(userId);
        UL_list ul = ul_listMapper.findByUserId(userId);

        // 发送用户状态
        if (ul == null) {
            emitter.send(SseEmitter.event()
                    .name("userStatus")
                    .data("用户已经被撤销"));
            return;
        } else {
            emitter.send(SseEmitter.event()
                    .name("userStatus")
                    .data("用户存在于UL列表"));
        }

        TK tk1 = Util.String2TK(ul.getTk1(), pairing);
        TK tk2 = Util.String2TK(ul.getTk2(), pairing);
        HK hk = Util.String2HK(ul.getHk(), pairing);
        DK dk = Util.String2DK(userEnc.getDk(), pairing);

        List<UL_list> list = ul_listMapper.findAll();
        List<UL> ulList = StUtil.convertList(list);
        String sign = st_listMapper.findNewST().getSign();
        ST st = new ST(sign, ulList, "1");

        Ciphertext ciphertext = CiphertextSerializer.String2Ciphertext(encKey);

        // transform1
        emitter.send(SseEmitter.event()
                .name("transform1")
                .data("开始执行Transform1"));
        PTC ptc = HRABE.Transform1_h(tk1, tk2, st, userId, ciphertext, mpk_h, pairing);
        if (ptc == null) {
            emitter.send(SseEmitter.event()
                    .name("transform1")
                    .data("Transform1执行失败"));
            return;
        }
        emitter.send(SseEmitter.event()
                .name("transform1")
                .data("Transform1执行成功"));

        // transform2
        emitter.send(SseEmitter.event()
                .name("transform2")
                .data("开始执行Transform2"));
        TC tc = HRABE.Transform2_h(ptc, hk, pairing);
        if (tc == null) {
            emitter.send(SseEmitter.event()
                    .name("transform2")
                    .data("Transform2执行失败"));
            return;
        }
        emitter.send(SseEmitter.event()
                .name("transform2")
                .data("Transform2执行成功"));

        // 解密
        emitter.send(SseEmitter.event()
                .name("decryption")
                .data("开始执行解密"));
        String password = HRABE.Dec_h(dk, tc, mpk_h, pairing);
        if (password == null) {
            emitter.send(SseEmitter.event()
                    .name("decryption")
                    .data("解密失败"));
            return;
        }
        emitter.send(SseEmitter.event()
                .name("decryption")
                .data("解密成功"));

        // 读取并解密文件
        byte[] encryptedFileBytes = Files.readAllBytes(Paths.get(filePath));
        byte[] decryptedFileBytes = AesUtil.decrypt(encryptedFileBytes, password);

        // 发送文件数据
        emitter.send(SseEmitter.event()
                .name("file")
                .data(Base64.getEncoder().encodeToString(decryptedFileBytes)));
    }

}
