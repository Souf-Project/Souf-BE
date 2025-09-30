package com.souf.soufwebsite.domain.inquiry.repository;

import com.souf.soufwebsite.domain.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}
