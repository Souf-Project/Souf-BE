package com.souf.soufwebsite.domain.member.service;

import com.souf.soufwebsite.domain.member.dto.ReqDto.ResetReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SignupReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.UpdateReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberCategoryMapping;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.exception.NotAvailableEmailException;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.exception.NotMatchPasswordException;
import com.souf.soufwebsite.domain.member.reposiotry.MemberRepository;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import com.souf.soufwebsite.global.common.category.service.CategoryService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
    private final CategoryService categoryService;
    private final MemberCategoryService memberCategoryService;

    //회원가입
    @Override
    public void signup(SignupReqDto reqDto) {
        if (memberRepository.findByEmail(reqDto.email()).isPresent()) {
            throw new NotAvailableEmailException();
        }

        if (!reqDto.password().equals(reqDto.passwordCheck())) {
            throw new NotMatchPasswordException();
        }

        String encodedPassword = passwordEncoder.encode(reqDto.password());

        Member member = new Member(reqDto.email(), encodedPassword, reqDto.username(), reqDto.nickname(), RoleType.MEMBER);

        for (CategoryDto dto : reqDto.categoryDtos()) {
            FirstCategory first = categoryService.findIfFirstIdExists(dto.firstCategory());
            SecondCategory second = categoryService.findIfSecondIdExists(dto.secondCategory());
            ThirdCategory third = categoryService.findIfThirdIdExists(dto.thirdCategory());
            categoryService.validate(first.getId(), second.getId(), third.getId());

            MemberCategoryMapping mapping = MemberCategoryMapping.of(member, first, second, third);
            member.addCategory(mapping);
        }

        memberRepository.save(member);
    }

    //로그인
    @Override
    public TokenDto signin(SigninReqDto reqDto) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(reqDto.email(), reqDto.password());

        Authentication authentication = authenticationManager.authenticate(token);
        String email = authentication.getName();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);

        String accessToken = jwtService.createAccessToken(member);
        String refreshToken = jwtService.createRefreshToken(member);

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
            throw new NotMatchPasswordException();
        }

        Member member = memberRepository.findByEmail(reqDto.email())
                .orElseThrow(NotFoundMemberException::new);

        member.updatePassword(passwordEncoder.encode(reqDto.newPassword()));
        memberRepository.save(member);
    }

    //인증번호 전송
    @Override
    public boolean sendEmailVerification(String email) {
        String emailKey = "email:verification:" + email;
        redisTemplate.delete(emailKey);
        String code = String.format("%06d", new Random().nextInt(1000000));
        String redisKey = "email:verification:" + email;
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);

        return emailService.sendEmail(email, "이메일 인증번호", "인증번호는 " + code + " 입니다.");
    }

    //인증번호 확인
    @Override
    @Transactional
    public boolean verifyEmail(String email, String code) {
        String emailKey = "email:verification:" + email;
        String storedCode = redisTemplate.opsForValue().get(emailKey);
        if (storedCode != null && storedCode.equals(code)) {
            if (email.endsWith(".ac.kr")) {
                Optional<Member> optionalMember = memberRepository.findByEmail(email);
                optionalMember.ifPresent(member -> {
                    if (member.getRole() != RoleType.STUDENT) {
                        member.updateRole(RoleType.STUDENT);
                    }
                });
            }

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
                .orElseThrow(NotFoundMemberException::new);

        member.updateInfo(reqDto);
        member.clearCategories();

        for (CategoryDto cat : reqDto.newCategories()) {
            FirstCategory first  = categoryService.findIfFirstIdExists(cat.firstCategory());
            SecondCategory second = categoryService.findIfSecondIdExists(cat.secondCategory());
            ThirdCategory third  = categoryService.findIfThirdIdExists(cat.thirdCategory());
            categoryService.validate(first.getId(), second.getId(), third.getId());
            MemberCategoryMapping mapping = MemberCategoryMapping.of(member, first, second, third);
            System.out.println("mapping = " + mapping);
            member.addCategory(mapping);
            System.out.println("member.getCategories().size() = " + member.getCategories().size());
        }
    }

    //회원 목록 조회
    @Override
    public Page<MemberResDto> getMembers(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(MemberResDto::from);
    }

    //회원 조회
    @Override
    public MemberResDto getMemberById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(NotFoundMemberException::new);
        return MemberResDto.from(member);
    }

    @Override
    public Page<MemberResDto> getMembersByCategory(Long first, Long second, Long third, Pageable pageable) {
        Page<Member> result = memberRepository.findByCategories(first, second, third, pageable);
        return result.map(MemberResDto::from);
    }

    @Override
    public Page<MemberResDto> getMembersByNickname(String nickname, Pageable pageable) {
        Page<Member> result = memberRepository.findByNicknameContainingIgnoreCase(nickname, pageable);
        return result.map(MemberResDto::from);
    }

    @Override
    public boolean isNicknameAvailable(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }
}
