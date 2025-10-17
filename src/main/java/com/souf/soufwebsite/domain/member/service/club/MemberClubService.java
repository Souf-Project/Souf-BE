package com.souf.soufwebsite.domain.member.service.club;

import com.souf.soufwebsite.domain.member.dto.ResDto.ClubMemberResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.ClubSimpleResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MyClubResDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberClubMapping;
import com.souf.soufwebsite.domain.member.entity.MembershipStatus;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.exception.*;
import com.souf.soufwebsite.domain.member.repository.MemberClubMappingRepository;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberClubService {
    private final MemberRepository memberRepository;
    private final MemberClubMappingRepository mappingRepository;

    @Transactional(readOnly = true)
    public Page<ClubSimpleResDto> getAllClubs(Pageable pageable) {
        Page<Member> clubs = memberRepository.findAllByRoleAndIsDeletedFalse(RoleType.CLUB, pageable);

        return clubs.map(club -> {
            Long memberCount = mappingRepository.countApprovedMembersByClubId(club.getId());
            return ClubSimpleResDto.from(club, memberCount);
        });
    }

    // 신청: PENDING 생성
    @Transactional
    public void joinClub(String studentEmail, Long clubId) {
        Member student = memberRepository.findByEmail(studentEmail)
                .orElseThrow(NotFoundMemberException::new);
        Member club = memberRepository.findById(clubId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(NotFoundClubException::new);

        if (student.getRole() != RoleType.STUDENT) throw new NotValidAuthenticationException();
        if (club.getRole() != RoleType.CLUB) throw new NotValidAuthenticationException();

        // PENDING/APPROVED가 이미 있으면 신규 신청 막기
        boolean existsActive = mappingRepository.existsByStudentAndClubAndStatusInAndIsDeletedFalse(
                student, club, List.of(MembershipStatus.PENDING, MembershipStatus.APPROVED));
        if (existsActive) return;

        var mapping = MemberClubMapping.create(student, club);
        mappingRepository.save(mapping);
    }

    // 동아리 계정이 승인
    @Transactional
    public void approveJoin(String clubEmail, Long clubId, Long studentId) {
        Member club = memberRepository.findByEmail(clubEmail)
                .filter(m -> m.getRole() == RoleType.CLUB && m.getId().equals(clubId))
                .orElseThrow(NotValidManageAuthenticationException::new);

        Member student = memberRepository.findById(studentId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(NotFoundMemberException::new);

        MemberClubMapping mapping = mappingRepository
                .findByStudentAndClubAndStatusAndIsDeletedFalse(student, club, MembershipStatus.PENDING)
                .orElseThrow(NotFoundPendingApplyException::new);

        mapping.approve(club);
    }

    // 동아리 계정이 거절
    @Transactional
    public void rejectJoin(String clubEmail, Long clubId, Long studentId) {
        Member club = memberRepository.findByEmail(clubEmail)
                .filter(m -> m.getRole() == RoleType.CLUB && m.getId().equals(clubId))
                .orElseThrow(NotValidManageAuthenticationException::new);

        Member student = memberRepository.findById(studentId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(NotFoundMemberException::new);

        MemberClubMapping mapping = mappingRepository
                .findByStudentAndClubAndStatusAndIsDeletedFalse(student, club, MembershipStatus.PENDING)
                .orElseThrow(NotFoundPendingApplyException::new);

        mapping.reject(club);
    }

    // 탈퇴(승인된 경우에만 의미)
    @Transactional
    public void leaveClub(String email, Long clubId) {
        Member student = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);
        Member club = memberRepository.findById(clubId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(NotFoundClubException::new);

        mappingRepository.findByStudentAndClubAndStatusAndIsDeletedFalse(student, club, MembershipStatus.APPROVED)
                .ifPresent(MemberClubMapping::softDelete);
    }

    @Transactional(readOnly = true)
    public Page<MyClubResDto> getMyClubs(String email, Pageable pageable) {
        Member student = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);

        Page<MemberClubMapping> page = mappingRepository
                .findAllByStudentIdAndStatusAndIsDeletedFalse(student.getId(), MembershipStatus.APPROVED, pageable);

        return page.map(mapping -> {
            Long memberCount = mappingRepository.countApprovedMembersByClubId(mapping.getClub().getId());
            return MyClubResDto.from(mapping, memberCount);
        });
    }

    @Transactional(readOnly = true)
    public Page<ClubMemberResDto> getClubMembers(Long clubId, Pageable pageable) {
        Page<MemberClubMapping> page = mappingRepository
                .findAllByClubIdAndStatusAndIsDeletedFalse(clubId, MembershipStatus.APPROVED, pageable);

        return page.map(mapping -> {
            Member s = mapping.getStudent();
            return ClubMemberResDto.from(mapping, s.getPersonalUrl(), List.of(), s.getCategories());
        });
    }

    @Transactional(readOnly = true)
    public Page<ClubMemberResDto> getPendingMembers(Long clubId, Pageable pageable) {
        Page<MemberClubMapping> page = mappingRepository
                .findAllByClubIdAndStatusAndIsDeletedFalse(clubId, MembershipStatus.PENDING, pageable);

        return page.map(mapping -> {
            Member s = mapping.getStudent();
            return ClubMemberResDto.from(mapping, s.getPersonalUrl(), List.of(), s.getCategories());
        });
    }

    @Transactional
    public void expelMember(String clubEmail, Long clubId, Long studentId) {
        Member club = memberRepository.findByEmail(clubEmail)
                .filter(m -> m.getRole() == RoleType.CLUB && m.getId().equals(clubId))
                .orElseThrow(NotValidManageAuthenticationException::new);

        Member student = memberRepository.findById(studentId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(NotFoundMemberException::new);

        mappingRepository.findByStudentAndClubAndStatusAndIsDeletedFalse(student, club, MembershipStatus.APPROVED)
                .ifPresent(MemberClubMapping::softDelete);
    }
}