package com.luoxi.hrabe.ABE2OD.param;

import it.unisa.dia.gas.jpbc.Element;

public class PTCJson2 {
    public String C0;
    public String C1;
    public String CP1;
    public String CP2;

    public PTCJson2(PTC ptc) {
        this.C0 = encodeElement(ptc.getC0());
        this.C1 = ptc.getC1();
        this.CP1 = encodeElement(ptc.getCP1());
        this.CP2 = encodeElement(ptc.getCP2());
    }

    // 将 Element 转换为二进制字符串
    private String encodeElement(Element element) {
        if (element == null) return null;
        byte[] bytes = element.toBytes();
        return bytesToBinaryString(bytes);
    }

    // 将字节数组转换为二进制字符串
    private String bytesToBinaryString(byte[] bytes) {
        StringBuilder binaryString = new StringBuilder();
        for (byte b : bytes) {
            // 转换为 8 位二进制字符串，不足前面补 0
            binaryString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return binaryString.toString();
    }
}

