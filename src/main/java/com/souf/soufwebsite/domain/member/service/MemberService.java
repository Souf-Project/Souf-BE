package com.souf.soufwebsite.domain.member.service;

import com.souf.soufwebsite.domain.member.dto.reqDto.ResetReqDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.SigninReqDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.SignupReqDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.UpdateReqDto;
import com.souf.soufwebsite.domain.member.dto.resDto.UserResDto;
import com.souf.soufwebsite.domain.member.dto.TokenDto;

import java.util.List;

public interface MemberService {
    void signup(SignupReqDto reqDto);

    TokenDto signin(SigninReqDto reqDto);

    void resetPassword(ResetReqDto reqDto);

    boolean sendEmailVerification(String email);

    boolean verifyEmail(String email, String code);

    void updateUserInfo(UpdateReqDto reqDto);

    List<UserResDto> getMembers();

    UserResDto getMemberById(Long id);

}
