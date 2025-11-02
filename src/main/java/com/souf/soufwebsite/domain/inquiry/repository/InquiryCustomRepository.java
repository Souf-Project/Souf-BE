package com.souf.soufwebsite.domain.inquiry.repository;

import com.souf.soufwebsite.domain.inquiry.dto.InquiryResDto;
import com.souf.soufwebsite.domain.inquiry.entity.InquiryStatus;
import com.souf.soufwebsite.domain.inquiry.entity.InquiryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryCustomRepository {

    Page<InquiryResDto> getInquiryListInAdmin(String search, InquiryType inquiryType, InquiryStatus status, Pageable pageable);
}
