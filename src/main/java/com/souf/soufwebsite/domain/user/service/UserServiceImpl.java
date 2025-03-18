package com.souf.soufwebsite.domain.user.service;

import com.souf.soufwebsite.domain.user.dto.ReqDto.EditReqDto;
import com.souf.soufwebsite.domain.user.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.user.dto.TokenDto;
import com.souf.soufwebsite.global.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;


    @Override
    public void signup(SignUpRequest reqDto) {

    }

    @Override
    public TokenDto signin(SigninReqDto reqDto) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(reqDto.email(), reqDto.password());

        Authentication authentication = authenticationManager.authenticate(token);
        String email = authentication.getName();

        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken(email);

        redisTemplate.opsForValue().set("refresh:" + email, refreshToken, jwtService.getExpiration(refreshToken), TimeUnit.MILLISECONDS);

        // 4. TokenDto 반환
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void logout(String accessToken) {

    }

    @Override
    public TokenDto reissue(String refreshToken) {
        return null;
    }

    @Override
    public void resetPassword(EditReqDto reqDto) {

    }

    @Override
    public boolean checkSchoolVerification(String schoolName) {
        return false;
    }

    @Override
    public boolean verifyEmail(String email, String code) {
        return false;
    }

    @Override
    public void editUserInfo(EditReqDto reqDto) {

    }

    @Override
    public Object getMembers() {
        return null;
    }

    @Override
    public Object getMemberById(Long id) {
        return null;
    }
}
