package com.souf.soufwebsite.domain.chat.service;

import com.souf.soufwebsite.domain.chat.dto.ChatRoomSummaryDto;
import com.souf.soufwebsite.domain.chat.entity.ChatMessage;
import com.souf.soufwebsite.domain.chat.entity.ChatParticipant;
import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.chat.repository.ChatMessageRepository;
import com.souf.soufwebsite.domain.chat.repository.ChatParticipantRepository;
import com.souf.soufwebsite.domain.chat.repository.ChatRoomNativeRepository;
import com.souf.soufwebsite.domain.chat.repository.ChatRoomRepository;
import com.souf.soufwebsite.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomNativeRepository chatRoomNativeRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    @Transactional
    public ChatRoom findOrCreateRoom(Member sender, Member receiver) {
        if (sender.equals(receiver)) {
            throw new IllegalArgumentException("자기 자신과는 채팅할 수 없습니다.");
        }

        Optional<ChatRoom> existingRoomOpt = chatRoomRepository.findBySenderAndReceiver(sender, receiver)
                .or(() -> chatRoomRepository.findBySenderAndReceiver(receiver, sender));

        if (existingRoomOpt.isPresent()) {
            ChatRoom existingRoom = existingRoomOpt.get();

            // ✅ participant 상태 복구
            restoreParticipantIfNeeded(existingRoom, sender);
            restoreParticipantIfNeeded(existingRoom, receiver);

            return existingRoom;
        }

        ChatRoom newRoom = chatRoomRepository.save(new ChatRoom(sender, receiver));
        chatParticipantRepository.save(ChatParticipant.of(newRoom, sender));
        chatParticipantRepository.save(ChatParticipant.of(newRoom, receiver));

        return newRoom;
    }

    public List<ChatRoomSummaryDto> getChatRoomsForUser(Member member) {
        return chatRoomNativeRepository.getChatRoomSummaries(member.getId());
    }

    public ChatRoom getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다."));
    }

    @Transactional
    public void exitChatRoom(Member member, Long roomId) {
        chatParticipantRepository.findByChatRoomIdAndMemberId(roomId, member.getId())
                .orElseThrow(() -> new IllegalArgumentException("참여자 없음"))
                .exit(); // exited = true로 변경
    }

    public void restoreParticipantIfNeeded(ChatRoom room, Member member) {
        chatParticipantRepository.findByChatRoomAndMember(room, member)
                .filter(ChatParticipant::isExited)
                .ifPresent(ChatParticipant::rejoin);
    }
}
