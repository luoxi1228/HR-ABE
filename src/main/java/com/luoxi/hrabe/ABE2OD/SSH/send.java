package com.luoxi.hrabe.ABE2OD.SSH;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;

public class send {
    public static String sendJson(String jsonString,SSHClient sshClient, String url) {
        ObjectMapper objectMapper = new ObjectMapper();
        String output = "";
        int i=0;
        try {
            // 解析 JSON 字符串
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 遍历 JSON 中的所有键
            Iterator<String> fieldNames = rootNode.fieldNames();

            while (fieldNames.hasNext()) {
                String key = fieldNames.next();
                String value = rootNode.get(key).asText();

                // 组织请求 JSON 数据
                String jsonData = "{\"data\":\"" + value + "\"}";

                // 发送 POST 请求
                String response = sshClient.sendPostRequest(url, jsonData);
                i++;
                if(i==6){
                    output=response;
                }
                System.out.println("POST Response: " + response);
            }
        } catch (IOException e) {
            System.err.println("Error parsing JSON input: " + e.getMessage());
        }
        return output;
    }

    public static String mergeJson(String json1, String json2) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // 转换为 JsonNode
            JsonNode node1 = objectMapper.readTree(json1);
            JsonNode node2 = objectMapper.readTree(json2);

            // 合并 JSON
            ObjectNode mergedNode = (ObjectNode) node1;
            mergedNode.setAll((ObjectNode) node2);

            // 转换回字符串
            return objectMapper.writeValueAsString(mergedNode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

}
    // 将 Base64 格式的 JSON 文件转换为字节流
    public static byte[] convertBase64JsonToBytes(String jsonString) {
        try {
            // 解析 JSON 字符串
            JSONObject json = new JSONObject(jsonString);

            // 获取 Base64 字符串
            String base64Str = json.optString("Message");
            if (base64Str.isEmpty() || base64Str.equals("error")) {
                return null;  // 如果为空或为 "error"，直接返回 null
            }else {
                // 解码 Base64 字符串为字节数组
                return Base64.getDecoder().decode(base64Str);
            }
        } catch (Exception e) {
            System.out.println("解析错误: " + e.getMessage());
        }
        return null;
    }

}
