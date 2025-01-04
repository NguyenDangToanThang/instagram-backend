package com.microservices.instagrambackend.web.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);
    // Lưu trữ session theo userId
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Lấy userId từ session attributes (đã được set bởi interceptor)
        String userId = (String) session.getAttributes().get("email");
        if (userId != null) {
            userSessions.put(userId, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info(message.getPayload());
        // Parse message từ client
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        // Lấy userId của người gửi từ session
        String senderId = (String) session.getAttributes().get("email");
        if (senderId == null) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        // Tạo message để gửi
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setSenderId(senderId);
        messageResponse.setMessage(chatMessage.getMessage());
        messageResponse.setTimestamp(System.currentTimeMillis());

        String jsonMessage = objectMapper.writeValueAsString(messageResponse);

        // Gửi tin nhắn cho người nhận
        WebSocketSession receiverSession = userSessions.get(chatMessage.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(jsonMessage));
        }

        // Gửi xác nhận lại cho người gửi
        session.sendMessage(new TextMessage(jsonMessage));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Xóa session khi người dùng disconnect
        String userId = (String) session.getAttributes().get("email");
        if (userId != null) {
            userSessions.remove(userId);
        }
    }
}

@Data
class ChatMessage {
    private String receiverId;  // ID người nhận
    private String message;     // Nội dung tin nhắn
}

@Data
class MessageResponse {
    private String senderId;    // ID người gửi
    private String message;     // Nội dung tin nhắn
    private long timestamp;     // Thời gian gửi
}