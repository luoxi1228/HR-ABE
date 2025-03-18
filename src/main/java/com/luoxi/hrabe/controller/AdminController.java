package com.luoxi.hrabe.controller;

import com.luoxi.hrabe.pojo.Public_param;
import com.luoxi.hrabe.pojo.Result;
import com.luoxi.hrabe.service.Public_paramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private Public_paramService public_paramService;

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
}
