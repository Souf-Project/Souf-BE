package com.souf.soufwebsite.domain.member.repository;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberClubMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberClubMappingRepository extends JpaRepository<MemberClubMapping, Long> {
    boolean existsByStudentAndClubAndIsDeletedFalse(Member student, Member club);
    Optional<MemberClubMapping> findByStudentAndClubAndIsDeletedFalse(Member student, Member club);
    Page<MemberClubMapping> findAllByStudentIdAndIsDeletedFalse(Long studentId, Pageable pageable);
    Page<MemberClubMapping> findAllByClubIdAndIsDeletedFalse(Long clubId, Pageable pageable);
}