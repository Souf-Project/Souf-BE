package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.res.PopularFeedResDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.global.common.PostType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedConverter {

    private final FileService fileService;

    public PopularFeedResDto getFeedSimpleResDto(Feed feed) {
        List<Media> mediaList = fileService.getMediaList(PostType.FEED, feed.getId());
        if(mediaList.isEmpty())
            return PopularFeedResDto.from(feed, null);
        return PopularFeedResDto.from(feed, MediaResDto.fromMedia(mediaList.get(0)));
    }
}
