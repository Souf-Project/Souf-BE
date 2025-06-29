package com.souf.soufwebsite.global.jwt;

import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.chat.service.ChatMessageService;
import com.souf.soufwebsite.domain.chat.service.ChatRoomService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtService.isTokenValid(token)) {
                    String email = jwtService.extractEmail(token)
                            .orElseThrow(() -> new IllegalArgumentException("[STOMP][CONNECT] 이메일 추출 실패"));
                    Member member = memberRepository.findByEmail(email)
                            .orElseThrow(() -> new IllegalArgumentException("[STOMP][CONNECT] 이메일에 해당하는 멤버 없음"));

                    UserDetailsImpl userDetails = new UserDetailsImpl(member);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    accessor.setLeaveMutable(true);
                    accessor.setUser(authentication);
                    accessor.getSessionAttributes().put("user", authentication);
                } else {
                    throw new IllegalArgumentException("유효하지 않은 JWT 토큰");
                }
            }
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            Principal principal = accessor.getUser();
            if (principal == null) {
                Object sessionUser = accessor.getSessionAttributes().get("user");
                if (sessionUser instanceof Authentication) {
                    accessor.setUser((Authentication) sessionUser);
                    principal = (Authentication) sessionUser;
                }
            }
            if (principal instanceof Authentication auth &&
                    auth.getPrincipal() instanceof UserDetailsImpl userDetails) {

                String destination = accessor.getDestination();
                if (destination != null && destination.startsWith("/topic/chatroom/")) {
                    Member member = userDetails.getMember();
                    Long roomId = extractRoomId(destination);
                    ChatRoom room = chatRoomService.getRoomById(roomId);

                    if (!room.hasParticipant(member)) {
                        throw new AccessDeniedException("채팅방에 참여하지 않은 유저입니다.");
                    }
                    chatMessageService.markMessagesAsRead(room, member);
                }
            }
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }

    private Long extractRoomId(String destination) {
        String[] split = destination.split("/");
        return Long.parseLong(split[split.length - 1]);
    }
}
