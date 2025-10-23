package com.souf.soufwebsite.domain.inquiry.dto;

import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.inquiry.entity.Inquiry;
import com.souf.soufwebsite.domain.member.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

public record InquiryDetailedResDto(

        Long inquiryId,
        Long memberId,
        List<MediaResDto> mediaResDtoList
) {
    public static InquiryDetailedResDto of(Inquiry inquiry, Member member, List<Media> mediaList) {
        return new InquiryDetailedResDto(
                inquiry.getId(),
                member.getId(),
                convertToMediaResDto(mediaList)
        );
    }

    private static List<MediaResDto> convertToMediaResDto(List<Media> mediaList){
        return mediaList.stream().map(
                MediaResDto::fromMedia
        ).collect(Collectors.toList());
    }
}
