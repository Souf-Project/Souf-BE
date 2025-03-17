package com.souf.soufwebsite.global.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface JwtService {

    String createAccessToken(String email);

    String createRefreshToken(String email);


    Optional<String> extractAccessToken(HttpServletRequest request);

    Optional<String> extractRefreshToken(HttpServletRequest request);

    Optional<String> extractEmail(String accessToken);


    boolean isTokenValid(String token);


    void sendAccessToken(HttpServletResponse response, String newAccessToken);
}
