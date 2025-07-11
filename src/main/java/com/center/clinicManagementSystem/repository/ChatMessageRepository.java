package com.center.clinicManagementSystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    @Query("SELECT cm FROM ChatMessage cm " +
           "WHERE cm.chatRoom.roomId = :roomId " +
           "ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findByRoomIdOrderByCreatedAtDesc(@Param("roomId") String roomId, Pageable pageable);
    
    @Query("SELECT cm FROM ChatMessage cm " +
           "WHERE cm.chatRoom.roomId = :roomId " +
           "AND cm.createdAt > :since " +
           "ORDER BY cm.createdAt ASC")
    List<ChatMessage> findByRoomIdAndCreatedAtAfter(@Param("roomId") String roomId, 
                                                    @Param("since") LocalDateTime since);
    
    @Query("SELECT cm FROM ChatMessage cm " +
           "WHERE cm.chatRoom.roomId = :roomId " +
           "ORDER BY cm.createdAt DESC " +
           "LIMIT 1")
    Optional<ChatMessage> findLastMessageByRoomId(@Param("roomId") String roomId);
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
           "JOIN cm.chatRoom cr " +
           "JOIN cr.participants p " +
           "WHERE cr.roomId = :roomId " +
           "AND p.user.email = :userEmail " +
           "AND cm.createdAt > COALESCE(p.lastReadAt, p.joinedAt) " +
           "AND cm.sender.email != :userEmail")
    Long countUnreadMessages(@Param("roomId") String roomId, @Param("userEmail") String userEmail);
    
    @Query("SELECT cm FROM ChatMessage cm " +
           "WHERE cm.chatRoom = :chatRoom " +
           "AND cm.content LIKE %:searchTerm% " +
           "ORDER BY cm.createdAt DESC")
    List<ChatMessage> searchMessagesInRoom(@Param("chatRoom") ChatRoom chatRoom, 
                                          @Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.roomId = :roomId")
    Long countMessagesByRoomId(@Param("roomId") String roomId);
    
    void deleteByCreatedAtBefore(LocalDateTime date);
}
