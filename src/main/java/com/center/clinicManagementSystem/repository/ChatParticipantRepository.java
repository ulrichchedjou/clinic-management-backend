package com.center.clinicManagementSystem.repository;

import com.center.clinicManagementSystem.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    
    @Query("SELECT cp FROM ChatParticipant cp " +
           "WHERE cp.chatRoom.roomId = :roomId " +
           "AND cp.isActive = true")
    List<ChatParticipant> findActiveByChatRoomRoomId(@Param("roomId") String roomId);
    
    @Query("SELECT cp FROM ChatParticipant cp " +
           "WHERE cp.chatRoom.roomId = :roomId " +
           "AND cp.user.email = :userEmail " +
           "AND cp.isActive = true")
    Optional<ChatParticipant> findByRoomIdAndUserEmail(@Param("roomId") String roomId, 
                                                      @Param("userEmail") String userEmail);
    
    @Query("SELECT cp FROM ChatParticipant cp " +
           "WHERE cp.user.email = :userEmail " +
           "AND cp.isActive = true")
    List<ChatParticipant> findActiveByUserEmail(@Param("userEmail") String userEmail);
    
    @Query("SELECT COUNT(cp) FROM ChatParticipant cp " +
           "WHERE cp.chatRoom.roomId = :roomId " +
           "AND cp.isActive = true")
    Long countActiveParticipantsByRoomId(@Param("roomId") String roomId);
    
    @Modifying
    @Query("UPDATE ChatParticipant cp " +
           "SET cp.lastReadAt = :readAt " +
           "WHERE cp.chatRoom.roomId = :roomId " +
           "AND cp.user.email = :userEmail")
    void updateLastReadAt(@Param("roomId") String roomId, 
                         @Param("userEmail") String userEmail, 
                         @Param("readAt") LocalDateTime readAt);
    
    @Modifying
    @Query("UPDATE ChatParticipant cp " +
           "SET cp.isActive = false " +
           "WHERE cp.chatRoom.roomId = :roomId " +
           "AND cp.user.email = :userEmail")
    void leaveRoom(@Param("roomId") String roomId, @Param("userEmail") String userEmail);
    
    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);
    
    @Query("SELECT cp.user.email FROM ChatParticipant cp " +
           "WHERE cp.chatRoom.roomId = :roomId " +
           "AND cp.isActive = true " +
           "AND cp.user.email != :excludeEmail")
    List<String> findParticipantEmailsExcluding(@Param("roomId") String roomId, 
                                               @Param("excludeEmail") String excludeEmail);
}
