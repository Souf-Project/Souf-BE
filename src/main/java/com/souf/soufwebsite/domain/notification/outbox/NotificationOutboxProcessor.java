package com.souf.soufwebsite.domain.notification.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.notification.service.NotificationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationOutboxProcessor {

    private final NotificationOutboxRepository outboxRepository;
    private final MemberRepository memberRepository;
    private final NotificationFacade notificationFacade;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void processDue() {
        List<NotificationOutbox> dueList = outboxRepository.findDue(LocalDateTime.now());

        for (NotificationOutbox o : dueList) {
            // 다른 스레드/인스턴스가 잡았을 수 있으니 방어 (락 쓰면 거의 불필요하지만 안전하게)
            if (o.getStatus() != NotificationOutboxStatus.PENDING) continue;

            try {
                o.markProcessing();

                NotificationOutboxPayload payload =
                        objectMapper.readValue(o.getPayloadJson(), NotificationOutboxPayload.class);

                Member target = memberRepository.findById(payload.targetMemberId()).orElse(null);
                if (target == null) {
                    // 대상이 없으면 더 이상 재시도 의미가 없어서 FAILED 처리
                    o.markFailed("target member not found: " + payload.targetMemberId());
                    continue;
                }

                notificationFacade.sendNow(
                        target,
                        payload.type(),
                        payload.title(),
                        payload.body(),
                        payload.refType(),
                        payload.refId()
                );

                o.markSent();
            } catch (Exception ex) {
                log.warn("notification outbox send failed. outboxId={}, retryCount={}, err={}",
                        o.getId(), o.getRetryCount(), ex.toString());
                o.markFailed(ex.getMessage());
            }
        }
    }
}