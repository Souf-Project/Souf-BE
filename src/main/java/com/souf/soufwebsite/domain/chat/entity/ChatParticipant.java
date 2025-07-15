package com.souf.soufwebsite.domain.chat.entity;

import com.souf.soufwebsite.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatParticipant {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private boolean exited = false; // 기본값 false

    public static ChatParticipant of(ChatRoom chatRoom, Member member) {
        ChatParticipant participant = new ChatParticipant();
        participant.chatRoom = chatRoom;
        participant.member = member;
        return participant;
    }

    public void exit() {
        this.exited = true;
    }

    public void rejoin() {
        this.exited = false;
    }
}