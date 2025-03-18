package com.souf.soufwebsite.domain.user.dto.ReqDto;

import java.time.LocalDateTime;

public record UserReqDto(
        String email,
        String password,
        String username,
        String nickname,
        LocalDateTime birth,
        String intro
) {
}
