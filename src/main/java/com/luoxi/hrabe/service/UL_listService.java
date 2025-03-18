package com.luoxi.hrabe.service;

import com.luoxi.hrabe.pojo.UL_list;

import java.util.List;

public interface UL_listService {
    //查询用户
    UL_list findByUserId(String userId);

    //添加用户进列表
    void addUL(UL_list ul);

    //撤销用户
    void deleteUL(String userId);

    //获取列表的所有用户
    List<UL_list> getAllULList();
}
