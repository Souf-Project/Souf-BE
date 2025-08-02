package com.souf.soufwebsite.global.common.viewCount.controller;

import com.souf.soufwebsite.global.common.viewCount.dto.ViewCountResDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "view", description = "메인 페이지 사용자 정보 조회")
public interface ViewApiSpecification {

    @Operation(summary = "방문자 수 조회", description = "스프에 방문한 사람 수, 학생 회원 수, 공고문 수를 반환합니다.")
    @GetMapping("/main")
    SuccessResponse<ViewCountResDto> getViewCountFromMain();
}
