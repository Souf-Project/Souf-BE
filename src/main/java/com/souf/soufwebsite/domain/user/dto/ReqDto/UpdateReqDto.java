package com.souf.soufwebsite.domain.user.dto.ReqDto;

import java.time.LocalDate;

public record UpdateReqDto(
        String username,
        String nickname,
        LocalDate birth,
        String intro
) {
}