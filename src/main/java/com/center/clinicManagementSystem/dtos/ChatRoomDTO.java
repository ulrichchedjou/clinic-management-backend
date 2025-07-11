package com.center.clinicManagementSystem.dtos;

import com.center.clinicManagementSystem.enums.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDTO {
    private Long id;
    private String roomId;
    private String name;
    private ChatRoomType type;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<ChatParticipantDTO> participants;
    private ChatMessageDTO lastMessage;
    private Long unreadCount;
}
