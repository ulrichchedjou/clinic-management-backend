package com.center.clinicManagementSystem.service;

import com.center.clinicManagementSystem.dtos.*;
import com.center.clinicManagementSystem.enums.*;
import com.center.clinicManagementSystem.model.*;
import com.center.clinicManagementSystem.model.ChatNotification;
import com.center.clinicManagementSystem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;

    @Transactional
    public ChatRoomDTO createChatRoom(CreateChatRoomRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier les permissions selon le rôle
        validateRoomCreationPermission(creator, request.getType());

        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(UUID.randomUUID().toString())
                .name(request.getName())
                .type(request.getType())
                .description(request.getDescription())
                .isActive(true)
                .build();

        chatRoom = chatRoomRepository.save(chatRoom);

        // Ajouter le créateur comme admin
        addParticipant(chatRoom, creator, ParticipantRole.ADMIN);

        // Ajouter les autres participants
        if (request.getParticipantEmails() != null) {
            for (String email : request.getParticipantEmails()) {
                userRepository.findByEmail(email).ifPresent(user -> {
                    if (!user.equals(creator)) {
                        addParticipant(chatRoom, user, ParticipantRole.MEMBER);
                    }
                });
            }
        }

        // Notification de création
        notifyRoomCreation(chatRoom, creator);

        return convertToRoomDTO(chatRoom);
    }

    @Transactional
    public ChatMessageDTO sendMessage(SendMessageRequest request, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Salon non trouvé"));

        // Vérifier que l'utilisateur est participant
        ChatParticipant participant = chatParticipantRepository
                .findByRoomIdAndUserEmail(request.getRoomId(), senderEmail)
                .orElseThrow(() -> new RuntimeException("Vous n'êtes pas autorisé à envoyer des messages dans ce salon"));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(request.getContent())
                .type(request.getType())
                .status(MessageStatus.SENT)
                .fileUrl(request.getFileUrl())
                .fileName(request.getFileName())
                .fileSize(request.getFileSize())
                .build();

        // Gestion des réponses
        if (request.getReplyToId() != null) {
            ChatMessage replyTo = chatMessageRepository.findById(request.getReplyToId())
                    .orElseThrow(() -> new RuntimeException("Message de réponse non trouvé"));
            message.setReplyTo(replyTo);
        }

        message = chatMessageRepository.save(message);

        // Notification en temps réel
        ChatNotification notification = ChatNotification.builder()
                .type(NotificationType.NEW_MESSAGE.name())
                .roomId(chatRoom.getRoomId())
                .senderName(sender.getFullName())
                .senderEmail(sender.getEmail())
                .content(message.getContent())
                .messageType(message.getType())
                .timestamp(LocalDateTime.now())
                .build();

        broadcastToRoom(chatRoom.getRoomId(), notification);

        // Notifications push pour les participants hors ligne
        notifyOfflineParticipants(chatRoom, message, sender);

        return convertToMessageDTO(message);
    }

    @Transactional
    public ChatMessageDTO uploadFile(String roomId, MultipartFile file, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Salon non trouvé"));

        // Vérifier les permissions
        chatParticipantRepository.findByRoomIdAndUserEmail(roomId, senderEmail)
                .orElseThrow(() -> new RuntimeException("Vous n'êtes pas autorisé à envoyer des fichiers dans ce salon"));

        // Stocker le fichier
        String fileUrl = fileStorageService.storeFile(file);

        // Déterminer le type de message selon le fichier
        MessageType messageType = determineMessageType(file.getContentType());

        SendMessageRequest request = new SendMessageRequest();
        request.setRoomId(roomId);
        request.setContent("Fichier partagé: " + file.getOriginalFilename());
        request.setType(messageType);
        request.setFileUrl(fileUrl);
        request.setFileName(file.getOriginalFilename());
        request.setFileSize(file.getSize());

        return sendMessage(request, senderEmail);
    }

    public Page<ChatMessageDTO> getMessages(String roomId, String userEmail, Pageable pageable) {
        // Vérifier les permissions
        chatParticipantRepository.findByRoomIdAndUserEmail(roomId, userEmail)
                .orElseThrow(() -> new RuntimeException("Vous n'avez pas accès à ce salon"));

        Page<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);

        return messages.map(this::convertToMessageDTO);
    }

    public List<ChatRoomDTO> getUserRooms(String userEmail) {
        List<ChatParticipant> participants = chatParticipantRepository.findActiveByUserEmail(userEmail);

        return participants.stream()
                .map(p -> convertToRoomDTO(p.getChatRoom()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(String roomId, String userEmail) {
        chatParticipantRepository.updateLastReadAt(roomId, userEmail, LocalDateTime.now());

        // Notification de lecture
        ChatNotification notification = ChatNotification.builder()
                .type("MESSAGE_READ")
                .roomId(roomId)
                .senderEmail(userEmail)
                .timestamp(LocalDateTime.now())
                .build();

        broadcastToRoom(roomId, notification);
    }

    @Transactional
    public void joinRoom(String roomId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Salon non trouvé"));

        // Vérifier si l'utilisateur peut rejoindre ce salon
        validateJoinPermission(user, chatRoom);

        Optional<ChatParticipant> existingParticipant = chatParticipantRepository
                .findByRoomIdAndUserEmail(roomId, userEmail);

        if (existingParticipant.isPresent()) {
            // Réactiver si inactif
            ChatParticipant participant = existingParticipant.get();
            if (!participant.getIsActive()) {
                participant.setIsActive(true);
                participant.setJoinedAt(LocalDateTime.now());
                chatParticipantRepository.save(participant);
            }
        } else {
            // Créer nouveau participant
            addParticipant(chatRoom, user, ParticipantRole.MEMBER);
        }

        // Notification de participation
        ChatNotification notification = ChatNotification.builder()
                .type(NotificationType.USER_JOINED.name())
                .roomId(roomId)
                .senderName(user.getFullName())
                .senderEmail(user.getEmail())
                .content(user.getFullName() + " a rejoint le salon")
                .timestamp(LocalDateTime.now())
                .build();

        broadcastToRoom(roomId, notification);
    }

    @Transactional
    public void leaveRoom(String roomId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        chatParticipantRepository.leaveRoom(roomId, userEmail);

        // Notification de départ
        ChatNotification notification = ChatNotification.builder()
                .type(NotificationType.USER_LEFT.name())
                .roomId(roomId)
                .senderName(user.getFullName())
                .senderEmail(user.getEmail())
                .content(user.getFullName() + " a quitté le salon")
                .timestamp(LocalDateTime.now())
                .build();

        broadcastToRoom(roomId, notification);
    }

    public Long getUnreadCount(String roomId, String userEmail) {
        return chatMessageRepository.countUnreadMessages(roomId, userEmail);
    }

    public List<ChatMessageDTO> searchMessages(String roomId, String searchTerm, String userEmail) {
        // Vérifier les permissions
        chatParticipantRepository.findByRoomIdAndUserEmail(roomId, userEmail)
                .orElseThrow(() -> new RuntimeException("Vous n'avez pas accès à ce salon"));

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Salon non trouvé"));

        List<ChatMessage> messages = chatMessageRepository.searchMessagesInRoom(chatRoom, searchTerm);

        return messages.stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
    }

    // Méthodes utilitaires privées

    private void validateRoomCreationPermission(User creator, ChatRoomType roomType) {
        UserRole role = creator.getRole();

        switch (roomType) {
            case EMERGENCY:
                if (role != UserRole.DOCTOR && role != UserRole.ADMIN) {
                    throw new RuntimeException("Seuls les médecins et administrateurs peuvent créer des salons d'urgence");
                }
                break;
            case ADMINISTRATIVE:
                if (role != UserRole.ADMIN && role != UserRole.RECEPTIONIST) {
                    throw new RuntimeException("Seuls les administrateurs et réceptionnistes peuvent créer des salons administratifs");
                }
                break;
            case MEDICAL_TEAM:
                if (role != UserRole.DOCTOR && role != UserRole.ADMIN) {
                    throw new RuntimeException("Seuls les médecins et administrateurs peuvent créer des salons d'équipe médicale");
                }
                break;
        }
    }

    private void validateJoinPermission(User user, ChatRoom chatRoom) {
        UserRole role = user.getRole();
        ChatRoomType roomType = chatRoom.getType();

        switch (roomType) {
            case EMERGENCY:
                if (role != UserRole.DOCTOR && role != UserRole.ADMIN && role != UserRole.RECEPTIONIST) {
                    throw new RuntimeException("Accès limité aux personnels médicaux et administratifs");
                }
                break;
            case MEDICAL_TEAM:
                if (role != UserRole.DOCTOR && role != UserRole.ADMIN) {
                    throw new RuntimeException("Accès limité aux médecins et administrateurs");
                }
                break;
        }
    }

    private void addParticipant(ChatRoom chatRoom, User user, ParticipantRole role) {
        ChatParticipant participant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(user)
                .role(role)
                .joinedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        chatParticipantRepository.save(participant);
    }

    private MessageType determineMessageType(String contentType) {
        if (contentType == null) return MessageType.FILE;

        if (contentType.startsWith("image/")) return MessageType.IMAGE;
        if (contentType.startsWith("video/")) return MessageType.VIDEO;
        if (contentType.startsWith("audio/")) return MessageType.AUDIO;
        if (contentType.equals("application/pdf") || contentType.startsWith("application/msword")) {
            return MessageType.DOCUMENT;
        }

        return MessageType.FILE;
    }

    private void broadcastToRoom(String roomId, ChatNotification notification) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId, notification);
    }

    private void notifyRoomCreation(ChatRoom chatRoom, User creator) {
        ChatNotification notification = ChatNotification.builder()
                .type(NotificationType.ROOM_CREATED.name())
                .roomId(chatRoom.getRoomId())
                .senderName(creator.getFullName())
                .senderEmail(creator.getEmail())
                .content("Salon créé: " + chatRoom.getName())
                .timestamp(LocalDateTime.now())
                .build();

        broadcastToRoom(chatRoom.getRoomId(), notification);
    }

    private void notifyOfflineParticipants(ChatRoom chatRoom, ChatMessage message, User sender) {
        List<String> participantEmails = chatParticipantRepository
                .findParticipantEmailsExcluding(chatRoom.getRoomId(), sender.getEmail());

        for (String email : participantEmails) {
            notificationService.sendPushNotification(email,
                    "Nouveau message de " + sender.getFullName(),
                    message.getContent());
        }
    }

    private ChatRoomDTO convertToRoomDTO(ChatRoom chatRoom) {
        List<ChatParticipantDTO> participants = chatRoom.getParticipants().stream()
                .filter(ChatParticipant::getIsActive)
                .map(this::convertToParticipantDTO)
                .collect(Collectors.toList());

        Optional<ChatMessage> lastMessage = chatMessageRepository
                .findLastMessageByRoomId(chatRoom.getRoomId());

        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .roomId(chatRoom.getRoomId())
                .name(chatRoom.getName())
                .type(chatRoom.getType())
                .description(chatRoom.getDescription())
                .isActive(chatRoom.getIsActive())
                .participants(participants)
                .lastMessage(lastMessage.map(this::convertToMessageDTO).orElse(null))
                .messageCount(chatMessageRepository.countMessagesByRoomId(chatRoom.getRoomId()))
                .createdAt(chatRoom.getCreatedAt())
                .updatedAt(chatRoom.getUpdatedAt())
                .build();
    }

    private ChatMessageDTO convertToMessageDTO(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .roomId(message.getChatRoom().getRoomId())
                .senderName(message.getSender().getFullName())
                .senderEmail(message.getSender().getEmail())
                .content(message.getContent())
                .type(message.getType())
                .status(message.getStatus())
                .fileUrl(message.getFileUrl())
                .fileName(message.getFileName())
                .fileSize(message.getFileSize())
                .isEdited(message.getIsEdited())
                .editedAt(message.getEditedAt())
                .replyTo(message.getReplyTo() != null ? convertToMessageDTO(message.getReplyTo()) : null)
                .createdAt(message.getCreatedAt())
                .build();
    }

    private ChatParticipantDTO convertToParticipantDTO(ChatParticipant participant) {
        return ChatParticipantDTO.builder()
                .id(participant.getId())
                .userName(participant.getUser().getFullName())
                .userEmail(participant.getUser().getEmail())
                .role(participant.getRole())
                .joinedAt(participant.getJoinedAt())
                .lastReadAt(participant.getLastReadAt())
                .isActive(participant.getIsActive())
                .build();
    }
}