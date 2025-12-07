package com.souf.soufwebsite.domain.notification.service;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.notification.dto.NotificationItemDto;
import com.souf.soufwebsite.domain.notification.entity.Notification;
import com.souf.soufwebsite.domain.notification.exception.NotFoundNotificationException;
import com.souf.soufwebsite.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationReadService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    private Long memberIdByEmail(String email) {
        Member m = memberRepository.findByEmail(email).orElseThrow(NotFoundMemberException::new);
        return m.getId();
    }

    @Transactional
    public Page<NotificationItemDto> getMyNotifications(String email, Pageable pageable) {
        Long memberId = memberIdByEmail(email);

        Page<Notification> page = notificationRepository.findByMemberIdOrderByCreatedTimeDesc(memberId, pageable);
        return page.map(NotificationItemDto::from);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {
        Long memberId = memberIdByEmail(email);
        return notificationRepository.countByMemberIdAndReadFalse(memberId);
    }

    @Transactional
    public void deleteOne(String email, Long notificationId) {
        Long memberId = memberIdByEmail(email);
        int deleted = notificationRepository.deleteOne(notificationId, memberId);

        if (deleted == 0) {
            throw new NotFoundNotificationException();
        }
    }

    @Transactional
    public void deleteAll(String email) {
        Long memberId = memberIdByEmail(email);
        notificationRepository.deleteAllByMemberId(memberId);
    }

    @Transactional
    public void markOneRead(String email, Long notificationId) {
        Long memberId = memberIdByEmail(email);
        int updated = notificationRepository.markOneRead(notificationId, memberId);

        if (updated == 0) {
            // 남의 알림이거나, 이미 읽었거나, 존재하지 않는 경우
            throw new NotFoundNotificationException();
        }
    }

    @Transactional
    public int markAllRead(String email) {
        Long memberId = memberIdByEmail(email);
        return notificationRepository.markAllRead(memberId);
    }
}
