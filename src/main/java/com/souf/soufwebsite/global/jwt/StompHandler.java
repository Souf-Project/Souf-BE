package com.souf.soufwebsite.global.jwt;

import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.chat.service.ChatMessageService;
import com.souf.soufwebsite.domain.chat.service.ChatRoomService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.reposiotry.MemberRepository;
import com.souf.soufwebsite.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;


@Component
@RequiredArgsConstructor
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
                    String email = jwtService.extractEmail(token).orElseThrow(() ->
                            new IllegalArgumentException("이메일 추출 실패"));

                    // 권한 없이 인증 객체 생성
                    Member member = memberRepository.findByEmail(email)
                            .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 유저를 찾을 수 없음"));

                    UserDetailsImpl userDetails = new UserDetailsImpl(member);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    accessor.setUser(authentication); // 인증된 사용자 등록
                } else {
                    throw new IllegalArgumentException("유효하지 않은 JWT 토큰");
                }
            }
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            Principal principal = accessor.getUser();

            if (destination != null && principal instanceof Authentication auth &&
                    auth.getPrincipal() instanceof UserDetailsImpl userDetails &&
                    destination.startsWith("/topic/chatroom/")) {

                Member member = userDetails.getMember();
                Long roomId = extractRoomId(destination);
                ChatRoom room = chatRoomService.getRoomById(roomId);

                chatMessageService.markMessagesAsRead(room, member); // ✅ 자동 읽음 처리
            }
        }
        return message;
    }

    private Long extractRoomId(String destination) {
        String[] split = destination.split("/");
        return Long.parseLong(split[split.length - 1]);
    }
}
