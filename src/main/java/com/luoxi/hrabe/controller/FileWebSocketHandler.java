package com.luoxi.hrabe.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Configuration
@EnableWebSocket
public class FileWebSocketHandler extends TextWebSocketHandler implements WebSocketConfigurer {

    // 使用 CopyOnWriteArrayList 存储所有活跃的 WebSocket 会话，支持并发操作
    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(this, "/ws").setAllowedOrigins("*"); // 允许所有跨域请求
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("WebSocket 连接成功, sessionId: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("WebSocket 连接关闭，sessionId: " + session.getId());
    }

    // 广播消息给所有连接的客户端，包含 fileName
    public static void sendStatus(String step, boolean success, String message, String fileName) {
        System.out.println("准备广播 WebSocket 消息 -> fileName: " + fileName);

        String json = String.format("{\"step\": \"%s\", \"success\": %b, \"message\": \"%s\", \"fileName\": \"%s\"}",
                step, success, message, fileName);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(json));
                    System.out.println("消息发送成功: " + json + " -> sessionId: " + session.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}