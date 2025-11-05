package com.souf.soufwebsite.domain.member.dto.reqDto.addInfo;

import com.souf.soufwebsite.domain.member.dto.reqDto.signup.AddressReqDto;

public record AddCompanyInfoReqDto(
        String phoneNumber,
        Boolean isCompany,
        String companyName,
        String businessRegistrationNumber,
        AddressReqDto addressReqDto,
        String businessStatus,
        String businessClassification,
        String businessRegistrationFile
) {
}
