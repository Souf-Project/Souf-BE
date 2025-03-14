package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.repository.FeedReposiotry;
import com.souf.soufwebsite.domain.user.entity.User;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private FeedReposiotry feedReposiotry;

    private User getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    @Override
    public void createFeed(FeedReqDto feedReqDto) {
        User user = getCurrentUser();
        Feed feed = Feed.of(feedReqDto, user);
        feedReposiotry.save(feed);
    }

    @Override
    public List<FeedResDto> getFeeds() {
        List<Feed> feeds = feedReposiotry.findAllByOrderByIdDesc();

        return feeds.stream()
                .map(feed -> FeedResDto.from(feed, feed.getUser().getNickname()))
                .toList();
    }

    @Override
    public FeedResDto getFeedById(Long feedId) {
        Feed feed = feedReposiotry.findById(feedId).orElseThrow(() -> new IllegalArgumentException("Feed not found"));

        return FeedResDto.from(feed, feed.getUser().getNickname());
    }

    @Override
    public void updateFeed(Long feedId, FeedReqDto feedReqDto) {
        User user = getCurrentUser();
        Feed feed = feedReposiotry.findById(feedId).orElseThrow(() -> new IllegalArgumentException("Feed not found"));

        feed.updateFeed(feedReqDto);
    }

    @Override
    public void deleteFeed(Long feedId) {
        User user = getCurrentUser();

        Feed feed = feedReposiotry.findById(feedId).orElseThrow(() -> new IllegalArgumentException("Feed not found"));

        feedReposiotry.delete(feed);
    }
}
