package com.souf.soufwebsite.domain.member.service;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.dto.ReqDto.*;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberSimpleResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberUpdateResDto;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberCategoryMapping;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.exception.*;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import com.souf.soufwebsite.global.common.category.service.CategoryService;
import com.souf.soufwebsite.global.email.EmailService;
import com.souf.soufwebsite.global.jwt.JwtService;
import com.souf.soufwebsite.global.util.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    private Member getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }
    private final CategoryService categoryService;

    //회원가입
    @Override
    public void signup(SignupReqDto reqDto) {

        String verifiedKey = "email:verified:" + reqDto.email();
        String isVerified = redisTemplate.opsForValue().get(verifiedKey);
        if (!"true".equals(isVerified)) {
            throw new NotVerifiedEmailException();
        }

        if (memberRepository.findByEmail(reqDto.email()).isPresent()) {
            throw new NotAvailableEmailException();
        }

        if (!reqDto.password().equals(reqDto.passwordCheck())) {
            throw new NotMatchPasswordException();
        }

        RoleType role = reqDto.email().endsWith(".ac.kr") ? RoleType.STUDENT : RoleType.MEMBER;

        String encodedPassword = passwordEncoder.encode(reqDto.password());

        Member member = new Member(reqDto.email(), encodedPassword, reqDto.username(), reqDto.nickname(), role);

        injectCatogies(reqDto, member);

        memberRepository.save(member);

        redisTemplate.delete(verifiedKey);
    }

    //로그인
    @Override
    public TokenDto signin(SigninReqDto reqDto, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(reqDto.email(), reqDto.password());

        Authentication authentication = authenticationManager.authenticate(token);
        String email = authentication.getName();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);

        String accessToken = jwtService.createAccessToken(member);
        String refreshToken = jwtService.createRefreshToken(member);
        redisTemplate.opsForValue().set("refresh:" + email, refreshToken, jwtService.getExpiration(refreshToken), TimeUnit.MILLISECONDS);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        return TokenDto.builder()
                .accessToken(accessToken)
                .memberId(member.getId())
                .nickname(member.getNickname())
                .roleType(member.getRole())
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
    public boolean sendSignupEmailVerification(SendEmailReqDto reqDto) {
        if (memberRepository.existsByEmail(reqDto.email())) {
            throw new NotAvailableEmailException();
        }
        return sendEmailCode(reqDto.email());
    }

    @Override
    public boolean sendResetEmailVerification(SendEmailReqDto reqDto) {
        if (!memberRepository.existsByEmail(reqDto.email())) {
            throw new NotFoundMemberException();
        }
        return sendEmailCode(reqDto.email());
    }

    @Override
    public boolean sendModifyEmailVerification(SendModifyEmailReqDto reqDto) {
        if (!memberRepository.existsByEmail(reqDto.originalEmail())) {
            throw new NotFoundMemberException();
        }
        if (!reqDto.acKrEmail().endsWith(".ac.kr")) {
            throw new NotValidEmailException();
        }
        return sendModifyEmailCode(reqDto.originalEmail(), reqDto.acKrEmail());
    }

    private boolean sendEmailCode(String email) {
        String redisKey = "email:verification:" + email;

        redisTemplate.delete(redisKey);
        String code = String.format("%06d", new Random().nextInt(1000000));
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);

        return emailService.sendEmail(email, "이메일 인증번호", "인증번호는 " + code + " 입니다.");
    }

    private boolean sendModifyEmailCode(String originalEmail, String acKrEmail) {
        String code = String.format("%06d", new Random().nextInt(1_000_000));
        String verifyKey = "email:verification:" + acKrEmail;
        String ownerKey = "email:owner:" + acKrEmail;

        redisTemplate.opsForValue().set(verifyKey, code, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(ownerKey, originalEmail, 5, TimeUnit.MINUTES);

        return emailService.sendEmail(acKrEmail, "학생 인증 메일", "인증번호는 " + code + " 입니다.");
    }

    //인증번호 확인
    @Override
    @Transactional
    public boolean verifyEmail(VerifyEmailReqDto reqDto) {
        String emailKey = "email:verification:" + reqDto.email();
        String storedCode = redisTemplate.opsForValue().get(emailKey);

        if (storedCode == null || !storedCode.equals(reqDto.code())) {
            return false;
        }

        switch (reqDto.purpose()) {
            case SIGNUP -> {
                String verifiedKey = "email:verified:" + reqDto.email();
                redisTemplate.opsForValue().set(verifiedKey, "true", Duration.ofMinutes(30));
            }

            case MODIFY -> {
                String ownerKey = "email:owner:" + reqDto.email();
                String ownerEmail = redisTemplate.opsForValue().get(ownerKey);

                if (ownerEmail == null) {
                    throw new NotValidEmailException();
                }

                Member member = memberRepository.findByEmail(ownerEmail)
                        .orElseThrow(NotFoundMemberException::new);

                if (reqDto.email().endsWith(".ac.kr")) {
                    member.updateRole(RoleType.STUDENT);
                }

                redisTemplate.delete(ownerKey);
            }
        }

        redisTemplate.delete(emailKey);
        return true;
    }

    //회원정보 수정
    @Override
    @Transactional
    public MemberUpdateResDto updateUserInfo(UpdateReqDto reqDto) {
        Long memberId = getCurrentUser().getId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        member.updateInfo(reqDto);
        member.clearCategories();

        for (CategoryDto cat : reqDto.newCategories()) {
            FirstCategory first = categoryService.findIfFirstIdExists(cat.firstCategory());
            SecondCategory second = categoryService.findIfSecondIdExists(cat.secondCategory());
            ThirdCategory third = categoryService.findIfThirdIdExists(cat.thirdCategory());

            categoryService.validate(cat.firstCategory(), cat.secondCategory(), cat.thirdCategory());
            MemberCategoryMapping mapping = MemberCategoryMapping.of(member, first, second, third);
            log.info("mapping = {}", mapping);
            member.addCategory(mapping);
            log.info("member.getCategories().size() = {}", member.getCategories().size());
        }

        fileService.clearMediaList(PostType.PROFILE, memberId);
        List<PresignedUrlResDto> presignedUrlResDtos;
        if (reqDto.profileOriginalFileName() != null) {
            presignedUrlResDtos = fileService.generatePresignedUrl("profile", List.of(reqDto.profileOriginalFileName()));
        } else {
            presignedUrlResDtos = List.of(new PresignedUrlResDto("", "", ""));
        }

        return MemberUpdateResDto.of(member.getId(), presignedUrlResDtos.get(0));
    }

    @Override
    public void uploadProfileImage(MediaReqDto reqDto) {
        Member member = memberRepository.findById(reqDto.postId()).orElseThrow(NotFoundMemberException::new);
        fileService.uploadMetadata(reqDto, PostType.PROFILE, member.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberSimpleResDto> getMembers(
            Long first, Long second, Long third,
            MemberSearchReqDto searchReqDto,
            Pageable pageable) {
        categoryService.validate(first, second, third);

        return memberRepository.getMemberList(first, second, third, searchReqDto, pageable);
    }


    //내 정보 조회
    @Override
    @Transactional(readOnly = true)
    public MemberResDto getMyInfo() {
        Member member = getCurrentUser();
        Member myMember = memberRepository.findById(member.getId()).orElseThrow(NotFoundMemberException::new); // 지연 로딩 오류 해결
        String mediaUrl = fileService.getMediaUrl(PostType.PROFILE, member.getId());
        return MemberResDto.from(myMember, myMember.getCategories(), mediaUrl);
    }

    //회원 조회
    @Override
    @Transactional(readOnly = true)
    public MemberResDto getMemberById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(NotFoundMemberException::new);
        String mediaUrl = fileService.getMediaUrl(PostType.PROFILE, member.getId());

        return MemberResDto.from(member, member.getCategories(), mediaUrl);
    }

//    @Override
//    public Page<MemberResDto> getMembersByCategory(Long first, Pageable pageable) {
//        Page<Member> result = memberRepository.findByCategory(first, pageable);
//        return result.map(MemberResDto::from);
//    }
//
//    @Override
//    public Page<MemberResDto> getMembersByNickname(String nickname, Pageable pageable) {
//        Page<Member> result = memberRepository.findByNicknameContainingIgnoreCase(nickname, pageable);
//        return result.map(MemberResDto::from);
//    }

    @Override
    public boolean isNicknameAvailable(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }

    @Override
    @Transactional
    public void withdraw(WithdrawReqDto reqDto) {
        Long memberId = getCurrentUser().getId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        if (!passwordEncoder.matches(reqDto.password(), member.getPassword())) {
            throw new NotMatchPasswordException();
        }

        member.softDelete();
    }

    private void injectCatogies(SignupReqDto reqDto, Member member) {
        for (CategoryDto dto : reqDto.categoryDtos()) {
            FirstCategory first = categoryService.findIfFirstIdExists(dto.firstCategory());
            SecondCategory second = categoryService.findIfSecondIdExists(dto.secondCategory());
            ThirdCategory third = categoryService.findIfThirdIdExists(dto.thirdCategory());
            categoryService.validate(dto.firstCategory(), dto.secondCategory(), dto.thirdCategory());

            MemberCategoryMapping mapping = MemberCategoryMapping.of(member, first, second, third);
            member.addCategory(mapping);
        }
    }
}
