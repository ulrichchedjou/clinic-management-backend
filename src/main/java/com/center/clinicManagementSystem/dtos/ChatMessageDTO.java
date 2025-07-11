package com.center.clinicManagementSystem.dtos;

import com.center.clinicManagementSystem.enums.MessageType;
import com.center.clinicManagementSystem.enums.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private Long id;
    private String roomId;
    private String senderName;
    private String senderEmail;
    private String content;
    private MessageType type;
    private MessageStatus status;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private Boolean isEdited;
    private LocalDateTime editedAt;
    private ChatMessageDTO replyTo;
    private LocalDateTime createdAt;
}
