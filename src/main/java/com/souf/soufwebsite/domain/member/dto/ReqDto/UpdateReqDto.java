package com.souf.soufwebsite.domain.member.dto.ReqDto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateReqDto(
        @Size(min = 2, max = 20) String username,
        @Size(min = 2, max = 20) String nickname,
        LocalDate birth,
        String intro
) {
}