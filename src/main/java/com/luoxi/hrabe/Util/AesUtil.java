package com.luoxi.hrabe.Util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.security.MessageDigest;
import java.util.Arrays;

public class AesUtil {
    // 生成 16 字节密钥
    private static SecretKeySpec getKey(String password) throws Exception {
        byte[] key = password.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key); // 先哈希
        key = Arrays.copyOf(key, 16); // 取前 16 字节
        return new SecretKeySpec(key, "AES");
    }

    // AES 加密
    public static byte[] encrypt(byte[] data, String password) throws Exception {
        SecretKeySpec secretKey = getKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    // AES 解密
    public static byte[] decrypt(byte[] encryptedData, String password) throws Exception {
        SecretKeySpec secretKey = getKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedData);
    }
}


