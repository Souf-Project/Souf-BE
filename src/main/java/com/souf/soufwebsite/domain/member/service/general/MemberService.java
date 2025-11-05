package com.souf.soufwebsite.domain.member.service.general;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.*;
import com.souf.soufwebsite.domain.member.dto.reqDto.addInfo.AddCompanyInfoReqDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.addInfo.AddStudentInfoReqDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.signup.SignupReqDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberSimpleResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberUpdateResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.info.MemberInfo;
import com.souf.soufwebsite.domain.member.dto.resDto.info.MemberInfoResDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {
    MemberUpdateResDto signup(SignupReqDto reqDto);

    TokenDto signin(SigninReqDto reqDto, HttpServletResponse response);

    TokenDto reissueToken(HttpServletRequest req, HttpServletResponse res);

    void resetPassword(ResetReqDto reqDto);

    void sendSignupEmailVerification(SendEmailReqDto reqDto);

    void sendResetEmailVerification(SendEmailReqDto reqDto);

    void sendModifyEmailVerification(SendModifyEmailReqDto reqDto);

    boolean verifyEmail(VerifyEmailReqDto reqDto);

    MemberUpdateResDto updateUserInfo(String email, UpdateReqDto reqDto);

    void uploadProfileImage(MediaReqDto reqDto);

    void uploadAuthenticationImage(MediaReqDto reqDto);

    Page<MemberSimpleResDto> getMembers(Long first, Long second, Long third, Pageable pageable);

    MemberInfoResDto<? extends MemberInfo> getMyInfo(String email);

    MemberResDto getMemberById(Long id);

//    Page<MemberResDto> getMembersByCategory(Long first, Pageable pageable);
//
//    Page<MemberResDto> getMembersByNickname(String nickname, Pageable pageable);

    boolean isNicknameAvailable(String nickname);

    void withdraw(String email, WithdrawReqDto reqDto);

    PresignedUrlResDto addStudentInfo(String email, AddStudentInfoReqDto reqDto);

    PresignedUrlResDto addCompanyInfo(String email, AddCompanyInfoReqDto reqDto);
}
