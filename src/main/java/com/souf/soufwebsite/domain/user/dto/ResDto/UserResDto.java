package com.souf.soufwebsite.domain.user.dto.ResDto;

import com.souf.soufwebsite.domain.user.entity.User;

public record UserResDto(
        Long id,
        String email,
        String username,
        String nickname
) {
    public static UserResDto fromEntity(User user) {
        return new UserResDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getNickname()
        );
    }
}
