package com.luoxi.hrabe.controller;

import com.luoxi.hrabe.Util.JwtUtil;
import com.luoxi.hrabe.Util.Md5Util;
import com.luoxi.hrabe.Util.ThreadLocalUtil;
import com.luoxi.hrabe.mapper.ST_listMapper;
import com.luoxi.hrabe.mapper.UL_listMapper;
import com.luoxi.hrabe.pojo.*;
import com.luoxi.hrabe.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private User_loginService user_loginService;

    @Autowired
    private User_encService user_encService;

    @Autowired
    private UL_listService ul_listService;

    @Autowired
    private ST_listService st_listService;

    @Autowired
    private FileService fileService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/register")
    public Result register(@RequestBody User_login user) throws Exception {
        String userId = user.getUserId();
        String userName = user.getUserName();
        String password = user.getPassword();
        String attributes = user.getAttributes();
        String userPic = "src/main/resources/Picture/1.jpg"; // Set default picture path

        // 用户ID格式校验 (必须以 H 开头，并跟随 6 位数字)
        if (userId == null || !userId.matches("^H\\d{6}$")) {
            return Result.error("用户ID格式错误，必须以'H'开头并跟随6位数字，例如: H123456");
        }

        // 密码长度校验 (必须大于等于 6 位)
        if (password == null || password.length() < 6) {
            return Result.error("密码长度必须至少6位");
        }

        System.out.println("userId length: " + userId.length() + ", value: " + userId);

        User_login u = user_loginService.findById(userId);
        User_enc u1=user_encService.findById(userId);

        if (u == null && u1 == null) {
            user_loginService.register(userId, userName, password, attributes, userPic); //登录注册
            user_encService.addUser_enc(userId, attributes); // 存储用户的密钥信息
            //将用户添加到用户列表
            User_enc u2=user_encService.findById(userId);
            UL_list ul=new UL_list(userId,attributes,u2.getTk1(), u2.getTk2(), u2.getHk());
            ul_listService.addUL(ul);
            //更新状态
            st_listService.updateUL();

            return Result.success();
        } else {
            return Result.error("用户ID已被占用");
        }
    }

    @PostMapping("/login")
    public Result<String> login(String userId, String password) {
        //根据用户名查询用户
        User_login u = user_loginService.findById(userId);
        //判断该用户是否存在
        if (u == null) {
            return Result.error("用户ID错误");
        }

        //判断密码是否正确  loginUser对象中的password是密文
        if (Md5Util.getMD5String(password).equals(u.getPassword())) {
            //登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", u.getUserId());
            claims.put("userName", u.getUserName());
            String token = JwtUtil.genToken(claims);
            //把token存储到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,1, TimeUnit.HOURS);
            return Result.success(token);
        }
        return Result.error("密码错误");
    }

    @GetMapping("/userInfo")
    public Result<User_login> userInfo(/*@RequestHeader(name = "Authorization") String token*/) {
        //根据用户名查询用户
       /* Map<String, Object> map = JwtUtil.parseToken(token);
        String username = (String) map.get("username");*/
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");
        User_login user = user_loginService.findById(userId);
        return Result.success(user);
    }

    @PutMapping("/updateInfo")
    public Result updateInfo(@RequestBody  User_login user) throws Exception {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");
        user.setUserId(userId);//获取当前登录的用户id
        user_loginService.updateInfo(user);
        //更新用户密钥列表
        user_encService.updateUser_enc(userId,user.getAttributes());
        //更新用户列表
        User_enc u1=user_encService.findById(userId);
        UL_list u2=ul_listService.findByUserId(userId);
        UL_list ul=new UL_list(userId,user.getAttributes(),u1.getTk1(), u1.getTk2(), u1.getHk());
        if(u2!=null){
            ul_listService.updateUL(ul);
        }
        //更新状态
        st_listService.updateUL();

        return Result.success();
    }

    //更新用户头像
    @PatchMapping("updatePic")
    public Result updatePic(@RequestParam("file") MultipartFile file) {
        try {
            user_loginService.updatePic(file);
            return Result.success("头像更新成功");
        } catch (Exception e) {
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }

    //修改密码
    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String, String> params,@RequestHeader("Authorization") String token) {
        //1.校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");

        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)) {
            return Result.error("缺少必要的参数");
        }

        //原密码是否正确
        //调用userService根据用户名拿到原密码,再和old_pwd比对
        Map<String,Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");
        User_login loginUser = user_loginService.findById(userId);
        if (!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码填写不正确");
        }

        //newPwd和rePwd是否一样
        if (!rePwd.equals(newPwd)){
            return Result.error("两次填写的新密码不一样");
        }

        //2.调用service完成密码更新
        user_loginService.updatePwd(newPwd);
        //删除redis中对应的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }

    @PostMapping("/uploadFile")
    public Result uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("policy") String policy) throws Exception {
        fileService.uploadFile(file,policy);
        System.out.println("文件上传成功");
        return Result.success();
    }

    // downloadFile 接口保持不变，但调整 sendStatus 调用
    @GetMapping("/downloadFile")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String fileName) throws Exception {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");

        // 在新线程中执行解密过程，但需要等待结果以返回文件
        byte[] decryptedFileBytes = null;
        try {
            // 同步调用，确保返回文件内容
            decryptedFileBytes = fileService.downloadFile(userId, fileName);
        } catch (Exception e) {
            // 异常已在 downloadFile 方法中通过 WebSocket 发送，无需重复处理
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

        if (decryptedFileBytes == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(decryptedFileBytes);
    }
    @GetMapping("/userMessage")
    public Result userMessage() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");
        List<Message> messageList=fileService.findMessageById(userId);
        return Result.success(messageList);
    }

    @PostMapping("/deleteMessage")
    public Result deleteMessage(@RequestParam("fileName") String fileName) {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");
        fileService.deleteMessage(userId, fileName);
        return Result.success();
    }

    @GetMapping("/allMessage")
    public Result allMessage() {
        List<Message> messageList = fileService.getAllMessage();
        return Result.success(messageList);
    }

    //查询用户是否在用户列表
    @GetMapping("/ulUser")
    public Result ulUser(){
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");
        UL_list u1=ul_listService.findByUserId(userId);
        if (u1==null){
            return Result.error("用户已被撤销，请重新注册！");
        }else {
            return Result.success("用户存在");
        }
    }

    //用户注销
    @PostMapping("/logout")
    public Result deleteUser(){
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");
        user_encService.deleteUser_enc(userId);
        user_loginService.deleteUser_login(userId);
        return Result.success();
    }


}


