package com.souf.soufwebsite.domain.member.service.favorite;

import com.souf.soufwebsite.domain.member.dto.reqDto.FavoriteMemberReqDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {

    void createFavoriteMember(FavoriteMemberReqDto favoriteMemberReqDto);

    Page<MemberResDto> getFavoriteMember(Long fromMemberId, Pageable pageable);

    void deleteFavoriteMember(FavoriteMemberReqDto favoriteMemberReqDto);
}
