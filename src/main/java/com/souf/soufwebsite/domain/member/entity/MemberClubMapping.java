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
    @Column(name = "membership_id")
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
    private MembershipStatus status;            // PENDING/APPROVED/REJECTED

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;          // 신청 시각

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    // 신청 생성: 상태 = PENDING
    public static MemberClubMapping create(Member student, Member club) {
        if (student == null || student.getRole() != RoleType.STUDENT)
            throw new IllegalArgumentException("학생(Member, role=STUDENT)만 가입 신청 가능합니다.");
        if (club == null || club.getRole() != RoleType.CLUB)
            throw new IllegalArgumentException("동아리(Member, role=CLUB)만 대상입니다.");

        MemberClubMapping m = new MemberClubMapping();
        m.student = student;
        m.club = club;
        m.status = MembershipStatus.PENDING;
        m.requestedAt = LocalDateTime.now();
        return m;
    }

    // 승인 처리
    public void approve() {
        this.status = MembershipStatus.APPROVED;
        this.joinedAt = LocalDateTime.now();
    }

    // 거절 처리
    public void reject() {
        this.status = MembershipStatus.REJECTED;
    }

    public void softDelete() {
        this.isDeleted = true;
        if (student != null) student.getMembershipsAsStudent().remove(this);
        if (club != null) club.getMembershipsAsClub().remove(this);
    }
}