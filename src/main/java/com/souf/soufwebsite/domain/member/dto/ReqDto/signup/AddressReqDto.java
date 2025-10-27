package com.souf.soufwebsite.domain.member.dto.ReqDto.signup;

public record AddressReqDto(
        String zipCode,
        String roadNameAddress,
        String detailedAddress
) {
}
