package com.souf.soufwebsite.domain.chat.service;

import com.souf.soufwebsite.domain.chat.dto.MessageType;
import com.souf.soufwebsite.domain.chat.entity.ChatMessage;
import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.chat.exception.NotFoundChatMessageException;
import com.souf.soufwebsite.domain.chat.repository.ChatMessageRepository;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.PostType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final FileService fileService;

    public ChatMessage saveMessage(ChatRoom room, Member sender, String content, MessageType type) {
        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .content(content)
                .type(type)
                .build();

        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessages(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomIdOrderByCreatedTimeAsc(chatRoomId);
    }

    @Transactional
    public void markMessagesAsRead(ChatRoom room, Member reader) {
        chatMessageRepository.markAllAsRead(room, reader);
    }

    public void uploadChatFile(MediaReqDto reqDto) {
        ChatMessage chatMessage = chatMessageRepository.findById(reqDto.postId()).orElseThrow(NotFoundChatMessageException::new);
        fileService.uploadMetadata(reqDto, PostType.CHAT, chatMessage.getId());
    }
}