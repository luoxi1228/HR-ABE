package com.luoxi.hrabe.Util;

import com.luoxi.hrabe.HRABE.param.MPK_h;
import com.luoxi.hrabe.ABE2OD.param.*;
import it.unisa.dia.gas.jpbc.Element;

import java.io.*;
import java.util.Base64;
import it.unisa.dia.gas.jpbc.Pairing;

public class MPK_hSerializer {

    // 将 MPK_h 对象序列化为字节数组
    public static byte[] serializeMPK_h(MPK_h mpk_h) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            if (mpk_h == null) {
                dataOutputStream.writeInt(0); // 标记空对象
            } else {
                dataOutputStream.writeInt(1); // 标记非空对象
                serializePK(dataOutputStream, mpk_h.getPk());
                writeString(dataOutputStream, mpk_h.getVk());
            }

            dataOutputStream.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("MPK_h 序列化失败", e);
        }
    }

    // 反序列化 MPK_h（需要 Pairing）
    public static MPK_h deserializeMPK_h(byte[] data, Pairing pairing) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            int flag = dataInputStream.readInt();
            if (flag == 0) {
                return null;
            }

            MPK_h mpk_h = new MPK_h();
            mpk_h.setPk(deserializePK(dataInputStream, pairing));
            mpk_h.setVk(readString(dataInputStream));

            return mpk_h;
        } catch (IOException e) {
            throw new RuntimeException("MPK_h 反序列化失败", e);
        }
    }

    // 序列化 PK
    private static void serializePK(DataOutputStream dataOutputStream, PK pk) throws IOException {
        if (pk == null) {
            dataOutputStream.writeInt(0);
        } else {
            dataOutputStream.writeInt(1);
            writeElement(dataOutputStream, pk.getG());
            writeElement(dataOutputStream, pk.getEggalpha());
            writeElement(dataOutputStream, pk.getGa());
        }
    }

    // 反序列化 PK（需要 Pairing）
    private static PK deserializePK(DataInputStream dataInputStream, Pairing pairing) throws IOException {
        int flag = dataInputStream.readInt();
        if (flag == 0) {
            return null;
        }

        PK pk = new PK();
        pk.setG(readElement(dataInputStream, pairing, "G1"));
        pk.setEggalpha(readElement(dataInputStream, pairing, "GT"));
        pk.setGa(readElement(dataInputStream, pairing, "G1"));

        return pk;
    }

    // 写入字符串
    private static void writeString(DataOutputStream dataOutputStream, String str) throws IOException {
        if (str == null) {
            dataOutputStream.writeInt(0);
        } else {
            byte[] strBytes = str.getBytes("UTF-8");
            dataOutputStream.writeInt(strBytes.length);
            dataOutputStream.write(strBytes);
        }
    }

    // 读取字符串
    private static String readString(DataInputStream dataInputStream) throws IOException {
        int length = dataInputStream.readInt();
        if (length == 0) {
            return null;
        }
        byte[] strBytes = new byte[length];
        dataInputStream.readFully(strBytes);
        return new String(strBytes, "UTF-8");
    }

    // 写入 Element
    private static void writeElement(DataOutputStream dataOutputStream, Element element) throws IOException {
        if (element == null) {
            dataOutputStream.writeInt(0);
        } else {
            byte[] elementBytes = element.toBytes();
            dataOutputStream.writeInt(elementBytes.length);
            dataOutputStream.write(elementBytes);
        }
    }

    // 读取 Element（需要 Pairing）
    private static Element readElement(DataInputStream dataInputStream, Pairing pairing, String groupType) throws IOException {
        int length = dataInputStream.readInt();
        if (length == 0) {
            return null;
        }
        byte[] elementBytes = new byte[length];
        dataInputStream.readFully(elementBytes);

        // 根据群类型恢复 Element
        if ("G1".equals(groupType)) {
            return pairing.getG1().newElementFromBytes(elementBytes);
        } else if ("GT".equals(groupType)) {
            return pairing.getGT().newElementFromBytes(elementBytes);
        } else {
            throw new IllegalArgumentException("未知的群类型: " + groupType);
        }
    }

    // 将 MPK_h 对象序列化并转换为 Base64 字符串
    public static String MPK2String(MPK_h mpk_h) {
        byte[] serializedData = serializeMPK_h(mpk_h);
        return Base64.getEncoder().encodeToString(serializedData);
    }

    // 将 Base64 字符串转换回 MPK_h 对象（需要 Pairing）
    public static MPK_h String2MPK(String base64String, Pairing pairing) {
        byte[] data = Base64.getDecoder().decode(base64String);
        return deserializeMPK_h(data, pairing);
    }
}


