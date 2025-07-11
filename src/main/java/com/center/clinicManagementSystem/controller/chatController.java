package com.center.clinicManagementSystem.controller;

import com.center.clinicManagementSystem.dtos.*;
import com.center.clinicManagementSystem.responses.ApiResponse;
import com.center.clinicManagementSystem.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoomDTO>> createRoom(
            @Valid @RequestBody CreateChatRoomRequest request,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            ChatRoomDTO room = chatService.createChatRoom(request, userEmail);

            return ResponseEntity.ok(ApiResponse.<ChatRoomDTO>builder()
                    .success(true)
                    .message("Salon créé avec succès")
                    .data(room)
                    .build());
        } catch (Exception e) {
            log.error("Erreur lors de la création du salon: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<ChatRoomDTO>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomDTO>>> getUserRooms(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            List<ChatRoomDTO> rooms = chatService.getUserRooms(userEmail);

            return ResponseEntity.ok(ApiResponse.<List<ChatRoomDTO>>builder()
                    .success(true)
                    .message("Salons récupérés avec succès")
                    .data(rooms)
                    .build());
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des salons: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<List<ChatRoomDTO>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<ApiResponse<Void>> joinRoom(
            @PathVariable String roomId,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            chatService.joinRoom(roomId, userEmail);

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Vous avez rejoint le salon avec succès")
                    .build());
        } catch (Exception e) {
            log.error("Erreur lors de la participation au salon: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/rooms/{roomId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveRoom(
            @PathVariable String roomId,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            chatService.leaveRoom(roomId, userEmail);

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Vous avez quitté le salon avec succès")
                    .build());
        } catch (Exception e) {
            log.error("Erreur lors de la sortie du salon: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/messages")
    public ResponseEntity<ApiResponse<ChatMessageDTO>> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            ChatMessageDTO message = chatService.sendMessage(request, userEmail);

            return ResponseEntity.ok(ApiResponse.<ChatMessageDTO>builder()
                    .success(true)
                    .message("Message envoyé avec succès")
                    .data(message)
                    .build());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du message: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<ChatMessageDTO>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/rooms/{roomId}/upload")
    public ResponseEntity<ApiResponse<ChatMessageDTO>> uploadFile(
            @PathVariable String roomId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            ChatMessageDTO message = chatService.uploadFile(roomId, file, userEmail);

            return ResponseEntity.ok(ApiResponse.<ChatMessageDTO>builder()
                    .success(true)
                    .message("Fichier envoyé avec succès")
                    .data(message)
                    .build());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du fichier: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<ChatMessageDTO>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<Page<ChatMessageDTO>>> getMessages(
            @PathVariable String roomId,
            Pageable pageable,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Page<ChatMessageDTO> messages = chatService.getMessages(roomId, userEmail, pageable);

            return ResponseEntity.ok(ApiResponse.<Page<ChatMessageDTO>>builder()
                    .success(true)
                    .message("Messages récupérés avec succès")
                    .data(messages)
                    .build());
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des messages: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<Page<ChatMessageDTO>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable String roomId,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            chatService.markAsRead(roomId, userEmail);

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Messages marqués comme lus")
                    .build());
        } catch (Exception e) {
            log.error("Erreur lors du marquage comme lu: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/rooms/{roomId}/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @PathVariable String roomId,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Long count = chatService.getUnreadCount(roomId, userEmail);

            return ResponseEntity.ok(ApiResponse.<Long>builder()
                    .success(true)
                    .message("Nombre de messages non lus récupéré")
                    .data(count)
                    .build());
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du nombre de messages non lus: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<Long>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/rooms/{roomId}/search")
    public ResponseEntity<ApiResponse<List<ChatMessageDTO>>> searchMessages(
            @PathVariable String roomId,
            @RequestParam String query,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            List<ChatMessageDTO> messages = chatService.searchMessages(roomId, query, userEmail);

            return ResponseEntity.ok(ApiResponse.<List<ChatMessageDTO>>builder()
                    .success(true)
                    .message("Recherche effectuée avec succès")
                    .data(messages)
                    .build());
        } catch (Exception e) {
            log.error("Erreur lors de la recherche: {}", e
