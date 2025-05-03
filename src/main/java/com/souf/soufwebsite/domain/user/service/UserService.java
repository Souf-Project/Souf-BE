package com.souf.soufwebsite.domain.user.service;

import com.souf.soufwebsite.domain.user.dto.ReqDto.ResetReqDto;
import com.souf.soufwebsite.domain.user.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.user.dto.ReqDto.SignupReqDto;
import com.souf.soufwebsite.domain.user.dto.ResDto.UserResDto;
import com.souf.soufwebsite.domain.user.dto.TokenDto;

import java.util.List;

public interface UserService {
    void signup(SignupReqDto reqDto);

    TokenDto signin(SigninReqDto reqDto);

    void resetPassword(ResetReqDto reqDto);

    boolean sendEmailVerification(String email);

    boolean verifyEmail(String email, String code);

    void editUserInfo(ResetReqDto reqDto);

    List<UserResDto> getMembers();

    UserResDto getMemberById(Long id);

}
