package com.souf.soufwebsite.domain.inquiry.service;

import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryReqDto;
import com.souf.soufwebsite.domain.inquiry.entity.Inquiry;
import com.souf.soufwebsite.domain.inquiry.exception.NotFoundInquiryException;
import com.souf.soufwebsite.domain.inquiry.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.inquiry.repository.InquiryRepository;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InquiryServiceImpl implements InquiryService {

    private final MemberRepository memberRepository;
    private final InquiryRepository inquiryRepository;

    private final FileService fileService;

    @Override
    public void createInquiry(String email, InquiryReqDto inquiryReqDto) {
        Member currentMember = findIfMemberExists(email);

        Inquiry inquiry = Inquiry.of(inquiryReqDto, currentMember);
        inquiryRepository.save(inquiry);

        fileService.generatePresignedUrl("inquiry", inquiryReqDto.originalFileNames());
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
