package com.souf.soufwebsite.domain.recruit.controller;

import com.souf.soufwebsite.domain.file.dto.FileReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitCreateReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitResDto;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.service.RecruitService;
import com.souf.soufwebsite.global.common.FirstCategory;
import com.souf.soufwebsite.global.success.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.souf.soufwebsite.domain.recruit.controller.RecruitSuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruit")
public class RecruitController {

    private final RecruitService recruitService;

    @PostMapping
    public SuccessResponse<RecruitCreateReqDto> createRecruit(@Valid @RequestBody RecruitReqDto recruitReqDto) {
        RecruitCreateReqDto recruitCreateReqDto = recruitService.createRecruit(recruitReqDto);

        return new SuccessResponse<>(recruitCreateReqDto, RECRUIT_CREATE.getMessage());
    }

    @PostMapping("/upload")
    public SuccessResponse uploadMetadata(@Valid @RequestBody FileReqDto fileReqDto){
        recruitService.uploadRecruitMedia(fileReqDto);

        return new SuccessResponse(RECRUIT_FILE_METADATA_CREATE.getMessage());
    }

    @GetMapping
    public SuccessResponse<List<RecruitResDto>> getRecruits(@RequestParam(name = "category") FirstCategory category) {
        return new SuccessResponse<>(
                recruitService.getRecruits(category),
                RECRUIT_GET.getMessage());
    }

    @GetMapping("/{recruitId}")
    public SuccessResponse<RecruitResDto> getRecruitById(@PathVariable(name = "recruitId") Long recruitId) {
        return new SuccessResponse<>(
                recruitService.getRecruitById(recruitId),
                RECRUIT_GET.getMessage());
    }

    @PatchMapping("/{recruitId}")
    public SuccessResponse updateRecruit(@PathVariable(name = "recruitId") Long recruitId, @Valid @RequestBody RecruitReqDto recruitReqDto) {
        recruitService.updateRecruit(recruitId, recruitReqDto);

        return new SuccessResponse(RECRUIT_UPDATE.getMessage());
    }

    @DeleteMapping("/{recruitId}")
    public SuccessResponse deleteRecruit(@PathVariable(name = "recruitId") Long recruitId) {
        recruitService.deleteRecruit(recruitId);
        return new SuccessResponse(RECRUIT_DELETE.getMessage());
    }
}
