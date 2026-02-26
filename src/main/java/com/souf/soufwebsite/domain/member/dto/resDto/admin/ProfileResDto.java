package com.souf.soufwebsite.domain.member.dto.resDto.admin;

import com.souf.soufwebsite.domain.member.dto.resDto.info.MemberInfoResDto;

public record ProfileResDto(

        MemberInfoResDto resDto,

        String authenticationFileUrl
) {
}
