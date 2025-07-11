package com.center.clinicManagementSystem.dtos;
import com.center.clinicManagementSystem.enums.ChatRoomType;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Data
public class CreateChatRoomRequest {
    @NotBlank(message = "Le nom de la room est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String name;
    
    @NotNull(message = "Le type de room est obligatoire")
    private ChatRoomType type;
    
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
    
    private List<String> participantEmails;
}
