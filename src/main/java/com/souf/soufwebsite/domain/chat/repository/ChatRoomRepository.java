package com.souf.soufwebsite.domain.chat.repository;

import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByIdAndReceiver(Long id, Member receiver);

    Optional<ChatRoom> findByIdAndSender(Long id, Member sender);

    Optional<ChatRoom> findBySenderAndReceiver(Member sender, Member receiver);

    @Query("select ch from ChatRoom ch where ch.id = :charRoomId and (ch.sender = :member or ch.receiver = :member)")
    Optional<ChatRoom> findByMember(@Param(value = "charRoomId") Long chatRoomId, @Param(value = "member") Member member);
}
