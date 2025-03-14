package com.souf.soufwebsite.domain.recruit.controller;

import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitResDto;
import com.souf.soufwebsite.domain.recruit.service.RecruitService;
import com.souf.soufwebsite.global.common.FirstCategory;
import com.souf.soufwebsite.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruit")
public class RecruitController {

    private final RecruitService recruitService;

    @PostMapping
    public SuccessResponse<?> createRecruit(RecruitReqDto recruitReqDto) {
        recruitService.createRecruit(recruitReqDto);

        return new SuccessResponse<>("Recruit created successfully");
    }

    @GetMapping
    public SuccessResponse<List<RecruitResDto>> getRecruits(@RequestParam(name = "category") FirstCategory category) {
        return new SuccessResponse<>(recruitService.getRecruits(category));
    }

    @GetMapping("/{recruitId}")
    public SuccessResponse<RecruitResDto> getRecruitById(@PathVariable(name = "recruitId") Long recruitId) {
        return new SuccessResponse<>(recruitService.getRecruitById(recruitId));
    }

    @PatchMapping("/{recruitId}")
    public SuccessResponse<?> updateRecruit(@PathVariable(name = "recruitId") Long recruitId, RecruitReqDto recruitReqDto) {
        recruitService.updateRecruit(recruitId, recruitReqDto);

        return new SuccessResponse<>("Recruit updated successfully");
    }

    @DeleteMapping("/{recruitId}")
    public SuccessResponse<?> deleteRecruit(@PathVariable(name = "recruitId") Long recruitId) {
        recruitService.deleteRecruit(recruitId);
        return new SuccessResponse<>("Recruit deleted successfully");
    }
}
