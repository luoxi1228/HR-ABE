package com.luoxi.hrabe.controller;

import com.luoxi.hrabe.Util.JwtUtil;
import com.luoxi.hrabe.pojo.*;
import com.luoxi.hrabe.service.AdminService;
import com.luoxi.hrabe.service.Public_paramService;
import com.luoxi.hrabe.service.ST_listService;
import com.luoxi.hrabe.service.UL_listService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private Public_paramService public_paramService;

    @Autowired
    private UL_listService ul_listService;

    @Autowired
    private ST_listService st_listService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @PostMapping("/login")
    public Result<String> login(String adminId, String password) {
        System.out.println("adminId: " + adminId);  // 打印 adminId
        System.out.println("password: " + password);  // 打印 password

        // 根据用户名查询用户
        Admin admin = adminService.getAdmin(adminId);
        System.out.println("查询到的 admin: " + admin);  // 打印查询结果

        // 判断该用户是否存在
        if (admin == null) {
            return Result.error("用户ID错误");
        }

        // 判断密码是否正确
        if (password.equals(admin.getPassword())) {
            //登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("adminId", admin.getAdminId());
            String token = JwtUtil.genToken(claims);
            //把token存储到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,1, TimeUnit.HOURS);
            return Result.success(token);
        }
        return Result.error("密码错误");
    }


    @GetMapping("/setup")
    public Result setup() throws Exception {
        public_paramService.setupPublicParam();
        System.out.println("初始化");

        Public_param public_param = public_paramService.findPublicParam();
        if (public_param == null) {
            return Result.error("初始化失败！");
        }
        return Result.success("初始化成功");
    }

    //撤销用户
    @PostMapping("/revoke")
    public Result revoke( String userId) throws Exception {

        UL_list ulList=ul_listService.findByUserId(userId);
        if (ulList == null) {
            return Result.error("不存在该用户");
        }
        ul_listService.deleteUL(userId);
        //更新状态
        st_listService.updateUL();

        return Result.success();
    }

}
