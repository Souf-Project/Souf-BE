package com.souf.soufwebsite.domain.member.controller;

import com.souf.soufwebsite.domain.member.dto.ReqDto.FavoriteMemberReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "favorite_member", description = "멤버 즐겨찾기 API")
public interface FavoriteApiSpecification {

    @Operation(summary = "즐겨찾기 등록", description = "즐겨찾기 하고 싶은 유저를 등록합니다.")
    @PostMapping
    SuccessResponse createFavoriteMember(
            @RequestBody FavoriteMemberReqDto favoriteMemberReqDto
    );

    @Operation(summary = "즐겨찾기 리스트 조회", description = "즐겨찾기 등록한 유저들을 조회합니다.")
    @GetMapping
    SuccessResponse<Page<MemberResDto>> getFavoriteMembers(
            @RequestParam(name = "id") Long fromMemberId,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기 등록한 유저를 해제합니다.")
    @DeleteMapping
    SuccessResponse deleteFavoriteMember(
            @RequestBody FavoriteMemberReqDto favoriteMemberReqDto
    );
}
