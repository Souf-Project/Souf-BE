package com.souf.soufwebsite.domain.feed.dto.res;

import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberSummaryDto;

import java.time.LocalDateTime;

public record FeedSimpleResDto(

        Long memberId,
        String nickname,
        String profileImageUrl,
        double temperature,

        Long feedId,
        String topic,
        String content,
        int likedCount,

        Boolean liked,
        MediaResDto mediaResDto,
        LocalDateTime createdTime
) {

    public static FeedSimpleResDto from(FeedSimpleBaseResDto resDto, Boolean liked) {

        MemberSummaryDto writer = resDto.writer();

        return new FeedSimpleResDto(
                writer.id(),
                writer.nickname(),
                resDto.writerProfileUrl(),
                writer.temperature(),
                resDto.feedId(),
                resDto.topic(),
                resDto.content(),
                resDto.likedCount(),
                liked,
                resDto.mediaResDto(),
                resDto.createdAt()
        );
    }
}
