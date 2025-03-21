package com.souf.soufwebsite.domain.user.service;

import com.souf.soufwebsite.domain.user.dto.ReqDto.EditReqDto;
import com.souf.soufwebsite.domain.user.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.user.dto.ReqDto.SignupReqDto;
import com.souf.soufwebsite.domain.user.dto.ResDto.UserResDto;
import com.souf.soufwebsite.domain.user.dto.TokenDto;
import com.souf.soufwebsite.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void signup(SignupReqDto reqDto);

    TokenDto signin(SigninReqDto reqDto);

    TokenDto reissue(String refreshToken);

    void resetPassword(EditReqDto reqDto);

    boolean sendEmailVerification(String email);

    boolean verifyEmail(String email, String code);

    void editUserInfo(EditReqDto reqDto);

    List<UserResDto> getMembers();

    UserResDto getMemberById(Long id);

}
