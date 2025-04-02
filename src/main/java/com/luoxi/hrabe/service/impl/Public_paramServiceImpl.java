package com.luoxi.hrabe.service.impl;

import com.luoxi.hrabe.HRABE.HRABE;
import com.luoxi.hrabe.HRABE.param.MPK_h;
import com.luoxi.hrabe.HRABE.param.MSK_h;
import com.luoxi.hrabe.HRABE.param.SetupResult_h;
import com.luoxi.hrabe.Util.MPK_hSerializer;
import com.luoxi.hrabe.Util.MSK_hSerializer;
import com.luoxi.hrabe.mapper.*;
import com.luoxi.hrabe.pojo.Message;
import com.luoxi.hrabe.pojo.Public_param;
import com.luoxi.hrabe.service.FileService;
import com.luoxi.hrabe.service.Public_paramService;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class Public_paramServiceImpl implements Public_paramService {

    String uploadDir = "static/FileStorage/";
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private Public_paramMapper publicParamMapper;
    @Autowired
    private ST_listMapper stListMapper;
    @Autowired
    private UL_listMapper ulListMapper;
    @Autowired
    private User_encMapper user_encMapper;
    @Autowired
    private User_loginMapper user_loginMapper;

    @Override
    public Public_param findPublicParam() {
        return publicParamMapper.findPublicParm();
    }

    @Override
    public void setupPublicParam() throws Exception {
        //用户注册后初始化其TK,HK,DK

        Pairing pairing = PairingFactory.getPairing("a.properties");

        SetupResult_h setupResult = HRABE.Setup_h(pairing);
        //setupResult.show();

        //初始化的参数
        MPK_h mpk_h = setupResult.getMpk_h();
        MSK_h msk_h = setupResult.getMsk_h();

        String mpk = MPK_hSerializer.MPK2String(mpk_h);
        String msk = MSK_hSerializer.MSK2String(msk_h);
        //初始化MSK，MPK
        if(publicParamMapper.findPublicParm() != null){
            publicParamMapper.updatePublicParam(mpk, msk);
        }else {
            publicParamMapper.addPublic(mpk, msk);
            System.out.println("初始化公共参数");
        }
        //初始化ST
        if (stListMapper.findNewST() != null) {
            stListMapper.updateST("sign");
        } else {
            stListMapper.addST("sign");
        }

        //清空消息类别并删除文件
        List<Message> messageList= messageMapper.findAll();
        for(Message message:messageList){
            String fileName = message.getFileName();
            String userId = message.getUserId();
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

        //删除用户列表、登录列表、用户加密列表、
        user_loginMapper.clear();
        user_encMapper.clear();
        ulListMapper.clear();

    }
}
