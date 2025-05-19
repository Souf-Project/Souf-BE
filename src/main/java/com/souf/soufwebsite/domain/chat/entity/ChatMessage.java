package com.souf.soufwebsite.domain.chat.entity;

import com.souf.soufwebsite.domain.chat.dto.MessageType;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "chatmessage_Id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    private String content;

    @Column(nullable = false)
    private boolean isRead = false;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Builder
    public ChatMessage(ChatRoom chatRoom, Member sender, String content, boolean isRead, MessageType type) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.content = content;
        this.isRead = isRead;
        this.type = type;
    }

    public void markAsRead() {
        this.isRead = true;
    }

}