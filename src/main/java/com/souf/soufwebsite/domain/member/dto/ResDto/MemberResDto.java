package com.souf.soufwebsite.domain.member.dto.ResDto;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberCategoryMapping;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record MemberResDto(

        @Schema(description = "회원 아이디", example = "1(Long 타입)")
        Long id,

        @Schema(description = "이메일 주소", example = "user@example.com")
        String email,


        @Schema(description = "실명", example = "김철수")
        String username,

        @Schema(description = "닉네임", example = "김개똥")
        String nickname,

        @Schema(description = "자기소개", example = "안녕하세요, 디자인 전공자입니다.")
        String intro,

        @Schema(description = "개인 URL", example = "https://github.com/username")
        String personalUrl,

        @Schema(description = "회원 권한", example = "MEMBER")
        RoleType role,

        @Schema(description = "회원 프로필 사진", example = "sakdjffasljk.png")
        String profileImageUrl,

        @Schema(description = "회원이 설정한 카테고리")
        List<CategoryDto> categoryDtoList

) {
    public static MemberResDto from(Member member, List<MemberCategoryMapping> categories, String profileImageUrl) {
        return new MemberResDto(
                member.getId(),
                member.getEmail(),
                member.getUsername(),
                member.getNickname(),
                member.getIntro(),
                member.getPersonalUrl(),
                member.getRole(),
                profileImageUrl,
                convertToCategoryDto(categories)
        );
    }

    private static List<CategoryDto> convertToCategoryDto(List<MemberCategoryMapping> mappings){
        if(mappings == null) return new ArrayList<>();
        return mappings.stream().map(
                m -> new CategoryDto(m.getFirstCategory().getId(), m.getSecondCategory().getId(), m.getThirdCategory().getId())
        ).collect(Collectors.toList());
    }
}
