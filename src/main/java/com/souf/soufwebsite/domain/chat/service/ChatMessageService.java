package com.souf.soufwebsite.domain.chat.service;

import com.souf.soufwebsite.domain.chat.dto.MessageType;
import com.souf.soufwebsite.domain.chat.entity.ChatMessage;
import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.chat.repository.ChatMessageRepository;
import com.souf.soufwebsite.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public void saveMessage(ChatRoom room, Member sender, String content, MessageType type) {
        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .content(content)
                .type(type)
                .build();

        chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessages(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
    }

    @Transactional
    public void markMessagesAsRead(ChatRoom room, Member reader) {
        List<ChatMessage> unreadMessages = chatMessageRepository
                .findByChatRoomAndSenderNotAndIsReadFalse(room, reader);

        unreadMessages.forEach(msg -> msg.markAsRead());
    }
}