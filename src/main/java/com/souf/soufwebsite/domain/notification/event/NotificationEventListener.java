package com.souf.soufwebsite.domain.notification.event;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.notification.service.NotificationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationFacade notificationFacade;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotificationEvent e) {
        notificationFacade.enqueue(
                e.targetMemberId(),
                e.type(),
                e.title(),
                e.body(),
                e.refType(),
                e.refId(),
                e.dedupKey()
        );
    }
}