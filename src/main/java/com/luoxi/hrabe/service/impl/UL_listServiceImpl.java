package com.luoxi.hrabe.service.impl;

import com.luoxi.hrabe.mapper.UL_listMapper;
import com.luoxi.hrabe.pojo.UL_list;
import com.luoxi.hrabe.service.UL_listService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UL_listServiceImpl implements UL_listService {

    @Autowired
    private UL_listMapper ul_listMapper;

    @Override
    public UL_list findByUserId(String userId) {
        return ul_listMapper.findByUserId(userId);
    }

    @Override
    public void addUL(UL_list ul) {
       ul_listMapper.addUL(ul);
    }

    @Override
    public void deleteUL(String userId) {
       ul_listMapper.deleteUL(userId);
    }

    @Override
    public List<UL_list> getAllULList() {
        return ul_listMapper.findAll();
    }
}
