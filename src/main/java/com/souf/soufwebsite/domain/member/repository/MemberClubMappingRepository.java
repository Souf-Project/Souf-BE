package com.souf.soufwebsite.domain.member.repository;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberClubMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberClubMappingRepository extends JpaRepository<MemberClubMapping, Long> {
    boolean existsByStudentAndClubAndIsDeletedFalse(Member student, Member club);
    Optional<MemberClubMapping> findByStudentAndClubAndIsDeletedFalse(Member student, Member club);
    List<MemberClubMapping> findAllByStudentIdAndIsDeletedFalse(Long studentId);
    List<MemberClubMapping> findAllByClubIdAndIsDeletedFalse(Long clubId);
    long countByClubIdAndIsDeletedFalse(Long clubId);
}