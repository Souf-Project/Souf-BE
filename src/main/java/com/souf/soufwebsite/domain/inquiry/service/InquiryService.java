package com.souf.soufwebsite.domain.inquiry.service;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryCreateResDto;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryDetailedResDto;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryReqDto;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryService {

    InquiryCreateResDto createInquiry(String email, InquiryReqDto inquiryReqDto);

    void uploadInquiryMedia(String email, MediaReqDto mediaReqDto);

    void updateInquiry(String email, Long inquiryId, InquiryReqDto reqDto);

    void deleteInquiry(String email, Long inquiryId);

    Page<InquiryResDto> getMyInquiry(String email, Pageable pageable);

    InquiryDetailedResDto getInquiryById(String email, Long inquiryId);
}
