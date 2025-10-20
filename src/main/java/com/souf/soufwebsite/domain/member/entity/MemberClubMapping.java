package com.souf.soufwebsite.domain.member.entity;

import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "is_deleted = false")
public class MemberClubMapping extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Member student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_id", nullable = false)
    private Member club;

    @Column(name = "joined_at")                 // 승인 시점
    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private EnrollmentStatus status;            // PENDING/APPROVED/REJECTED

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;          // 신청 시각

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public static MemberClubMapping create(Member student, Member club) {
        MemberClubMapping m = new MemberClubMapping();
        m.student = student;
        m.club = club;
        m.status = EnrollmentStatus.PENDING;
        m.requestedAt = LocalDateTime.now();
        return m;
    }

    // 승인 처리
    public void approve() {
        this.status = EnrollmentStatus.APPROVED;
        this.joinedAt = LocalDateTime.now();
    }

    // 거절 처리
    public void reject() {
        this.status = EnrollmentStatus.REJECTED;
    }

    public void softDelete() {
        this.isDeleted = true;
        if (student != null) student.getEnrollmentAsStudent().remove(this);
        if (club != null) club.getEnrollmentAsClub().remove(this);
    }
}