package com.center.clinicManagementSystem.dtos;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class SendMessageRequest {
    @NotBlank(message = "L'ID de la room est obligatoire")
    private String roomId;
    
    @NotBlank(message = "Le contenu du message est obligatoire")
    private String content;
    
    @NotNull(message = "Le type de message est obligatoire")
    private MessageType type;
    
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private Long replyToId;
}
