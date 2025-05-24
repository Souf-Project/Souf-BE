package com.souf.soufwebsite.domain.chat.service;

import com.souf.soufwebsite.domain.chat.dto.ChatRoomSummaryDto;
import com.souf.soufwebsite.domain.chat.entity.ChatMessage;
import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.chat.repository.ChatMessageRepository;
import com.souf.soufwebsite.domain.chat.repository.ChatRoomNativeRepository;
import com.souf.soufwebsite.domain.chat.repository.ChatRoomRepository;
import com.souf.soufwebsite.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomNativeRepository chatRoomNativeRepository;

    public ChatRoom findOrCreateRoom(Member sender, Member receiver) {
        if (sender.equals(receiver)) {
            throw new IllegalArgumentException("자기 자신과는 채팅할 수 없습니다.");
        }

        return chatRoomRepository.findBySenderAndReceiver(sender, receiver)
                .or(() -> chatRoomRepository.findBySenderAndReceiver(receiver, sender))
                .orElseGet(() -> chatRoomRepository.save(new ChatRoom(sender, receiver)));
    }

    public List<ChatRoomSummaryDto> getChatRoomsForUser(Member member) {
        return chatRoomNativeRepository.getChatRoomSummaries(member.getId());
    }

    public ChatRoom getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다."));
    }

}
