package com.souf.soufwebsite.domain.member.dto.resDto.info;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberCategoryMapping;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "회원 정보 dto")
public record MemberInfoResDto<T extends MemberInfo>(
        Long id,
        String email,
        String username,
        String nickname,
        String phoneNumber,
        String intro,
        String personalUrl,
        RoleType role,
        String profileImageUrl,
        Boolean marketingAgreement,
        List<CategoryDto> categoryDtoList,
        @Schema(description = "역할별 상세 정보", oneOf = {
                StudentInfo.class,
                CompanyInfo.class
        })
        T detail
) {
    public static <T extends MemberInfo> MemberInfoResDto<T> from(Member m, List<MemberCategoryMapping> categories, String profileImageUrl, T detail) {
        return new MemberInfoResDto<>(
                m.getId(),
                m.getEmail(),
                m.getUsername(),
                m.getNickname(),
                m.getPhoneNumber(),
                m.getIntro(),
                m.getPersonalUrl(),
                m.getRole(),
                profileImageUrl,
                m.isMarketingAgreement(),
                convertToCategoryDto(categories),
                detail
        );
    }

    private static List<CategoryDto> convertToCategoryDto(List<MemberCategoryMapping> mappings){
        if(mappings == null) return new ArrayList<>();
        return mappings.stream().map(
                m -> new CategoryDto(
                        m.getFirstCategory().getId(),
                        m.getSecondCategory() != null ? m.getSecondCategory().getId() : null,
                        m.getThirdCategory() != null ? m.getThirdCategory().getId() : null
                )).collect(Collectors.toList());
    }
}