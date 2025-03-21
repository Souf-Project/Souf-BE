package com.souf.soufwebsite.domain.user.dto.ReqDto;

import java.time.LocalDate;

public record UserReqDto(
        String email,
        String password,
        String username,
        String nickname,
        LocalDate birth,
        String intro
) {
}
