package com.luoxi.hrabe.ABE2OD.SSH;

import com.jcraft.jsch.*;

import java.io.*;

public class SSHClient {
    private String user;
    private String host;
    private int port;
    private String password;

    public SSHClient(String user, String host, int port, String password) {
        this.user = user;
        this.host = host;
        this.port = port;
        this.password = password;
    }

    public String executeCommand(String command) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);

            InputStream input = channel.getInputStream();
            channel.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder outputBuffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                outputBuffer.append(line).append("\n");
            }

            channel.disconnect();
            session.disconnect();

            return outputBuffer.toString();
        } catch (Exception e) {
            return "SSH Error: " + e.getMessage();
        }
    }

    public String sendPostRequest(String url, String jsonData) {
        String command = "curl -X POST " + url + " -H \"Content-Type: application/json\" -d '" + jsonData + "'";
        return executeCommand(command);
    }

    public String sendGetRequest(String url) {
        String command = "curl -X GET " + url;
        return executeCommand(command);
    }


//    public static void main(String[] args) {
//        SSHClient sshClient = new SSHClient("lx", "10.242.175.231", 10021, "qymshkm#:\"ZHkcNDV6TQq");
//        String url="http://localhost:5000/encrypt";
//        String jsonPath="D:\\ASUS\\Java\\HR-ABE\\src\\main\\java\\com\\luoxi\\hrabe\\utils\\ptc_data.json";
//        //String jsonData = "{\"data\":\"luoxi\"}";
//        //String postResponse = sshClient.sendPostRequest(url, jsonData);
//       // System.out.println("POST Response: " + postResponse);
//
//        send.sendPTC(jsonPath,sshClient,url);
//
////        String getResponse = sshClient.sendGetRequest("http://localhost:5000/status");
////        System.out.println("GET Response: " + getResponse);
//    }
}
