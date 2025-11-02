package com.souf.soufwebsite.domain.notification.entity;

import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 수신자 */
    @Column(nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NotificationType type;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 500)
    private String body;

    /** 참조 도메인/PK (선택) */
    @Column(length = 40)
    private String refType;

    private Long refId;

    /** 읽음 여부 */
    @Column(nullable = false)
    private boolean read;

    @Builder
    public Notification(Long memberId, NotificationType type, String title, String body, String refType, Long refId, boolean read
    ) {
        this.memberId = memberId;
        this.type = type;
        this.title = title;
        this.body = body;
        this.refType = refType;
        this.refId = refId;
        this.read = read;
    }

    public static Notification of(
            Long memberId, NotificationType type, String title, String body, String refType, Long refId
    ) {
        return Notification.builder()
                .memberId(memberId)
                .type(type)
                .title(title)
                .body(body)
                .refType(refType)
                .refId(refId)
                .read(false)
                .build();
    }
}