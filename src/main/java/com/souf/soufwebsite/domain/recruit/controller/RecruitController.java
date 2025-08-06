package com.souf.soufwebsite.domain.recruit.controller;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.MemberIdReqDto;
import com.souf.soufwebsite.domain.recruit.dto.*;
import com.souf.soufwebsite.domain.recruit.service.RecruitService;
import com.souf.soufwebsite.global.redis.util.RedisUtil;
import com.souf.soufwebsite.global.success.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.souf.soufwebsite.domain.recruit.controller.RecruitSuccessMessage.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruit")
public class RecruitController implements RecruitApiSpecification{

    private final RecruitService recruitService;
    private final RedisUtil redisUtil;

    @PostMapping
    public SuccessResponse<RecruitCreateResDto> createRecruit(@Valid @RequestBody RecruitReqDto recruitReqDto) {
        RecruitCreateResDto recruitCreateResDto = recruitService.createRecruit(recruitReqDto);

        return new SuccessResponse<>(recruitCreateResDto, RECRUIT_CREATE.getMessage());
    }

    @PostMapping("/upload")
    public SuccessResponse uploadMetadata(@Valid @RequestBody MediaReqDto mediaReqDto){
        recruitService.uploadRecruitMedia(mediaReqDto);

        return new SuccessResponse(RECRUIT_FILE_METADATA_CREATE.getMessage());
    }

    @GetMapping
    public SuccessResponse<Page<RecruitSimpleResDto>> getRecruits(
            @RequestParam(required = false, name = "firstCategory") Long first,
            @RequestParam(required = false, name = "secondCategory") Long second,
            @RequestParam(required = false, name = "thirdCategory") Long third,
            @ModelAttribute RecruitSearchReqDto recruitSearchReqDto,
            @PageableDefault(size = 12) Pageable pageable) {
        return new SuccessResponse<>(
                recruitService.getRecruits(first, second, third, recruitSearchReqDto, pageable),
                RECRUIT_GET.getMessage());
    }

    @GetMapping("/{recruitId}")
    public SuccessResponse<RecruitResDto> getRecruitById(@PathVariable(name = "recruitId") Long recruitId) {
        return new SuccessResponse<>(
                recruitService.getRecruitById(recruitId),
                RECRUIT_GET.getMessage());
    }

    @GetMapping("/my")
    public SuccessResponse<Page<MyRecruitResDto>> getMyRecruits(@PageableDefault(size = 10) Pageable pageable) {
        // 페이징 10으로 설정, 추후 검토 후 수정 필요
        return new SuccessResponse<>(recruitService.getMyRecruits(pageable), RECRUIT_GET.getMessage());
    }

    @PatchMapping("/{recruitId}")
    public SuccessResponse updateRecruit(@PathVariable(name = "recruitId") Long recruitId, @Valid @RequestBody RecruitReqDto recruitReqDto) {
        RecruitCreateResDto recruitUpdateDto = recruitService.updateRecruit(recruitId, recruitReqDto);

        return new SuccessResponse<>(recruitUpdateDto, RECRUIT_UPDATE.getMessage());
    }

    @DeleteMapping("/{recruitId}")
    public SuccessResponse deleteRecruit(@PathVariable(name = "recruitId") Long recruitId) {
        recruitService.deleteRecruit(recruitId);
        return new SuccessResponse(RECRUIT_DELETE.getMessage());
    }

    @GetMapping("/popular")
    public SuccessResponse<List<RecruitPopularityResDto>> getPopularRecruits(
            @PageableDefault(size = 10) Pageable pageable
    ) {

        log.info("공고문 캐싱 조회");
        redisUtil.increaseCount("view:main:totalCount");

        return new SuccessResponse<>(
                recruitService.getPopularRecruits(pageable),
                RECRUIT_GET_POPULATION.getMessage());
    }

    @PatchMapping("/closure/{recruitId}")
    public SuccessResponse closeRecruit(
            @PathVariable(name = "recruitId") Long recruitId,
            @RequestBody MemberIdReqDto reqDto) {

        recruitService.updateRecruitable(recruitId, reqDto);
        return SuccessResponse.ok(RECRUIT_UPDATE.getMessage());
    }
}
