package com.souf.soufwebsite.domain.member.dto.resDto.info;


public record CompanyInfo(
        String companyName,
        String businessRegistrationNumber,
        String zipCode,
        String roadNameAddress,
        String detailedAddress,
        String businessStatus,
        String businessClassification
) implements MemberInfo {}