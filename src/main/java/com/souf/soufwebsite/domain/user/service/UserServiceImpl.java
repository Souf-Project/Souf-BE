package com.souf.soufwebsite.domain.user.service;

import com.souf.soufwebsite.domain.user.dto.ReqDto.EditReqDto;
import com.souf.soufwebsite.domain.user.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.user.dto.ReqDto.SignupReqDto;
import com.souf.soufwebsite.domain.user.dto.TokenDto;
import com.souf.soufwebsite.domain.user.entity.User;
import com.souf.soufwebsite.domain.user.reposiotry.UserRepository;
import com.souf.soufwebsite.global.email.EmailService;
import com.souf.soufwebsite.global.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    //회원가입
    @Override
    public void signup(SignupReqDto reqDto) {
        if (userRepository.findByEmail(reqDto.email()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(reqDto.password());

        User user = new User(reqDto.email(), encodedPassword, reqDto.username(), reqDto.nickname());
        userRepository.save(user);
    }

    //로그인
    @Override
    public TokenDto signin(SigninReqDto reqDto) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(reqDto.email(), reqDto.password());

        Authentication authentication = authenticationManager.authenticate(token);
        String email = authentication.getName();

        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken(email);

        redisTemplate.opsForValue().set("refresh:" + email, refreshToken, jwtService.getExpiration(refreshToken), TimeUnit.MILLISECONDS);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //토큰 재발급
    @Override
    public TokenDto reissue(String refreshToken) {
        return null;
    }

    //비밀번호 초기화
    @Override
    public void resetPassword(EditReqDto reqDto) {

    }

    //인증번호 전송
    @Override
    public boolean sendEmailVerification(String email) {
        String code = String.format("%06d", new Random().nextInt(1000000));
        String redisKey = "email:verification:" + email;
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);

        boolean emailSent = emailService.sendEmail(email, "이메일 인증번호", "인증번호는: " + code + " 입니다.");

        return emailSent;
    }

    //인증번호 확인
    @Override
    public boolean verifyEmail(String email, String code) {
        String emailKey = "email:verification:" + email;
        String storedCode = redisTemplate.opsForValue().get(emailKey);
        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(emailKey);
            return true;
        }
        return false;
    }

    //회원정보 수정
    @Override
    public void editUserInfo(EditReqDto reqDto) {

    }

    //회원 목록 조회
    @Override
    public Object getMembers() {
        return null;
    }

    //회원 조회
    @Override
    public Object getMemberById(Long id) {
        return null;
    }
}
