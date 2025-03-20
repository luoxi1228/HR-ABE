package com.luoxi.hrabe.controller;

import com.luoxi.hrabe.Util.JwtUtil;
import com.luoxi.hrabe.Util.Md5Util;
import com.luoxi.hrabe.Util.ThreadLocalUtil;
import com.luoxi.hrabe.mapper.ST_listMapper;
import com.luoxi.hrabe.mapper.UL_listMapper;
import com.luoxi.hrabe.pojo.Result;
import com.luoxi.hrabe.pojo.UL_list;
import com.luoxi.hrabe.pojo.User_enc;
import com.luoxi.hrabe.pojo.User_login;
import com.luoxi.hrabe.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
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
        String userPic = user.getUserPic();

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

    @PutMapping("/update")
    public Result update(@RequestBody  User_login user) {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");
        user.setUserId(userId);//获取当前登录的用户id
        user_loginService.update(user);
        return Result.success();
    }

    //更新用户头像
    @PatchMapping("updatePic")
    public Result updatePic(@RequestParam String userPic) {
        user_loginService.updatePic(userPic);
        return Result.success();
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
    public Result uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("password") String password,@RequestParam("policy") String policy) throws Exception {
        fileService.uploadFile(file, password,policy);
        System.out.println("文件上传成功");
        return Result.success();
    }

    @PostMapping("/download")
    public Result downloadFile(@RequestParam String fileName,
                             HttpServletResponse response) throws Exception {
        // 1. 调用 FileService 解密文件并返回文件流
        byte[] decryptedFileBytes = fileService.dowenFile(fileName);
        if (decryptedFileBytes != null) {
            //发送解密后的文件给前端
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.getOutputStream().write(decryptedFileBytes);
            response.getOutputStream().flush();
            return Result.success();
        }else{
            return Result.error("获取失败");
        }
    }


}

