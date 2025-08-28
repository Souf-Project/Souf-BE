package com.souf.soufwebsite.domain.socialAccount.dto;

import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SocialCompleteSignupReqDto(
        @NotBlank @Size(min = 2, max = 20) String nickname,

        @Size(max = 3, message = "카테고리는 최대 3개까지 선택 가능합니다.")
        List<@Valid @NotNull CategoryDto> categoryDtos,

        @NotNull
        @Schema(description = "개인정보 동의서 확인 여부", example = "true여야 합니다.")
        Boolean isPersonalInfoAgreed,

        @Schema(description = "마케팅 동의 확인 여부", example = "true or false")
        Boolean isMarketingAgreed,

        @NotBlank String registrationToken
) {}