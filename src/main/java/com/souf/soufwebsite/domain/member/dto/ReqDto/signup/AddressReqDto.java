package com.souf.soufwebsite.domain.member.dto.reqDto.signup;

public record AddressReqDto(
        String zipCode,
        String roadNameAddress,
        String detailedAddress
) {
}
