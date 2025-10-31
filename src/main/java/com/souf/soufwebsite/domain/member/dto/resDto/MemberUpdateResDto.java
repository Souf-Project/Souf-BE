package com.souf.soufwebsite.domain.member.dto.resDto;

import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;

public record MemberUpdateResDto(
        Long memberId,
        PresignedUrlResDto dtoList
) {
    public static MemberUpdateResDto of(Long memberId, PresignedUrlResDto dtoList) {
        return new MemberUpdateResDto(memberId, dtoList);
    }
}
