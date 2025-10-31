package com.souf.soufwebsite.domain.member.service.club;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.dto.resDto.ClubSimpleResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberSimpleResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MyClubResDto;
import com.souf.soufwebsite.domain.member.entity.*;
import com.souf.soufwebsite.domain.member.exception.*;
import com.souf.soufwebsite.domain.member.repository.MemberClubMappingRepository;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.global.common.PostType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MemberClubService {
    private final MemberRepository memberRepository;
    private final MemberClubMappingRepository mappingRepository;
    private final FeedRepository feedRepository;

    private final FileService fileService;

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
                student, club, List.of(EnrollmentStatus.PENDING, EnrollmentStatus.APPROVED));
        if (existsActive) throw new AlreadyJoinedClubException();

        MemberClubMapping mapping = MemberClubMapping.create(student, club);

        mappingRepository.save(mapping);
    }

    @Transactional
    public void decideJoin(String clubEmail, Long clubId, Long studentId, JoinDecision decision) {
        Member club = memberRepository.findByEmail(clubEmail)
                .filter(m -> m.getRole() == RoleType.CLUB && m.getId().equals(clubId))
                .orElseThrow(NotValidManageAuthenticationException::new);

        Member student = memberRepository.findById(studentId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(NotFoundMemberException::new);

        MemberClubMapping mapping = mappingRepository
                .findByStudentAndClubAndStatusAndIsDeletedFalse(student, club, EnrollmentStatus.PENDING)
                .orElseThrow(NotFoundPendingApplyException::new);

        switch (decision) {
            case APPROVE -> mapping.approve();
            case REJECT  -> mapping.reject();
            default -> throw new InvalidJoinDecisionException();
        }
    }

    // 탈퇴(승인된 경우에만 의미)
    @Transactional
    public void leaveClub(String email, Long clubId) {
        Member student = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);
        Member club = memberRepository.findById(clubId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(NotFoundClubException::new);

        mappingRepository.findByStudentAndClubAndStatusAndIsDeletedFalse(student, club, EnrollmentStatus.APPROVED)
                .ifPresent(MemberClubMapping::softDelete);
    }

    @Transactional(readOnly = true)
    public Page<MyClubResDto> getMyClubs(String email, Pageable pageable) {
        Member student = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);

        Page<MemberClubMapping> page = mappingRepository
                .findAllByStudentIdAndStatusAndIsDeletedFalse(student.getId(), EnrollmentStatus.APPROVED, pageable);

        return page.map(mapping -> {
            Long memberCount = mappingRepository.countApprovedMembersByClubId(mapping.getClub().getId());
            return MyClubResDto.from(mapping, memberCount);
        });
    }

    @Transactional(readOnly = true)
    public Page<MemberSimpleResDto> getClubMembers(Long clubId, EnrollmentStatus status, Pageable pageable) {
        Page<MemberClubMapping> page = mappingRepository
                .findAllByClubIdAndStatusAndIsDeletedFalse(clubId, status, pageable);

        return page.map(mapping -> {
            Member member = mapping.getStudent();

            String profileImageUrl = fileService.getMediaUrl(PostType.PROFILE, member.getId());

            List<Feed> topFeeds = feedRepository.findTop3ByMemberOrderByViewCountDesc(member);
            List<MemberSimpleResDto.PopularFeedDto> feedDtos = topFeeds.stream()
                    .map(feed -> {
                        List<Media> mediaList = fileService.getMediaList(PostType.FEED, feed.getId());
                        if (mediaList == null || mediaList.isEmpty()) return null;

                        Media first = mediaList.get(0);
                        String url = (first.getThumbnailUrl() != null)
                                ? first.getThumbnailUrl()
                                : first.getOriginalUrl();

                        return new MemberSimpleResDto.PopularFeedDto(url);
                    })
                    .filter(Objects::nonNull)
                    .toList();

            return MemberSimpleResDto.from(member, profileImageUrl, feedDtos, member.getCategories());
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

        mappingRepository.findByStudentAndClubAndStatusAndIsDeletedFalse(student, club, EnrollmentStatus.APPROVED)
                .ifPresent(MemberClubMapping::softDelete);
    }
}