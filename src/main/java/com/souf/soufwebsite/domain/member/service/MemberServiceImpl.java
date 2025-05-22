package com.souf.soufwebsite.domain.member.service;

import com.souf.soufwebsite.domain.member.dto.*;
import com.souf.soufwebsite.domain.member.dto.ReqDto.ResetReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SignupReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.UpdateReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.reposiotry.MemberRepository;
import com.souf.soufwebsite.global.email.EmailService;
import com.souf.soufwebsite.global.jwt.JwtService;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private Member getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    //회원가입
    @Override
    public void signup(SignupReqDto reqDto) {
        if (memberRepository.findByEmail(reqDto.email()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if (!reqDto.password().equals(reqDto.passwordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(reqDto.password());

        Member member = new Member(reqDto.email(), encodedPassword, reqDto.username(), reqDto.nickname());
        memberRepository.save(member);
    }

    //로그인
    @Override
    public TokenDto signin(SigninReqDto reqDto) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(reqDto.email(), reqDto.password());

        Authentication authentication = authenticationManager.authenticate(token);
        String email = authentication.getName();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        RoleType role = member.getRole();

        String accessToken = jwtService.createAccessToken(email, role);
        String refreshToken = jwtService.createRefreshToken(email);

        redisTemplate.opsForValue().set("refresh:" + email, refreshToken, jwtService.getExpiration(refreshToken), TimeUnit.MILLISECONDS);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //비밀번호 초기화
    @Override
    public void resetPassword(ResetReqDto reqDto) {
        if (!reqDto.newPassword().equals(reqDto.confirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Member member = memberRepository.findByEmail(reqDto.email())
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 찾을 수 없습니다."));

        member.updatePassword(passwordEncoder.encode(reqDto.newPassword()));
        memberRepository.save(member);
    }

    //인증번호 전송
    @Override
    public boolean sendEmailVerification(String email) {
        String code = String.format("%06d", new Random().nextInt(1000000));
        String redisKey = "email:verification:" + email;
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);

        return emailService.sendEmail(email, "이메일 인증번호", "인증번호는 " + code + " 입니다.");
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
    @Transactional
    public void updateUserInfo(UpdateReqDto reqDto) {
        Long memberId = getCurrentUser().getId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        member.updateInfo(reqDto); // 도메인에 위임
    }

    //회원 목록 조회
    @Override
    public List<MemberResDto> getMembers(Pageable pageable) {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(MemberResDto::from)
                .collect(Collectors.toList());
    }

    //회원 조회
    @Override
    public MemberResDto getMemberById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return MemberResDto.from(member);
    }

    @Override
    public Page<MemberResDto> searchMembers(String keyword, Pageable pageable) {
        Page<Member> result = memberRepository.searchMembers(keyword, pageable);
        return result.map(MemberResDto::from);
    }
}
