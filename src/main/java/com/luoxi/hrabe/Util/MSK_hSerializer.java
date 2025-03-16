package com.luoxi.hrabe.Util;

import com.luoxi.hrabe.HRABE.param.MSK_h;
import it.unisa.dia.gas.jpbc.Pairing;
import com.luoxi.hrabe.ABE2OD.param.*;
import it.unisa.dia.gas.jpbc.Element;

import java.io.*;
import java.util.Base64;
import it.unisa.dia.gas.jpbc.Pairing;

public class MSK_hSerializer {

    // 将 MSK_h 对象序列化为字节数组
    public static byte[] serializeMSK_h(MSK_h msk_h) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            if (msk_h == null) {
                dataOutputStream.writeInt(0); // 标记空对象
            } else {
                dataOutputStream.writeInt(1); // 标记非空对象
                serializeMSK(dataOutputStream, msk_h.getMsk());
                writeString(dataOutputStream, msk_h.getSk());
            }

            dataOutputStream.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("MSK_h 序列化失败", e);
        }
    }

    // 反序列化 MSK_h（需要 Pairing）
    public static MSK_h deserializeMSK_h(byte[] data, Pairing pairing) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            int flag = dataInputStream.readInt();
            if (flag == 0) {
                return null;
            }

            MSK_h msk_h = new MSK_h();
            msk_h.setMsk(deserializeMSK(dataInputStream, pairing));
            msk_h.setSk(readString(dataInputStream));

            return msk_h;
        } catch (IOException e) {
            throw new RuntimeException("MSK_h 反序列化失败", e);
        }
    }

    // 序列化 MSK
    private static void serializeMSK(DataOutputStream dataOutputStream, MSK msk) throws IOException {
        if (msk == null) {
            dataOutputStream.writeInt(0);
        } else {
            dataOutputStream.writeInt(1);
            writeElement(dataOutputStream, msk.getGalpha());
        }
    }

    // 反序列化 MSK（需要 Pairing）
    private static MSK deserializeMSK(DataInputStream dataInputStream, Pairing pairing) throws IOException {
        int flag = dataInputStream.readInt();
        if (flag == 0) {
            return null;
        }

        MSK msk = new MSK();
        msk.setGalpha(readElement(dataInputStream, pairing, "G1"));

        return msk;
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
        } else {
            throw new IllegalArgumentException("未知的群类型: " + groupType);
        }
    }

    // 将 MSK_h 对象序列化并转换为 Base64 字符串
    public static String MSK2String(MSK_h msk_h) {
        byte[] serializedData = serializeMSK_h(msk_h);
        return Base64.getEncoder().encodeToString(serializedData);
    }

    // 将 Base64 字符串转换回 MSK_h 对象（需要 Pairing）
    public static MSK_h String2MSK(String base64String, Pairing pairing) {
        byte[] data = Base64.getDecoder().decode(base64String);
        return deserializeMSK_h(data, pairing);
    }
}

