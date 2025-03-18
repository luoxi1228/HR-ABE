package com.luoxi.hrabe.service;

import com.luoxi.hrabe.pojo.Public_param;

public interface Public_paramService {
    Public_param findPublicParam();
    //初始化公共参数MPk msk
    void setupPublicParam() throws Exception;

}
