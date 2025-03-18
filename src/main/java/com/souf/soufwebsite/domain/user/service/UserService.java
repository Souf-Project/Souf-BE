package com.souf.soufwebsite.domain.user.service;

import com.souf.soufwebsite.domain.user.dto.ReqDto.EditReqDto;
import com.souf.soufwebsite.domain.user.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.user.dto.TokenDto;

public interface UserService {
    void signup(SignUpRequest reqDto);

    TokenDto signin(SigninReqDto reqDto);

    void logout(String accessToken);

    TokenDto reissue(String refreshToken);

    void resetPassword(EditReqDto reqDto);

    boolean checkSchoolVerification(String schoolName);

    boolean verifyEmail(String email, String code);

    void editUserInfo(EditReqDto reqDto);

    Object getMembers();

    Object getMemberById(Long id);
}
