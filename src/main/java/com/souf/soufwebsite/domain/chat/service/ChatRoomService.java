package com.souf.soufwebsite.domain.chat.service;

import com.souf.soufwebsite.domain.chat.dto.ChatRoomSummaryDto;
import com.souf.soufwebsite.domain.chat.entity.ChatMessage;
import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.chat.repository.ChatMessageRepository;
import com.souf.soufwebsite.domain.chat.repository.ChatRoomRepository;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.reposiotry.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatRoom findOrCreateRoom(Member sender, Member receiver) {
        return chatRoomRepository.findBySenderAndReceiver(sender, receiver)
                .orElseGet(() -> chatRoomRepository.save(new ChatRoom(sender, receiver)));
    }

    public List<ChatRoomSummaryDto> getChatRoomsForUser(Member member) {
        List<ChatRoom> rooms = chatRoomRepository.findAll().stream()
                .filter(room -> room.getSender().equals(member) || room.getReceiver().equals(member))
                .toList();

        return rooms.stream()
                .map(room -> {
                    Member opponent = room.getSender().equals(member) ? room.getReceiver() : room.getSender();

                    // 마지막 메시지 가져오기
                    ChatMessage lastMessage = chatMessageRepository
                            .findTopByChatRoomOrderByCreatedAtDesc(room)
                            .orElse(null);

                    String lastContent = lastMessage != null ? lastMessage.getContent() : "";

                    // 안 읽은 메시지 수
                    int unreadCount = chatMessageRepository
                            .countByChatRoomAndSenderNotAndIsReadFalse(room, member);

                    return new ChatRoomSummaryDto(
                            room.getId(),
                            opponent.getNickname(),
                            lastContent,
                            unreadCount
                    );
                })
                .toList();
    }

    public ChatRoom getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다."));
    }

}
