package com.souf.soufwebsite.domain.chat.repository;

import com.souf.soufwebsite.domain.chat.entity.ChatMessage;
import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderByCreatedTimeAsc(Long chatRoomId); // 채팅방 ID로 이전의 메시지 조회

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE ChatMessage m
        SET m.isRead = true
        WHERE m.chatRoom = :room
          AND m.sender <> :reader
          AND m.isRead = false
    """)
    void markAllAsRead(@Param("room") ChatRoom room, @Param("reader") Member reader);
}
