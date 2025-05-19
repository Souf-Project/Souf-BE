package com.souf.soufwebsite.domain.chat.repository;

import com.souf.soufwebsite.domain.chat.entity.ChatMessage;
import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderByCreatedTimeAsc(Long chatRoomId); // 채팅방 ID로 이전의 메시지 조회

    Optional<ChatMessage> findTopByChatRoomOrderByCreatedTimeDesc(ChatRoom chatRoom); // 채팅방 ID로 마지막 메시지 조회

    int countByChatRoomAndSenderNotAndIsReadFalse(ChatRoom chatRoom, Member reader); // 안 읽은 메시지 수 조회

    List<ChatMessage> findByChatRoomAndSenderNotAndIsReadFalse(ChatRoom room, Member me); // 읽지 않은 메시지 조회

}
