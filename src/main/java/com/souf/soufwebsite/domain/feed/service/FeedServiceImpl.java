package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.*;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.exception.NotFoundFeedException;
import com.souf.soufwebsite.domain.feed.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.reposiotry.MemberRepository;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;
    private final TagService tagService;

    private Member getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    @Override
    @Transactional
    public FeedResDto createFeed(FeedReqDto reqDto) {
        Member member = getCurrentUser();

        Feed feed = Feed.of(reqDto, member);
        feed = feedRepository.save(feed);
        tagService.createFeedTag(feed, reqDto.tags());

        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl("feed", reqDto.originalFileNames());

        return new FeedResDto(feed.getId(), presignedUrlResDtos);
    }

    @Override
    public void uploadFeedMedia(MediaReqDto mediaReqDto) {
        Feed feed = findIfFeedExist(mediaReqDto.postId());
        List<Media> mediaList = fileService.uploadMetadata(mediaReqDto);

        for(Media f : mediaList){
            feed.addFileOnFeed(f);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<FeedSimpleResDto> getFeeds(Long memberId, Pageable pageable) {
        Member member = findIfMemberExists(memberId);
        return feedRepository.findAllByMemberOrderByIdDesc(member, pageable)
                .map(feed -> FeedSimpleResDto.from(feed, MediaResDto.fromFeedThumbnail(feed)));
    }

    @Transactional(readOnly = true)
    @Override
    public FeedDetailResDto getFeedById(Long memberId, Long feedId) {
        findIfMemberExists(memberId);
        Feed feed = findIfFeedExist(feedId);
        feed.addViewCount();

        return FeedDetailResDto.from(feed);
    }

    @Transactional
    @Override
    public FeedResDto updateFeed(Long feedId, FeedReqDto reqDto) {
        Member member = getCurrentUser();
        Feed feed = findIfFeedExist(feedId);
        verifyIfFeedIsMine(feed, member);

        feed.updateContent(reqDto);
        tagService.createFeedTag(feed, reqDto.tags());
        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl("feed", reqDto.originalFileNames());

        return new FeedResDto(feed.getId(), presignedUrlResDtos);
    }

    @Override
    public void deleteFeed(Long feedId) {
        Member member = getCurrentUser();
        Feed feed = findIfFeedExist(feedId);
        verifyIfFeedIsMine(feed, member);

        feedRepository.delete(feed);
    }

    private void verifyIfFeedIsMine(Feed feed, Member member) {
        log.info("currentMember: {}, feedMember: {}", member, feed.getMember());
        if(!feed.getMember().getId().equals(member.getId())){
            throw new NotValidAuthenticationException();
        }
    }

    private Feed findIfFeedExist(Long id) {
        return feedRepository.findById(id).orElseThrow(NotFoundFeedException::new);
    }

    private Member findIfMemberExists(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(NotFoundFeedException::new);
    }
}
