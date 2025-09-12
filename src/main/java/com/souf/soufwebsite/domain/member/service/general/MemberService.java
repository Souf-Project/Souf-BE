package com.souf.soufwebsite.domain.member.service.general;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.*;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberSimpleResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberUpdateResDto;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {
    void signup(SignupReqDto reqDto);

    TokenDto signin(SigninReqDto reqDto, HttpServletResponse response);

    void resetPassword(ResetReqDto reqDto);

    void sendSignupEmailVerification(SendEmailReqDto reqDto);

    void sendResetEmailVerification(SendEmailReqDto reqDto);

    void sendModifyEmailVerification(SendModifyEmailReqDto reqDto);

    boolean verifyEmail(VerifyEmailReqDto reqDto);

    MemberUpdateResDto updateUserInfo(String email, UpdateReqDto reqDto);

    void uploadProfileImage(MediaReqDto reqDto);

    Page<MemberSimpleResDto> getMembers(Long first, Long second, Long third, MemberSearchReqDto searchReqDto, Pageable pageable);

    MemberResDto getMyInfo(String email);

    MemberResDto getMemberById(Long id);

//    Page<MemberResDto> getMembersByCategory(Long first, Pageable pageable);
//
//    Page<MemberResDto> getMembersByNickname(String nickname, Pageable pageable);

    boolean isNicknameAvailable(String nickname);

    void withdraw(String email, WithdrawReqDto reqDto);
}
