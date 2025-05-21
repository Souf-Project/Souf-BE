package com.souf.soufwebsite.global.jwt;

import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.chat.service.ChatMessageService;
import com.souf.soufwebsite.domain.chat.service.ChatRoomService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.reposiotry.MemberRepository;
import com.souf.soufwebsite.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
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
        log.info("[STOMP] ▶ preSend 호출 - command: {}", accessor.getCommand());

        // 1. CONNECT: 토큰 인증 처리
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("[DEBUG] sessionId = {}", accessor.getSessionId());

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            log.info("[STOMP][CONNECT] Authorization 헤더: {}", authHeader);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                log.info("[STOMP][CONNECT] 토큰 추출 완료");

                if (jwtService.isTokenValid(token)) {
                    String email = jwtService.extractEmail(token)
                            .orElseThrow(() -> new IllegalArgumentException("[STOMP][CONNECT] 이메일 추출 실패"));
                    log.info("[STOMP][CONNECT] 이메일 추출 성공: {}", email);

                    Member member = memberRepository.findByEmail(email)
                            .orElseThrow(() -> new IllegalArgumentException("[STOMP][CONNECT] 이메일에 해당하는 멤버 없음"));

                    UserDetailsImpl userDetails = new UserDetailsImpl(member);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    accessor.setLeaveMutable(true);
                    accessor.setUser(authentication);
                    accessor.getSessionAttributes().put("user", authentication);
                    log.info("[STOMP][CONNECT] 인증 객체 설정 완료 - memberId: {}", member.getId());
                } else {
                    throw new IllegalArgumentException("유효하지 않은 JWT 토큰");
                }
            } else {
                log.warn("[STOMP][CONNECT] Authorization 헤더가 없거나 잘못된 형식");
            }
        }

        // 2. SUBSCRIBE: 자동 읽음 처리
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            log.info("[DEBUG] sessionId = {}", accessor.getSessionId());

            String destination = accessor.getDestination();
            Principal principal = accessor.getUser();
            if (principal == null) {
                Object sessionUser = accessor.getSessionAttributes().get("user");
                if (sessionUser instanceof Authentication) {
                    accessor.setUser((Authentication) sessionUser); // 다시 설정
                    principal = (Authentication) sessionUser;
                }
            }


            log.info("[DEBUG] principal = {}", principal);
            log.info("[DEBUG] principal instanceof Authentication = {}", principal instanceof Authentication);

            if (destination != null && principal instanceof Authentication auth &&
                    auth.getPrincipal() instanceof UserDetailsImpl userDetails &&
                    destination.startsWith("/topic/chatroom/")) {

                Member member = userDetails.getMember();
                Long roomId = extractRoomId(destination);
                log.info("[STOMP][SUBSCRIBE] 채팅방 ID: {}, 구독자 memberId: {}", roomId, member.getId());

                ChatRoom room = chatRoomService.getRoomById(roomId);
                chatMessageService.markMessagesAsRead(room, member);

                log.info("[STOMP][SUBSCRIBE] 읽음 처리 완료");
            } else {
                log.warn("[STOMP][SUBSCRIBE] destination 또는 인증 정보 이상");
            }
        }

        log.info("[STOMP] ◀ preSend 종료");
        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }


    private Long extractRoomId(String destination) {
        String[] split = destination.split("/");
        return Long.parseLong(split[split.length - 1]);
    }
}
