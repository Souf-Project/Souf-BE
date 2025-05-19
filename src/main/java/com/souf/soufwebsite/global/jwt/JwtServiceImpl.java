package com.souf.soufwebsite.global.jwt;

import com.souf.soufwebsite.domain.member.entity.RoleType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access.expiration}")
    private long accessTokenExpireTime;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpireTime;

    @Value("${jwt.access.header}")
    private String accessTokenHeader;

    @Value("${jwt.refresh.header}")
    private String refreshTokenHeader;

    private Key secretKey;

    @PostConstruct
    public void init() {
        // jwtSecret을 바탕으로 암호화에 사용할 Key 객체 생성
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String createAccessToken(String email, RoleType role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpireTime);
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String createRefreshToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpireTime);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader(accessTokenHeader);
        if (header != null && header.startsWith("Bearer ")) {
            return Optional.of(header.substring("Bearer ".length()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        String header = request.getHeader(refreshTokenHeader);
        if (header != null && header.startsWith("Bearer ")) {
            return Optional.of(header.substring("Bearer ".length()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> extractEmail(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException e) {
            log.error("토큰에서 이메일 추출 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<RoleType> extractRoleType(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);
            return Optional.of(RoleType.valueOf(role));
        } catch (Exception e) {
            log.error("권한 추출 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("토큰 유효성 검사 실패: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public long getExpiration(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().getTime();
        } catch (JwtException e) {
            log.error("토큰 만료 시간 추출 실패: {}", e.getMessage());
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }

    @Override
    public void sendAccessToken(HttpServletResponse response, String newAccessToken) {
        response.setHeader(accessTokenHeader, "Bearer " + newAccessToken);
    }
}
