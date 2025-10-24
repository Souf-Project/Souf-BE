package com.souf.soufwebsite.domain.inquiry.service;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.event.MediaCleanupHelper;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.file.service.MediaCleanupPublisher;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryCreateResDto;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryDetailedResDto;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryReqDto;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryResDto;
import com.souf.soufwebsite.domain.inquiry.entity.Inquiry;
import com.souf.soufwebsite.domain.inquiry.exception.NotFoundInquiryException;
import com.souf.soufwebsite.domain.inquiry.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.inquiry.repository.InquiryRepository;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.global.common.PostType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InquiryServiceImpl implements InquiryService {

    private final MemberRepository memberRepository;
    private final InquiryRepository inquiryRepository;

    private final FileService fileService;

    private final MediaCleanupPublisher mediaCleanupPublisher;
    private final MediaCleanupHelper mediaCleanupHelper;

    @Override
    public InquiryCreateResDto createInquiry(String email, InquiryReqDto inquiryReqDto) {
        Member currentMember = findIfMemberExists(email);

        Inquiry inquiry = Inquiry.of(inquiryReqDto, currentMember);
        inquiry = inquiryRepository.save(inquiry);

        List<PresignedUrlResDto> inquiryPresignedUrls =
                fileService.generatePresignedUrl("inquiry", inquiryReqDto.originalFileNames());

        return new InquiryCreateResDto(inquiry.getId(), inquiryPresignedUrls);
    }

    @Override
    public void uploadInquiryMedia(String email, MediaReqDto mediaReqDto) {
        findIfMemberExists(email);

        Inquiry inquiry = findIfInquiryExists(mediaReqDto.postId());
        fileService.uploadMetadata(mediaReqDto, PostType.INQUIRY, inquiry.getId());
    }

    @Override
    public void updateInquiry(String email, Long inquiryId, InquiryReqDto reqDto) {
        Member currentMember = findIfMemberExists(email);
        Inquiry inquiry = findIfInquiryExists(inquiryId);
        verifyIfInquiryIsMine(inquiry, currentMember);

        updateRemainingImages(reqDto, inquiry);
        inquiry.updateInquiry(reqDto);
    }

    @Override
    public void deleteInquiry(String email, Long inquiryId) {
        Member currentMember = findIfMemberExists(email);
        Inquiry inquiry = findIfInquiryExists(inquiryId);
        verifyIfInquiryIsMine(inquiry, currentMember);

        inquiryRepository.delete(inquiry);
        mediaCleanupPublisher.publish(PostType.INQUIRY, inquiryId);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<InquiryResDto> getMyInquiry(String email, Pageable pageable) {
        Member currentMember = findIfMemberExists(email);

        Page<Inquiry> inquiries = inquiryRepository.findByMember(currentMember.getEmail(), pageable);
        List<InquiryResDto> result = inquiries.getContent().stream().map(
                InquiryResDto::of
        ).toList();

        return new PageImpl<>(result, pageable, inquiries.getTotalElements());
    }

    @Override
    public InquiryDetailedResDto getInquiryById(String email, Long inquiryId) {
        Member currentMember = findIfMemberExists(email);
        Inquiry inquiry = findIfInquiryExists(inquiryId);

        // 현재 멤버가 관리자이거나 본인일 경우에만 오픈
        if (currentMember.getRole().equals(RoleType.ADMIN) || verifyIfInquiryIsMine(inquiry, currentMember)) {
            List<Media> mediaList = fileService.getMediaList(PostType.INQUIRY, inquiry.getId());
            log.info("문의글을 상세 조회합니다!");
            return InquiryDetailedResDto.of(inquiry, inquiry.getMember(), mediaList);
        }

        throw new NotValidAuthenticationException();
    }


    private Member findIfMemberExists(String email) {
        return memberRepository.findByEmail(email).orElseThrow(NotFoundMemberException::new);
    }

    private Inquiry findIfInquiryExists(Long inquiryId) {
        return inquiryRepository.findById(inquiryId).orElseThrow(NotFoundInquiryException::new);
    }

    private boolean verifyIfInquiryIsMine(Inquiry inquiry, Member member) {
        log.info("currentMember: {}, inquiry: {}", member, inquiry.getMember());
        if(!inquiry.getMember().getId().equals(member.getId())){
            throw new NotValidAuthenticationException();
        }

        return true;
    }

    private void updateRemainingImages(InquiryReqDto reqDto, Inquiry inquiry) {
        List<String> removed = mediaCleanupHelper.purgeRemovedMedias(
                PostType.INQUIRY,
                inquiry.getId(),
                reqDto.existingImageUrls()
        );

        // 삭제할 URL이 있으면 S3 삭제 이벤트 발행
        if (!removed.isEmpty()) {
            mediaCleanupPublisher.publishUrls(PostType.INQUIRY, inquiry.getId(), removed);
        }
    }
}
