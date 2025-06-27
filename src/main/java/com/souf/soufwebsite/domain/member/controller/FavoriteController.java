package com.souf.soufwebsite.domain.member.controller;

import com.souf.soufwebsite.domain.member.dto.ReqDto.FavoriteMemberReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.domain.member.service.FavoriteService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static com.souf.soufwebsite.domain.member.controller.FavoriteMemberSuccessResponse.ADD_FAVORITE_SUCCESS;
import static com.souf.soufwebsite.domain.member.controller.FavoriteMemberSuccessResponse.GET_FAVORITE_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public SuccessResponse createFavoriteMember(
            @RequestBody FavoriteMemberReqDto favoriteMemberReqDto
    ) {
       favoriteService.createFavoriteMember(favoriteMemberReqDto);

       return new SuccessResponse(ADD_FAVORITE_SUCCESS.getMessage());
    }

    @GetMapping
    public SuccessResponse<Page<MemberResDto>> getFavoriteMembers(
            @RequestParam(name = "id") Long fromMemberId,
            @PageableDefault Pageable pageable
    ){
        return new SuccessResponse<>(
                favoriteService.getFavoriteMember(fromMemberId, pageable),
                GET_FAVORITE_SUCCESS.getMessage());
    }
}
