package com.souf.soufwebsite.domain.member.service.club;

import com.souf.soufwebsite.domain.member.dto.ResDto.ClubMemberResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MyClubResDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberClubMapping;
import com.souf.soufwebsite.domain.member.entity.RoleType;
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

    @Transactional
    public void joinClub(String email, Long clubId, String roleInClub) {
        Member student = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));
        Member club = memberRepository.findById(clubId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("동아리를 찾을 수 없습니다."));

        if (student.getRole() != RoleType.STUDENT) throw new IllegalStateException("학생 계정만 가입할 수 있습니다.");
        if (club.getRole() != RoleType.CLUB) throw new IllegalStateException("동아리 계정만 수용할 수 있습니다.");
        if (mappingRepository.existsByStudentAndClubAndIsDeletedFalse(student, club)) return;

        var mapping = student.joinClub(club, roleInClub);
        mappingRepository.save(mapping);
    }

    @Transactional
    public void leaveClub(String email, Long clubId) {
        Member student = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));
        Member club = memberRepository.findById(clubId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("동아리를 찾을 수 없습니다."));
        mappingRepository.findByStudentAndClubAndIsDeletedFalse(student, club)
                .ifPresent(MemberClubMapping::softDelete);
    }

    @Transactional(readOnly = true)
    public Page<MyClubResDto> getMyClubs(String email, Pageable pageable) {
        Member student = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));
        Page<MemberClubMapping> page = mappingRepository.findAllByStudentIdAndIsDeletedFalse(student.getId(), pageable);

        // ↓ 필요 시 배치 조회로 profile/카테고리/썸네일 등을 주입
        return page.map(mapping -> MyClubResDto.from(mapping));
    }

    @Transactional(readOnly = true)
    public Page<ClubMemberResDto> getClubMembers(Long clubId, Pageable pageable) {
        Page<MemberClubMapping> page = mappingRepository.findAllByClubIdAndIsDeletedFalse(clubId, pageable);

        // ↓ 필요 시 배치 조회로 profile/카테고리/인기피드 주입
        return page.map(mapping -> {
            Member student = mapping.getStudent();
            return ClubMemberResDto.from(
                    mapping,
                    student.getPersonalUrl(),
                    List.of(),
                    student.getCategories()
            );
        });
    }
}