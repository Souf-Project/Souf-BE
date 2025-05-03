package com.souf.soufwebsite.domain.user.dto.ResDto;

import com.souf.soufwebsite.domain.user.entity.RoleType;
import com.souf.soufwebsite.domain.user.entity.User;

import java.time.LocalDate;

public record UserResDto(
        Long id,
        String email,
        String username,
        String nickname,
        LocalDate birth,
        String intro,
        RoleType role

) {
    public static UserResDto from(User user) {
        return new UserResDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getNickname(),
                user.getBirth(),
                user.getIntro(),
                user.getRole()
        );
    }
}
