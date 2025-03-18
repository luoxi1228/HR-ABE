package com.luoxi.hrabe.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User_enc {
    private String userId;
    private String tk1;
    private String tk2;
    private String hk;
    private String dk;
}
