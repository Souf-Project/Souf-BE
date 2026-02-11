package com.souf.soufwebsite.domain.feed.dto.res;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.member.entity.Member;

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

    public static FeedSimpleResDto from(Member member, String profileImageUrl, Feed feed, Boolean liked, Media feedMedia) {

        return new FeedSimpleResDto(
                member.getId(),
                member.getNickname(),
                profileImageUrl,
                member.getTemperature(),
                feed.getId(),
                feed.getTopic(),
                feed.getContent(),
                feed.getLikedCount(),
                liked,
                convertToMediaResDto(feedMedia),
                feed.getCreatedTime()
        );
    }

    private static MediaResDto convertToMediaResDto(Media media){

        if(media == null){
            return null;
        }

        return MediaResDto.fromFeedList(media);
    }
}
