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

    // 학생
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Member student;

    // 동아리
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_id", nullable = false)
    private Member club;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    // 팩토리/검증
    public static MemberClubMapping create(Member student, Member club, String roleInClub) {
        if (student == null || student.getRole() != RoleType.STUDENT)
            throw new IllegalArgumentException("학생(Member, role=STUDENT)만 가입할 수 있습니다.");
        if (club == null || club.getRole() != RoleType.CLUB)
            throw new IllegalArgumentException("동아리(Member, role=CLUB)만 수용할 수 있습니다.");

        MemberClubMapping m = new MemberClubMapping();
        m.student = student;
        m.club = club;
        m.joinedAt = LocalDateTime.now();
        return m;
    }

    public void softDelete() {
        this.isDeleted = true;

        if (student != null) student.getMembershipsAsStudent().remove(this);
        if (club != null) club.getMembershipsAsClub().remove(this);
    }
}