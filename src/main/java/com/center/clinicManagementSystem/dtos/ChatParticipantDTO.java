package com.center.clinicManagementSystem.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import com.center.clinicManagementSystem.enums.ParticipantRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipantDTO {
    private Long id;
    private String userName;
    private String userEmail;
    private ParticipantRole role;
    private LocalDateTime joinedAt;
    private LocalDateTime lastReadAt;
    private Boolean isActive;
    private Boolean isOnline;
}
