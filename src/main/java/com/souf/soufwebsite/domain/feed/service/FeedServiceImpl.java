package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.exception.NotFoundFeedException;
import com.souf.soufwebsite.domain.feed.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.user.entity.User;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;

    private User getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    @Override
    public void createFeed(FeedReqDto reqDto) {
        User user = getCurrentUser();
        Feed feed = Feed.of(reqDto, user);
        feedRepository.save(feed);
    }

    @Transactional(readOnly = true)
    @Override
    public List<FeedResDto> getFeeds() {
        List<Feed> feeds = feedRepository.findAllByOrderByIdDesc();

        return feeds.stream()
                .map(feed -> FeedResDto.from(feed, feed.getUser().getNickname()))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public FeedResDto getFeedById(Long feedId) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new IllegalArgumentException("Feed not found"));

        return FeedResDto.from(feed, feed.getUser().getNickname());
    }

    @Transactional
    @Override
    public void updateFeed(Long feedId, FeedReqDto reqDto) {
        User user = getCurrentUser();
        Feed feed = findIfFeedExist(feedId);
        verifyIfFeedIsMine(feed, user);

        feed.updateFeed(reqDto);
    }

    @Override
    public void deleteFeed(Long feedId) {
        User user = getCurrentUser();
        Feed feed = findIfFeedExist(feedId);
        verifyIfFeedIsMine(feed, user);

        feedRepository.delete(feed);
    }

    private void verifyIfFeedIsMine(Feed feed, User user) {
        if(!feed.getUser().equals(user)){
            throw new NotValidAuthenticationException();
        }
    }

    private Feed findIfFeedExist(Long id) {
        return feedRepository.findById(id).orElseThrow(NotFoundFeedException::new);
    }
}
