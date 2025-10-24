package com.souf.soufwebsite.domain.member.repository;

import com.souf.soufwebsite.domain.member.entity.EnrollmentStatus;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberClubMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface MemberClubMappingRepository extends JpaRepository<MemberClubMapping, Long> {
    // 이미 활성 신청/가입이 있는지(대기 또는 승인) 확인
    boolean existsByStudentAndClubAndStatusInAndIsDeletedFalse(Member student, Member club,
                                                               Collection<EnrollmentStatus> statuses);

    Optional<MemberClubMapping> findByStudentAndClubAndStatusAndIsDeletedFalse(
            Member student, Member club, EnrollmentStatus status);

    // 내 동아리(승인된 것만)
    Page<MemberClubMapping> findAllByStudentIdAndStatusAndIsDeletedFalse(
            Long studentId, EnrollmentStatus status, Pageable pageable);

    // 동아리 회원(승인된 것만)
    Page<MemberClubMapping> findAllByClubIdAndStatusAndIsDeletedFalse(
            Long clubId, EnrollmentStatus status, Pageable pageable);

    @Query("SELECT COUNT(m) FROM MemberClubMapping m WHERE m.club.id = :clubId AND m.status = 'APPROVED' AND m.isDeleted = false")
    Long countApprovedMembersByClubId(@Param("clubId") Long clubId);
}