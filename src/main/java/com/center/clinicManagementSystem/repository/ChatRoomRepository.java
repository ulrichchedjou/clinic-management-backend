package com.center.clinicManagementSystem.repository;
import com.center.clinicManagementSystem.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomId(String roomId);

    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN cr.participants p " +
            "WHERE p.user.email = :userEmail AND p.isActive = true " +
            "ORDER BY cr.updatedAt DESC")
    List<ChatRoom> findRoomsByUserEmail(@Param("userEmail") String userEmail);

    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN cr.participants p " +
            "WHERE p.user.email = :userEmail AND cr.isActive = true " +
            "ORDER BY cr.updatedAt DESC")
    List<ChatRoom> findActiveRoomsByUserEmail(@Param("userEmail") String userEmail);

    @Query("SELECT COUNT(cr) FROM ChatRoom cr " +
            "JOIN cr.participants p " +
            "WHERE p.user.email = :userEmail AND cr.isActive = true")
    Long countActiveRoomsByUserEmail(@Param("userEmail") String userEmail);

    boolean existsByRoomId(String roomId);
}
