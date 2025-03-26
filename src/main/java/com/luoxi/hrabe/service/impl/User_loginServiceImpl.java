package com.luoxi.hrabe.service.impl;

import com.luoxi.hrabe.Util.Md5Util;
import com.luoxi.hrabe.Util.ThreadLocalUtil;
import com.luoxi.hrabe.mapper.User_loginMapper;
import com.luoxi.hrabe.pojo.User_login;
import com.luoxi.hrabe.service.User_loginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class User_loginServiceImpl implements User_loginService {

    @Autowired
    private User_loginMapper user_loginMapper;

    //根据用户id查询用户
    @Override
    public User_login findById(String userId){
         User_login user_login = user_loginMapper.findById(userId);
         return user_login;
    }

    //注册
    @Override
    public void register(String userId,String userName, String password,String attributes,String userPic) throws Exception {
        //加密
        String md5String = Md5Util.getMD5String(password);
        //添加
        user_loginMapper.add(userId,userName,md5String,attributes,userPic);


    }

    @Override
    public void update(User_login user) {
        user_loginMapper.update(user);
    }

    @Override
    public void updatePic(MultipartFile file) {
        //  获取用户 ID
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");

        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传失败：文件为空");
        }

        //  确保文件有扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new RuntimeException("上传失败：文件名无效");
        }

        //设置存储路径
        String uploadDir = System.getProperty("user.dir") + "/static/Picture/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs(); // 创建目录
        }

        //  生成新文件名（用户 ID 作为文件名）
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = userId + fileExtension;
        File destFile = new File(uploadDir + newFileName);

        try {
            // 保存文件
            file.transferTo(destFile);

            // 生成数据库存储的 URL
            String picPath = "http://localhost:8080/Picture/" + newFileName;

            // 8更新数据库
            user_loginMapper.updatePic(picPath, userId);
        } catch (IOException e) {
            throw new RuntimeException("头像上传失败：" + e.getMessage(), e);
        }
    }



    @Override
    public void updatePwd(String newPwd) {
        Map<String,Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");
        user_loginMapper.updatePwd(Md5Util.getMD5String(newPwd),userId);
    }


}
