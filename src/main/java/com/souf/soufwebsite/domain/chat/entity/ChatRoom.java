package com.souf.soufwebsite.domain.chat.entity;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_Id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    public ChatRoom(Member sender, Member receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public boolean hasParticipant(Member member) {
        return Objects.equals(this.getSender().getId(), member.getId()) ||
                Objects.equals(this.getReceiver().getId(), member.getId());
    }


    public Member getOpponent(Member me) {
        if (me.equals(sender)) return receiver;
        if (me.equals(receiver)) return sender;
        throw new IllegalArgumentException("이 채팅방에 속하지 않은 유저입니다.");
    }
}