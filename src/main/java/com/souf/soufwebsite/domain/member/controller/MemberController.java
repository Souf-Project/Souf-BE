package com.souf.soufwebsite.domain.member.controller;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.SendModifyEmailReqDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.UpdateReqDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberSimpleResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberUpdateResDto;
import com.souf.soufwebsite.domain.member.service.general.MemberService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import jakarta.validation.Valid;
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
    public SuccessResponse<Page<MemberSimpleResDto>> getMembers(
            @RequestParam(name = "firstCategory", required = false) Long first,
            @RequestParam(name = "secondCategory", required = false) Long second,
            @RequestParam(name = "thirdCategory", required = false) Long third,
            @PageableDefault(size = 6) Pageable pageable) {

        return new SuccessResponse<>(memberService.getMembers(first, second, third, pageable),
                "멤버 목록을 조회했습니다.");
    }

    @GetMapping("/myinfo")
    public SuccessResponse<MemberResDto> getMyInfo(
            @CurrentEmail String email
    ) {
        MemberResDto meDto = memberService.getMyInfo(email);
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
    public SuccessResponse<MemberUpdateResDto> updateUserInfo(
            @CurrentEmail String email,
            @RequestBody UpdateReqDto reqDto) {
        MemberUpdateResDto resDto = memberService.updateUserInfo(email, reqDto);
        return new SuccessResponse<>(resDto, "회원정보 수정 성공");
    }

    @PostMapping("/modify/email/send")
    public SuccessResponse<?> sendModifyEmailVerification(
            @RequestBody @Valid SendModifyEmailReqDto reqDto) {

        memberService.sendModifyEmailVerification(reqDto);

        return new SuccessResponse<>("인증번호가 전송되었습니다.");
    }

    @PostMapping("/upload")
    public SuccessResponse uploadMetadata(@Valid @RequestBody MediaReqDto mediaReqDto){
        memberService.uploadProfileImage(mediaReqDto);

        return new SuccessResponse("회원프로필 업로드에 성공하였습니다.");
    }
}
