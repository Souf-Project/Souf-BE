package com.souf.soufwebsite.global.jwt;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface JwtService {

    String createAccessToken(Member member);

    String createRefreshToken(Member member);


    Optional<String> extractAccessToken(HttpServletRequest request);

    Optional<String> extractRefreshToken(HttpServletRequest request);

    Optional<String> extractEmail(String accessToken);

    boolean isTokenValid(String token);


    long getExpiration(String token);

    void sendAccessToken(HttpServletResponse response, String newAccessToken);
}
