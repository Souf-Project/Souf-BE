package com.souf.soufwebsite.domain.member.service.general;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.file.service.S3UploaderService;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.*;
import com.souf.soufwebsite.domain.member.dto.reqDto.signup.SignupReqDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberSimpleResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberUpdateResDto;
import com.souf.soufwebsite.domain.member.entity.ApprovedStatus;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberCategoryMapping;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.exception.*;
import com.souf.soufwebsite.domain.member.mapper.SignupMapper;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.report.exception.DeclaredMemberException;
import com.souf.soufwebsite.domain.report.service.BanService;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import com.souf.soufwebsite.global.common.category.service.CategoryService;
import com.souf.soufwebsite.global.common.mail.SesMailService;
import com.souf.soufwebsite.global.jwt.JwtService;
import com.souf.soufwebsite.global.slack.service.SlackService;
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
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final S3UploaderService s3UploaderService;
    private final SlackService slackService;

    private final SesMailService mailService;
    private final BanService banService;

    private final CategoryService categoryService;

    private final SignupMapper signupMapper;

    //회원가입
    @Transactional
    @Override
    public MemberUpdateResDto signup(SignupReqDto reqDto) {

        if (redisTemplate.hasKey("email:withdraw:" + reqDto.email())) {
            throw new NotAllowedSignupException();
        }

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

        String encodedPassword = passwordEncoder.encode(reqDto.password());

        // 개인 정보 동의 확인
        if (reqDto.isPersonalInfoAgreed().equals(Boolean.FALSE) || reqDto.isServiceUtilizationAgreed().equals(Boolean.FALSE) || reqDto.isSuitableAged().equals(Boolean.FALSE)) {
            throw new NotAgreedPersonalInfoException();
        }

        ApprovedStatus status = ApprovedStatus.PENDING;
        Member member = new Member(status, reqDto.email(), encodedPassword, reqDto.username(), reqDto.nickname(), reqDto.phoneNumber(), reqDto.roleType(), reqDto.isMarketingAgreed());

        injectCategories(reqDto, member);

        PresignedUrlResDto presignedUrlResDto = signupMapper.signupByRole(member, reqDto);

        memberRepository.save(member);

//        indexEventPublisherHelper.publishIndexEvent(
//                EntityType.MEMBER,
//                OperationType.CREATE,
//                "Member",
//                member
//        );

        redisTemplate.delete(verifiedKey);
        slackService.sendSlackMessage(member.getNickname() + " 님이 회원가입했습니다.", "signup");



        return new MemberUpdateResDto(member.getId(), presignedUrlResDto);
    }

    //로그인
    @Override
    public TokenDto signin(SigninReqDto reqDto, HttpServletResponse response) {
        log.info("email: {}", reqDto.email());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(reqDto.email(), reqDto.password());

        Authentication authentication = authenticationManager.authenticate(token);
        String email = authentication.getName();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);

        if(banService.isBanned(member.getId())){
            Optional<Duration> remaining = banService.remaining(member.getId());
            String msg = remaining.map(duration -> "remaining: " + duration.toHours() + "h").orElse("permanent");
            throw new DeclaredMemberException(msg);
        }

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
    public void sendSignupEmailVerification(SendEmailReqDto reqDto) {
        if (redisTemplate.hasKey("email:withdraw:" + reqDto.email())) {
            throw new NotAllowedSignupException();
        }
        if (memberRepository.existsByEmail(reqDto.email())) {
            throw new NotAvailableEmailException();
        }
        sendEmailCode(reqDto.email());
    }

    @Override
    public void sendResetEmailVerification(SendEmailReqDto reqDto) {
        if (!memberRepository.existsByEmail(reqDto.email())) {
            throw new NotFoundMemberException();
        }
        sendEmailCode(reqDto.email());
    }

    @Override
    public void sendModifyEmailVerification(SendModifyEmailReqDto reqDto) {
        if (!memberRepository.existsByEmail(reqDto.originalEmail())) {
            throw new NotFoundMemberException();
        }
        if (!reqDto.acKrEmail().endsWith(".ac.kr")) {
            throw new NotValidEmailException();
        }
        sendModifyEmailCode(reqDto.originalEmail(), reqDto.acKrEmail());
    }

    private void sendEmailCode(String email) {
        String redisKey = "email:verification:" + email;

        redisTemplate.delete(redisKey);
        String code = String.format("%06d", new Random().nextInt(1000000));
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);

        mailService.sendEmailAuthenticationCode(email, "인증번호", code);
    }

    private void sendModifyEmailCode(String originalEmail, String acKrEmail) {
        String code = String.format("%06d", new Random().nextInt(1_000_000));
        String verifyKey = "email:verification:" + acKrEmail;
        String ownerKey = "email:owner:" + acKrEmail;

        redisTemplate.opsForValue().set(verifyKey, code, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(ownerKey, originalEmail, 5, TimeUnit.MINUTES);

        mailService.sendEmailAuthenticationCode(acKrEmail, "학생 사용자 확인 인증번호", code);
    }

    //인증번호 확인
    @Override
    @Transactional
    public boolean verifyEmail(VerifyEmailReqDto reqDto) {
        String emailKey = "email:verification:" + reqDto.email();
        String storedCode = redisTemplate.opsForValue().get(emailKey);

        if (storedCode == null || !storedCode.equals(reqDto.code())) {
            log.info("storeCode: {}, reqCode: {}", storedCode, reqDto.code());
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
    public MemberUpdateResDto updateUserInfo(String email, UpdateReqDto reqDto) {
        Long memberId = findIfEmailExists(email).getId();
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

//        indexEventPublisherHelper.publishIndexEvent(
//                EntityType.MEMBER,
//                OperationType.CREATE,
//                "Member",
//                member
//        );

        return MemberUpdateResDto.of(member.getId(), presignedUrlResDtos.get(0));
    }

    @Override
    public void uploadProfileImage(MediaReqDto reqDto) {
        Member member = memberRepository.findById(reqDto.postId()).orElseThrow(NotFoundMemberException::new);
        fileService.uploadMetadata(reqDto, PostType.PROFILE, member.getId());
    }

    @Override
    public void uploadAuthenticationImage(MediaReqDto reqDto) {
        Member member = memberRepository.findById(reqDto.postId()).orElseThrow(NotFoundMemberException::new);
        fileService.uploadMetadata(reqDto, PostType.AUTHENTICATION, member.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberSimpleResDto> getMembers(
            Long first, Long second, Long third,
            Pageable pageable) {
        categoryService.validate(first, second, third);

        return memberRepository.getMemberList(first, second, third, pageable);
    }


    //내 정보 조회
    @Override
    @Transactional(readOnly = true)
    public MemberResDto getMyInfo(String email) {
        Member member = findIfEmailExists(email);
        Member myMember = memberRepository.findById(member.getId()).orElseThrow(NotFoundMemberException::new); // 지연 로딩 오류 해결
        String mediaUrl = fileService.getMediaUrl(PostType.PROFILE, member.getId());
        return MemberResDto.from(myMember, myMember.getCategories(), mediaUrl, member.isMarketingAgreement());
    }

    //회원 조회
    @Override
    @Transactional(readOnly = true)
    public MemberResDto getMemberById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(NotFoundMemberException::new);
        String mediaUrl = fileService.getMediaUrl(PostType.PROFILE, member.getId());

        return MemberResDto.from(member, member.getCategories(), mediaUrl, false);
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
    public void withdraw(String email, WithdrawReqDto reqDto) {
        Member currentMember = findIfEmailExists(email);
        Long memberId = currentMember.getId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        if (!passwordEncoder.matches(reqDto.password(), member.getPassword())) {
            throw new NotMatchPasswordException();
        }

        String redisKey = "email:withdraw:" + member.getEmail();
        redisTemplate.opsForValue().set(redisKey, "CanNotSignedUpFor7Days", 7, TimeUnit.DAYS);
//        memberRepository.delete(member); // 탈퇴하면 삭제가 아닌 개인정보 들만 교체
        member.softDelete();

//        indexEventPublisherHelper.publishIndexEvent(
//                EntityType.MEMBER,
//                OperationType.DELETE,
//                "Member",
//                member.getId()
//        );
    }

    private void injectCategories(SignupReqDto reqDto, Member member) {
        for (CategoryDto dto : reqDto.categoryDtos()) {
            FirstCategory first = categoryService.findIfFirstIdExists(dto.firstCategory());
            SecondCategory second = categoryService.findIfSecondIdExists(dto.secondCategory());
            ThirdCategory third = categoryService.findIfThirdIdExists(dto.thirdCategory());
            categoryService.validate(dto.firstCategory(), dto.secondCategory(), dto.thirdCategory());

            MemberCategoryMapping mapping = MemberCategoryMapping.of(member, first, second, third);
            member.addCategory(mapping);
        }
    }

    private Member findIfEmailExists(String email) {
        return memberRepository.findByEmail(email).orElseThrow(NotFoundMemberException::new);
    }
}
