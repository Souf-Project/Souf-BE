package com.souf.soufwebsite.domain.inquiry.service;

import com.souf.soufwebsite.domain.inquiry.dto.InquiryReqDto;
public interface InquiryService {

    void createInquiry(String email, InquiryReqDto inquiryReqDto);

    void updateInquiry(String email, Long inquiryId, InquiryReqDto reqDto);

    void deleteInquiry(String email, Long inquiryId);
}
