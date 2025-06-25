package com.souf.soufwebsite.domain.feed.dto;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record FeedDetailResDto(

        Long memberId,
        String nickname,
        String profileUrl,
        Long feedId,
        String topic,
        String content,
        Long view,
        List<MediaResDto> mediaResDtos,
        LocalDateTime lastModifiedTime
) {
    public static FeedDetailResDto from(Member member, String profileUrl, Feed feed, Long feedViewCount, List<Media> mediaList) {
        return new FeedDetailResDto(
                member.getId(),
                member.getNickname(),
                profileUrl,
                feed.getId(),
                feed.getTopic(),
            feed.getContent(),
            feed.getViewCount() + feedViewCount,
            convertToMediaResDto(mediaList),
            feed.getLastModifiedTime());
    }

    private static List<MediaResDto> convertToMediaResDto(List<Media> mediaList){
        return mediaList.stream().map(
                MediaResDto::fromFeedDetail
        ).collect(Collectors.toList());
    }
}
