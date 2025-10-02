package com.souf.soufwebsite.domain.inquiry.service;

import com.souf.soufwebsite.domain.inquiry.dto.InquiryReqDto;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryService {

    void createInquiry(String email, InquiryReqDto inquiryReqDto);

    void updateInquiry(String email, Long inquiryId, InquiryReqDto reqDto);

    void deleteInquiry(String email, Long inquiryId);

    Page<InquiryResDto> getMyInquiry(String email, Pageable pageable);
}
