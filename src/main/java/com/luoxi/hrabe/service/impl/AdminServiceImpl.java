package com.luoxi.hrabe.service.impl;

import com.luoxi.hrabe.mapper.AdminMapper;
import com.luoxi.hrabe.pojo.Admin;
import com.luoxi.hrabe.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin getAdmin(String adminId) {
        return adminMapper.findById(adminId);
    }
}
