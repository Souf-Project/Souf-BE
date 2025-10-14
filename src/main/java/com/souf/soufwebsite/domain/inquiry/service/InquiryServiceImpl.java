package com.souf.soufwebsite.domain.inquiry.service;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryCreateResDto;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryReqDto;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryResDto;
import com.souf.soufwebsite.domain.inquiry.entity.Inquiry;
import com.souf.soufwebsite.domain.inquiry.exception.NotFoundInquiryException;
import com.souf.soufwebsite.domain.inquiry.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.inquiry.repository.InquiryRepository;
import com.souf.soufwebsite.domain.member.entity.Member;
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

        inquiry.updateInquiry(reqDto);
    }

    @Override
    public void deleteInquiry(String email, Long inquiryId) {
        Member currentMember = findIfMemberExists(email);
        Inquiry inquiry = findIfInquiryExists(inquiryId);
        verifyIfInquiryIsMine(inquiry, currentMember);

        inquiryRepository.delete(inquiry);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<InquiryResDto> getMyInquiry(String email, Pageable pageable) {
        Member currentMember = findIfMemberExists(email);

        Page<Inquiry> inquiries = inquiryRepository.findByMember(currentMember.getEmail(), pageable);
        List<InquiryResDto> result = inquiries.getContent().stream().map(
                i -> new InquiryResDto(i.getId(), i.getTitle(), i.getContent())
        ).toList();

        return new PageImpl<>(result, pageable, inquiries.getTotalElements());
    }


    private Member findIfMemberExists(String email) {
        return memberRepository.findByEmail(email).orElseThrow(NotFoundMemberException::new);
    }

    private Inquiry findIfInquiryExists(Long inquiryId) {
        return inquiryRepository.findById(inquiryId).orElseThrow(NotFoundInquiryException::new);
    }

    private void verifyIfInquiryIsMine(Inquiry inquiry, Member member) {
        log.info("currentMember: {}, inquiry: {}", member, inquiry.getMember());
        if(!inquiry.getMember().getId().equals(member.getId())){
            throw new NotValidAuthenticationException();
        }
    }
}
