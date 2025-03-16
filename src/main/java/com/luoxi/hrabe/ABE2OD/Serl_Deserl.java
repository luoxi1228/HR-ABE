package com.luoxi.hrabe.ABE2OD;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luoxi.hrabe.ABE2OD.param.*;
import com.luoxi.hrabe.HRABE.param.UL;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Serl_Deserl {
    // 将 PTC 对象序列化为 JSON
    public static String serializePTC2Json(PTC ptc) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(new PTCJson(ptc));
    }
    public static PTC deserializeJson2PTC(String json, Pairing pairing) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);

            String c0Base64 = rootNode.get("C0").asText();
            String c1String = rootNode.get("C1").asText();
            String cp1Base64 = rootNode.get("CP1").asText();
            String cp2Base64 = rootNode.get("CP2").asText();

            Element C0 = pairing.getGT().newElement();
            Element CP1 = pairing.getGT().newElement();
            Element CP2 = pairing.getGT().newElement();

            C0.setFromBytes(Base64.getDecoder().decode(c0Base64));
            CP1.setFromBytes(Base64.getDecoder().decode(cp1Base64));
            CP2.setFromBytes(Base64.getDecoder().decode(cp2Base64));

            return new PTC(C0, c1String, CP1, CP2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 将 HK 对象序列化为 JSON
    public static String serializeHK2Json(HK hk) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(new HKJson(hk));
    }
    public static HK deserializeJson2HK(String json, Pairing pairing) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        String gamma_1Base64 = rootNode.get("gamma_1").asText();
        String gamma_2Base64 = rootNode.get("gamma_2").asText();

        Element gamma_1 = pairing.getGT().newElement();
        Element gamma_2 = pairing.getGT().newElement();

        gamma_1.setFromBytes(Base64.getDecoder().decode(gamma_1Base64));
        gamma_2.setFromBytes(Base64.getDecoder().decode(gamma_2Base64));

        return new HK(gamma_1,gamma_2);
    }

    // 反序列化TC
    public static TC deserl_TC( byte[] str, Pairing pairing) {
        try {
            TC tc =new TC();
            // 使用 ByteBuffer 方便处理字节流
            ByteBuffer buffer = ByteBuffer.wrap(str);

            // 获取 T1 的长度（前两个字节，大端序）
            int T1_size = ((buffer.get() & 0xFF) << 8) | (buffer.get() & 0xFF);
            //System.out.println("T1_size: " + T1_size);

            // 获取 GT 群元素的字节大小
            int GT_SIZE = pairing.getGT().newElement().getLengthInBytes();
           // System.out.println("GT_SIZE: " + GT_SIZE);

            // 读取 T0 部分 (GT 群元素)
            byte[] t0Bytes = new byte[GT_SIZE];
            buffer.get(t0Bytes);
            Element T0 = pairing.getGT().newElement();
            T0.setFromBytes(t0Bytes);
            tc.setT0(T0);

            // 读取 T1 部分 (普通字符串)
            byte[] t1Bytes = new byte[T1_size];
            buffer.get(t1Bytes);
            // 直接将字节数组转换为字符串
            String T1 = new String(t1Bytes, StandardCharsets.US_ASCII);
            tc.setT1(T1);

            // 读取 T2 部分 (GT 群元素)
            byte[] t2Bytes = new byte[GT_SIZE];
            buffer.get(t2Bytes);
            Element T2 = pairing.getGT().newElement();
            T2.setFromBytes(t2Bytes);
            tc.setT2(T2);

            //System.out.println("反序列化完成!");
            return tc;
        } catch (Exception e) {
            System.out.println("TC反序列化错误: " + e.getMessage());
            return null;
        }
    }

    // 序列化TK
    public static byte[] serializeTK(TK tk) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            if (tk == null) {
                dataOutputStream.writeInt(0); // 标记空对象
            } else {
                dataOutputStream.writeInt(1); // 标记非空对象
                writeString(dataOutputStream, tk.getAttributes());
                writeElement(dataOutputStream, tk.getK());
                writeElement(dataOutputStream, tk.getL());

                // 序列化 Ky (Map<String, Element>)
                if (tk.getKy() != null) {
                    dataOutputStream.writeInt(tk.getKy().size()); // 写入 Map 大小
                    for (Map.Entry<String, Element> entry : tk.getKy().entrySet()) {
                        writeString(dataOutputStream, entry.getKey());
                        writeElement(dataOutputStream, entry.getValue());
                    }
                } else {
                    dataOutputStream.writeInt(0);
                }
            }

            dataOutputStream.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("TK 序列化失败", e);
        }
    }

    // 反序列化TK
    public static TK deserializeTK(byte[] data, Pairing pairing) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            int flag = dataInputStream.readInt();
            if (flag == 0) return null; // 说明对象是 null

            TK tk = new TK();
            tk.setAttributes(readString(dataInputStream));
            tk.setK(readElement(dataInputStream, pairing.getG1()));
            tk.setL(readElement(dataInputStream, pairing.getG1()));

            // 反序列化 Ky (Map<String, Element>)
            int mapSize = dataInputStream.readInt();
            Map<String, Element> kyMap = new HashMap<>();
            for (int i = 0; i < mapSize; i++) {
                String key = readString(dataInputStream);
                Element value = readElement(dataInputStream, pairing.getG1());
                kyMap.put(key, value);
            }
            tk.setKy(kyMap);

            return tk;
        } catch (IOException e) {
            throw new RuntimeException("TK 反序列化失败", e);
        }
    }

    //序列化HK
    public static byte[] serializeHK(HK hk) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            if (hk == null) {
                dataOutputStream.writeInt(0); // 标记空对象
            } else {
                dataOutputStream.writeInt(1); // 标记非空对象
                writeElement(dataOutputStream, hk.getGamma_1());
                writeElement(dataOutputStream, hk.getGamma_2());
            }

            dataOutputStream.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("HK 序列化失败", e);
        }
    }

    // 反序列化HK
    public static HK deserializeHK(byte[] data, Pairing pairing) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            int flag = dataInputStream.readInt();
            if (flag == 0) return null; // 说明对象是 null

            HK hk = new HK();
            hk.setGamma_1(readElement(dataInputStream, pairing.getZr()));
            hk.setGamma_2(readElement(dataInputStream, pairing.getZr()));

            return hk;
        } catch (IOException e) {
            throw new RuntimeException("HK 反序列化失败", e);
        }
    }



    //签名序列化
    public static byte[] serializeULListAndIndex(List<UL> ulList, String index, Pairing pairing) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            // 1. 序列化 index
            writeString(dataOutputStream, index);

            // 2. 序列化 ulList
            if (ulList != null) {
                dataOutputStream.writeInt(ulList.size()); // 先写入列表大小
                for (UL ul : ulList) {
                    writeUL(dataOutputStream, ul, pairing);
                }
            } else {
                dataOutputStream.writeInt(0);
            }

            dataOutputStream.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("UL 列表和 index 序列化失败", e);
        }
    }

    // 处理 UL 序列化
    private static void writeUL(DataOutputStream out, UL ul, Pairing pairing) throws IOException {
        if (ul == null) {
            out.writeInt(0);
        } else {
            out.writeInt(1);
            writeString(out, ul.getID());
            writeString(out, ul.getA());

            // 序列化 TK 和 HK
            out.write(serializeTK(ul.getTk1()));
            out.write(serializeTK(ul.getTk2()));
            out.write(serializeHK(ul.getHk()));
        }
    }

    // 处理 Element -> byte[]
    private static void writeElement(DataOutputStream out, Element element) throws IOException {
        if (element == null) {
            out.writeInt(0);
        } else {
            byte[] bytes = element.toBytes();
            out.writeInt(bytes.length);
            out.write(bytes);
        }
    }

    // 读取 byte[] -> Element
    private static Element readElement(DataInputStream in, Field field) throws IOException {
        int length = in.readInt();
        if (length == 0) return null;

        byte[] bytes = new byte[length];
        in.readFully(bytes);
        return field.newElementFromBytes(bytes);
    }

    // 处理 String -> byte[]
    private static void writeString(DataOutputStream out, String str) throws IOException {
        if (str == null) {
            out.writeInt(0);
        } else {
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            out.writeInt(bytes.length);
            out.write(bytes);
        }
    }

    // 读取 byte[] -> String
    private static String readString(DataInputStream in) throws IOException {
        int length = in.readInt();
        if (length == 0) return null;

        byte[] bytes = new byte[length];
        in.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
