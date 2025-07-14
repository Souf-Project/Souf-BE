package com.souf.soufwebsite.domain.chat.repository;

import com.souf.soufwebsite.domain.chat.entity.ChatParticipant;
import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    Optional<ChatParticipant> findByChatRoomIdAndMemberId(Long roomId, Long memberId);

    Optional<ChatParticipant> findByChatRoomAndMember(ChatRoom existingRoom, Member sender);
}
