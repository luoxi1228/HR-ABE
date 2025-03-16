package com.luoxi.hrabe.ABE2OD;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class utils {
    public static String trim(String str){//删除字符串中的空格
        int index=0;
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.replace(" ", "");
    }

    public static String stringToBinary(String input) {
        StringBuilder binaryString = new StringBuilder();

        for (char c : input.toCharArray()) {
            // 使用 UTF-16 的 16 位处理每个字符
            binaryString.append(String.format("%16s", Integer.toBinaryString(c)).replace(' ', '0'));
        }

        return binaryString.toString();
    }
    public static String binaryToString(String binary) {
        // 移除所有空格，确保处理的是连续的二进制流
        binary = binary.replaceAll("\\s+", "");

        // 从右往左，每 16 位提取
        StringBuilder result = new StringBuilder();
        int length = binary.length();

        for (int i = length; i >= 16; i -= 16) {
            String byteStr = binary.substring(i - 16, i); // 获取 16 位二进制字符串

            // 转换为 UTF-16 字符
            int decimal = Integer.parseInt(byteStr, 2);
            result.insert(0, (char) decimal);  // 插入到前面，保持顺序
        }

        return result.toString();
    }

    public static String elementToHash(Element element) {
        try {
            // 1. 获取 Element 的字节表示
            byte[] elementBytes = element.toBytes();

            // 2. 计算 SHA-256 哈希值
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(elementBytes);

            // 3. 将哈希字节转换为二进制字符串
            StringBuilder binaryString = new StringBuilder();
            for (byte b : hashBytes) {
                binaryString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            }
            return binaryString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("哈希算法不可用", e);
        }
    }

    public static String xorHashes(String bin1, String bin2) {
        // 1. 让两个二进制字符串长度相等（前面补 0）
        int maxLength = Math.max(bin1.length(), bin2.length());
        bin1 = String.format("%" + maxLength + "s", bin1).replace(' ', '0');
        bin2 = String.format("%" + maxLength + "s", bin2).replace(' ', '0');

        // 2. 进行逐位异或
        StringBuilder xorResult = new StringBuilder();
        for (int i = 0; i < maxLength; i++) {
            xorResult.append(bin1.charAt(i) ^ bin2.charAt(i)); // XOR 操作
        }

        return xorResult.toString();
    }

    public static List<String> string2attributeSet(String attributes) {
        // 移除括号，分割字符串
        String[] attrs = attributes.replaceAll("[()]", "").split(",");
        List<String> attributeList = new ArrayList<>();
        for (String attr : attrs) {
            attributeList.add(attr.trim());
        }
        return attributeList;
    }

    public static List<Integer> fetchRows(List<String> attr, List<String> labels) {
        List<Integer> I= new ArrayList<>();
        for (int i = 0; i < labels.size(); ++i) {
            if (attr.contains(labels.get(i))) {
                I.add(i);
            }
        }
        return I;
    }

    public static void solve(List<Element> res, List<List<Element>> matrix, Pairing pairing) {
        int rows = matrix.size();
        int cols = matrix.get(0).size();

        for (int j = 0; j < cols - 1; ++j) { // 遍历列
            int i;
            for (i = j; i < rows; ++i) { // 找到非0元素
                if (!matrix.get(i).get(j).isZero()) break;
            }

            if (i == rows) continue; // 无解情况

            // 交换当前行和找到的非零行
            Collections.swap(matrix, i, j);

            // 归一化 A[j][j]
            Element div = matrix.get(j).get(j).invert();
            for (int k = j; k < cols; ++k) {
                matrix.get(j).set(k, matrix.get(j).get(k).duplicate().mul(div));
            }

            // 消元
            for (i = 0; i < rows; ++i) {
                if (i != j) {
                    Element factor = matrix.get(i).get(j).duplicate();
                    for (int k = j; k < cols; ++k) {
                        Element temp = matrix.get(j).get(k).duplicate().mul(factor);
                        matrix.get(i).set(k, matrix.get(i).get(k).duplicate().sub(temp));
                    }
                }
            }
        }

        // 提取解向量
        for (List<Element> row : matrix) {
            res.add(row.get(cols - 1).duplicate());
        }
    }

}
