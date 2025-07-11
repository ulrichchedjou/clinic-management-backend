package com.center.clinicManagementSystem.services;

import com.clinic.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatService {
    
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    public ChatRoomDTO createChatRoom(CreateChatRoomRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        // Créer la room
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
                if (!email.equals(creatorEmail)) {
                    userRepository.findByEmail(email)
                        .ifPresent(user -> addParticipant(chatRoom, user, ParticipantRole.MEMBER));
                }
            }
        }
        
        return convertToDTO(chatRoom, creatorEmail);
    }
    
    public ChatMessageDTO sendMessage(SendMessageRequest request, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(request.getRoomId())
            .orElseThrow(() -> new ResourceNotFoundException("Room non trouvée"));
        
        // Vérifier que l'utilisateur est participant
        if (!isParticipant(request.getRoomId(), senderEmail)) {
            throw new IllegalArgumentException("Vous n'êtes pas participant de cette room");
        }
        
        // Créer le message
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
        
        // Gérer la réponse à un message
        if (request.getReplyToId() != null) {
            chatMessageRepository.findById(request.getReplyToId())
                .ifPresent(message::setReplyTo);
        }
        
        message = chatMessageRepository.save(message);
        
        // Mettre à jour le timestamp de la room
        chatRoom.setUpdatedAt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
        
        ChatMessageDTO messageDTO = convertToDTO(message);
        
        // Envoyer le message via WebSocket
        broadcastMessage(request.getRoomId(), messageDTO);
        
        // Envoyer notifications aux participants
        sendNotificationToParticipants(request.getRoomId(), messageDTO, senderEmail);
        
        return messageDTO;
    }
    
    public List<ChatRoomDTO> getUserRooms(String userEmail) {
        List<ChatRoom> rooms = chatRoomRepository.findActiveRoomsByUserEmail(userEmail);
        return rooms.stream()
            .map(room -> convertToDTO(room, userEmail))
            .collect(Collectors.toList());
    }
    
    public Page<ChatMessageDTO> getRoomMessages(String roomId, String userEmail, Pageable pageable) {
        if (!isParticipant(roomId, userEmail)) {
            throw new IllegalArgumentException("Vous n'êtes pas participant de cette room");
        }
        
        Page<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);
        return messages.map(this::convertToDTO);
    }
    
    public void markAsRead(String roomId, String userEmail) {
        chatParticipantRepository.updateLastReadAt(roomId, userEmail, LocalDateTime.now());
        
        // Notifier le changement de statut
        ChatNotification notification = ChatNotification.builder()
            .type("MESSAGE_READ")
            .roomId(roomId)
            .senderEmail(userEmail)
            .timestamp(LocalDateTime.now())
            .build();
        
        messagingTemplate.convertAndSend("/topic/room/" + roomId, notification);
    }
    
    public void joinRoom(String roomId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room non trouvée"));
        
        if (!isParticipant(roomId, userEmail)) {
            addParticipant(chatRoom, user, ParticipantRole.MEMBER);
        }
        
        // Notifier les autres participants
        ChatNotification notification = ChatNotification.builder()
            .type("USER_JOINED")
            .roomId(roomId)
            .senderName(user.getFullName())
            .senderEmail(userEmail)
            .timestamp(LocalDateTime.now())
            .build();
        
        messagingTemplate.convertAndSend("/topic/room/" + roomId, notification);
    }
    
    public void leaveRoom(String roomId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        chatParticipantRepository.leaveRoom(roomId, userEmail);
        
        // Notifier les autres participants
        ChatNotification notification = ChatNotification.builder()
            .type("USER_LEFT")
            .roomId(roomId)
            .senderName(user.getFullName())
            .senderEmail(userEmail)
            .timestamp(LocalDateTime.now())
            .build();
        
        messagingTemplate.convertAndSend("/topic/room/" + roomId, notification);
    }
    
    public void sendTypingNotification(String roomId, String userEmail, boolean isTyping) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        ChatNotification notification = ChatNotification.builder()
            .type(isTyping ? "TYPING_START" : "TYPING_STOP")
            .roomId(roomId)
            
