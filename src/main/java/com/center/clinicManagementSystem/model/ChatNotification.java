package com.center.clinicManagementSystem.model;

import com.center.clinicManagementSystem.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatNotification {
    private String type; // NEW_MESSAGE, USER_JOINED, USER_LEFT, TYPING, etc.
    private String roomId;
    private String senderName;
    private String senderEmail;
    private String content;
    private MessageType messageType;
    private LocalDateTime timestamp;
    private Object data;
}
