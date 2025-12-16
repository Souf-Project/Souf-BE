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

    private final MemberRepository memberRepository;
    private final NotificationFacade notificationFacade;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotificationEvent e) {
        Member target = memberRepository.findById(e.targetMemberId()).orElse(null);
        if (target == null) return;

        notificationFacade.notify(
                target,
                e.type(),
                e.title(),
                e.body(),
                e.refType(),
                e.refId()
        );
    }
}