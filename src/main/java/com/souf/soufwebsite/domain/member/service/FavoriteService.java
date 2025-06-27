package com.souf.soufwebsite.domain.member.service;

import com.souf.soufwebsite.domain.member.dto.ReqDto.FavoriteMemberReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {

    void createFavoriteMember(FavoriteMemberReqDto favoriteMemberReqDto);

    Page<MemberResDto> getFavoriteMember(Long fromMemberId, Pageable pageable);
}
