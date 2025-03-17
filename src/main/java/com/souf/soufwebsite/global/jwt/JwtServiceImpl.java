package com.souf.soufwebsite.global.jwt;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtService jwtService;


    @Override
    public String createAccessToken(String email) {
        return "";
    }

    @Override
    public String createRefreshToken(String email) {
        return "";
    }

    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.empty();
    }

    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.empty();
    }

    @Override
    public Optional<String> extractEmail(String accessToken) {
        return Optional.empty();
    }

    @Override
    public boolean isTokenValid(String token) {
        return false;
    }

    @Override
    public void sendAccessToken(HttpServletResponse response, String newAccessToken) {

    }
}
