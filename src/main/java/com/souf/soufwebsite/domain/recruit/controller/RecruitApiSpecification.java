package com.souf.soufwebsite.domain.recruit.controller;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.MemberIdReqDto;
import com.souf.soufwebsite.domain.recruit.dto.*;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Recruit", description = "공고문 관련 API")
public interface RecruitApiSpecification {

    @Operation(summary = "공고문 생성", description = "일반 사용자의 권한을 가진 사용자가 공고문을 생성합니다.")
    @PostMapping
    SuccessResponse<RecruitCreateResDto> createRecruit(
            @CurrentEmail String email,
            @Valid @RequestBody RecruitReqDto recruitReqDto);

    @Operation(summary = "해당 공고문 관련 미디어 파일 정보 저장", description = "제공된 presignedUrl을 통해 업로드한 파일의 정보를 DB에도 반영할 수 있도록 서버에게 파일 정보를 보냅니다.")
    @PostMapping("/upload")
    SuccessResponse uploadMetadata(@Valid @RequestBody MediaReqDto mediaReqDto);

    @Operation(summary = "공고문 리스트 조회", description = "카테고리에 걸맞는 공고문 리스트를 조회합니다.")
    @GetMapping
    SuccessResponse<Page<RecruitSimpleResDto>> getRecruits(@RequestParam(name = "firstCategory") Long first,
                                                           @RequestParam(name = "secondCategory") Long second,
                                                           @RequestParam(name = "thirdCategory") Long third,
                                                           @RequestBody RecruitSearchReqDto recruitSearchReqDto,
                                                           @PageableDefault(size = 12) Pageable pageable);

    @Operation(summary = "특정 공고문 상세 조회", description = "특정 공고문에 대한 상세 정보를 조회합니다.")
    @GetMapping("/{recruitId}")
    SuccessResponse<RecruitResDto> getRecruitById(@PathVariable(name = "recruitId") Long recruitId);

    @Operation(summary = "내 공고문 리스트 조회", description = "사용자 본인이 작성한 공고문 리스트를 조회합니다.")
    @GetMapping("/my")
    SuccessResponse<Page<MyRecruitResDto>> getMyRecruits(
            @CurrentEmail String email,
            @PageableDefault Pageable pageable);

    @Operation(summary = "특정 공고문 수정", description = "사용자 본인이 소유한 공고문에 대해 수정합니다.")
    @PatchMapping("/{recruitId}")
    SuccessResponse updateRecruit(
            @CurrentEmail String email,
            @PathVariable(name = "recruitId") Long recruitId,
            @Valid @RequestBody RecruitReqDto recruitReqDto);

    @Operation(summary = "특정 공고문 삭제", description = "사용자 본인이 소유한 공고문에 대해 삭제합니다.")
    @DeleteMapping("/{recruitId}")
    SuccessResponse deleteRecruit(
            @CurrentEmail String email,
            @PathVariable(name = "recruitId") Long recruitId);

    @Operation(summary = "인기있는 공고문 조회", description = "사용자 조회 수 별로 인기있는 공고문을 조회합니다.")
    @GetMapping("/popular")
    SuccessResponse<List<RecruitPopularityResDto>> getPopularRecruits(
            @PageableDefault Pageable pageable);

    @Operation(summary = "공고문 마감하기", description = "마감기한 전에 매칭된 공고문에 대해 소유자가 마감합니다.")
    @PatchMapping("/closure/{recruitId}")
    SuccessResponse closeRecruit(
            @PathVariable(name = "recruitId") Long recruitId,
            @RequestBody MemberIdReqDto reqDto);
}
