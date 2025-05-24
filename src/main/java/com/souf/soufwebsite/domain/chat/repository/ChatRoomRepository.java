package com.souf.soufwebsite.domain.chat.repository;

import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySenderAndReceiver(Member sender, Member receiver);
}
