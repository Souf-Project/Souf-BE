package com.souf.soufwebsite.domain.member.service;

import com.souf.soufwebsite.domain.member.dto.ReqDto.ResetReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SigninReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.SignupReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.UpdateReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.domain.member.dto.TokenDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberService {
    void signup(SignupReqDto reqDto);

    TokenDto signin(SigninReqDto reqDto);

    void resetPassword(ResetReqDto reqDto);

    boolean sendEmailVerification(String email);

    boolean verifyEmail(String email, String code);

    void updateUserInfo(UpdateReqDto reqDto);

    Page<MemberResDto> getMembers(Pageable pageable);

    MemberResDto getMemberById(Long id);

    Page<MemberResDto> getMembersByCategory(Long first, Long second, Long third, Pageable pageable);

    Page<MemberResDto> getMembersByNickname(String nickname, Pageable pageable);
}
