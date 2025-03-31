package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FeedServiceImplTest {

    @InjectMocks
    private FeedServiceImpl feedService;

    @Mock
    private FeedRepository feedRepository;

    @Test
    void createFeed() {
        // given
        FeedReqDto reqDto = new FeedReqDto("content");

        // when
        Feed feed = Feed.of(reqDto, null);

        // then
        assertThat(feed).isNotNull();
    }

    @Test
    void getFeeds() {
        // given
        List<Feed> feeds = List.of(new Feed(), new Feed());
        when(feedRepository.findAllByOrderByIdDesc()).thenReturn(feeds);

        // when
        List<FeedResDto> result = feedService.getFeeds();

        // then
        assertThat(result).hasSize(2);
    }

}