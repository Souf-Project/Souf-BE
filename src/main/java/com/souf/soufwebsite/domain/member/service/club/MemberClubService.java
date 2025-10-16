package com.souf.soufwebsite.domain.member.service.club;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberClubMapping;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.repository.MemberClubMappingRepository;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberClubService {

    private final MemberRepository memberRepository;
    private final MemberClubMappingRepository memberClubMappingRepository;

    @Transactional
    public void joinClub(Long studentId, Long clubId, String roleInClub) {
        Member student = memberRepository.findById(studentId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));
        Member club = memberRepository.findById(clubId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("동아리를 찾을 수 없습니다."));

        if (student.getRole() != RoleType.STUDENT) throw new IllegalStateException("학생 계정만 가입할 수 있습니다.");
        if (club.getRole() != RoleType.CLUB) throw new IllegalStateException("동아리 계정만 수용할 수 있습니다.");

        if (memberClubMappingRepository.existsByStudentAndClubAndIsDeletedFalse(student, club))
            return; // 이미 가입됨

        MemberClubMapping m = student.joinClub(club, roleInClub);
        memberClubMappingRepository.save(m);
    }

    @Transactional
    public void leaveClub(Long studentId, Long clubId) {
        Member student = memberRepository.findById(studentId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));
        Member club = memberRepository.findById(clubId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("동아리를 찾을 수 없습니다."));

        memberClubMappingRepository.findByStudentAndClubAndIsDeletedFalse(student, club)
                .ifPresent(MemberClubMapping::softDelete);
    }

    @Transactional(readOnly = true)
    public List<MemberClubMapping> getMyClubs(Long studentId) {
        return memberClubMappingRepository.findAllByStudentIdAndIsDeletedFalse(studentId);
    }

    @Transactional(readOnly = true)
    public List<MemberClubMapping> getClubMembers(Long clubId) {
        return memberClubMappingRepository.findAllByClubIdAndIsDeletedFalse(clubId);
    }
}
