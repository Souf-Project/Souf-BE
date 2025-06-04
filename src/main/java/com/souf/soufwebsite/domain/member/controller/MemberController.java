package com.souf.soufwebsite.domain.member.controller;

import com.souf.soufwebsite.domain.member.dto.ReqDto.UpdateReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.domain.member.service.MemberService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Slf4j
public class MemberController implements MemberApiSpecification{

    private final MemberService memberService;

    @GetMapping
    public SuccessResponse<Page<MemberResDto>> getMembers(
            @PageableDefault(size = 6) Pageable pageable
    ) {
        return new SuccessResponse<>(memberService.getMembers(pageable));
    }

    @GetMapping("/myinfo")
    public SuccessResponse<MemberResDto> getMyInfo() {
        MemberResDto meDto = memberService.getMyInfo();
        return new SuccessResponse<>(meDto);
    }

    @GetMapping("/{id}")
    public SuccessResponse<MemberResDto> getMemberById(@PathVariable Long id) {
        return new SuccessResponse<>(memberService.getMemberById(id));
    }

//    @GetMapping("/search")
//    public SuccessResponse<Page<MemberResDto>> findByCategory(
//            @RequestParam(required = false) Long first,
//            @PageableDefault(size = 6) Pageable pageable
//    ) {
//        return new SuccessResponse<>(
//                memberService.getMembersByCategory(first, pageable),
//                "카테고리로 검색한 멤버 목록입니다."
//        );
//    }
//
//    @GetMapping("/search/nickname")
//    public SuccessResponse<Page<MemberResDto>> findByNickname(
//            @RequestParam String keyword,
//            @PageableDefault(size = 6) Pageable pageable
//    ) {
//        return new SuccessResponse<>(
//                memberService.getMembersByNickname(keyword, pageable),
//                "닉네임으로 검색한 멤버 목록입니다."
//        );
//    }

    @PutMapping("/update")
    public SuccessResponse<?> updateUserInfo(@RequestBody UpdateReqDto reqDto) {
        memberService.updateUserInfo(reqDto);
        return new SuccessResponse<>("회원정보 수정 성공");
    }
}
