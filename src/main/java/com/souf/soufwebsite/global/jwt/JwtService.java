package com.souf.soufwebsite.global.jwt;

import com.souf.soufwebsite.domain.member.entity.RoleType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface JwtService {

    String createAccessToken(String email, RoleType role);

    String createRefreshToken(String email);


    Optional<String> extractAccessToken(HttpServletRequest request);

    Optional<String> extractRefreshToken(HttpServletRequest request);

    Optional<String> extractEmail(String accessToken);

    Optional<RoleType> extractRoleType(String accessToken);

    boolean isTokenValid(String token);


    long getExpiration(String token);

    void sendAccessToken(HttpServletResponse response, String newAccessToken);
}
