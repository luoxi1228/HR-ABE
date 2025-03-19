package com.luoxi.hrabe.Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luoxi.hrabe.ABE2OD.LSSS;
import com.luoxi.hrabe.ABE2OD.param.Ciphertext;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class CiphertextSerializer {

    private static final ObjectMapper objectMapper = new ObjectMapper(); // 用于 JSON 解析
    private static final Pairing pairing = PairingFactory.getPairing("a.properties"); // 配对参数

    // 将 Ciphertext 转换为 Base64 字符串
    public static String Ciphertext2String(Ciphertext ciphertext) throws Exception {
        StringBuilder sb = new StringBuilder();

        // 1. 序列化 LSSS 策略
        String policyJson = objectMapper.writeValueAsString(ciphertext.getPolicy());
        sb.append(Base64.getEncoder().encodeToString(policyJson.getBytes())).append(";");

        // 2. 序列化各个 Element
        sb.append(encodeElement(ciphertext.getC0())).append(";");
        sb.append(Base64.getEncoder().encodeToString(ciphertext.getC1().getBytes())).append(";");
        sb.append(encodeElement(ciphertext.getC2())).append(";");

        // 3. 序列化列表
        sb.append(encodeElementList(ciphertext.getDi())).append(";");
        sb.append(encodeElementList(ciphertext.getEi())).append(";");
        sb.append(encodeElementList(ciphertext.getLambda())).append(";");

        return sb.toString();
    }

    // 解析 Base64 字符串为 Ciphertext 对象
    public static Ciphertext String2Ciphertext(String base64Str) throws Exception {
        String[] parts = base64Str.split(";");
        if (parts.length != 7) {
            throw new IllegalArgumentException("Invalid base64 ciphertext format");
        }

        Ciphertext ciphertext = new Ciphertext();

        // 1. 反序列化 LSSS
        String policyJson = new String(Base64.getDecoder().decode(parts[0]));
        LSSS policy = objectMapper.readValue(policyJson, LSSS.class);
        ciphertext.setPolicy(policy);

        // 2. 解析 Element
        ciphertext.setC0(decodeElement(parts[1], pairing.getGT().newElement()));
        ciphertext.setC1(new String(Base64.getDecoder().decode(parts[2])));
        ciphertext.setC2(decodeElement(parts[3], pairing.getG1().newElement()));

        // 3. 解析列表
        ciphertext.setDi(decodeElementList(parts[4], pairing.getG1().newElement()));
        ciphertext.setEi(decodeElementList(parts[5], pairing.getG1().newElement()));
        ciphertext.setLambda(decodeElementList(parts[6], pairing.getZr().newElement()));

        return ciphertext;
    }

    // 辅助方法：将 Element 转 Base64
    private static String encodeElement(Element element) {
        return Base64.getEncoder().encodeToString(element.toBytes());
    }

    // 辅助方法：解析 Base64 为 Element
    private static Element decodeElement(String base64, Element field) {
        return field.getField().newElementFromBytes(Base64.getDecoder().decode(base64)).getImmutable();
    }

    // 辅助方法：将 List<Element> 转 Base64
    private static String encodeElementList(List<Element> list) {
        return list.stream().map(CiphertextSerializer::encodeElement).collect(Collectors.joining(","));
    }

    // 辅助方法：解析 Base64 字符串为 List<Element>
    private static List<Element> decodeElementList(String base64List, Element field) {
        return List.of(base64List.split(",")).stream()
                .map(base64 -> decodeElement(base64, field))
                .collect(Collectors.toList());
    }
}
