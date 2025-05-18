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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
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
    public FeedCreateResDto createFeed(FeedReqDto reqDto) {
        Member member = getCurrentUser();

        Feed feed = Feed.of(reqDto, member);
        feed = feedRepository.save(feed);
        tagService.createFeedTag(feed, reqDto.tags());

        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl("feed", reqDto.originalFileNames());

        return new FeedCreateResDto(feed.getId(), presignedUrlResDtos);
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
    public FeedResDto getFeedById(Long memberId, Long feedId) {
        Member member = findIfMemberExists(memberId);
        Feed feed = findIfFeedExist(feedId);
        feed.addViewCount();

        return FeedResDto.from(feed);
    }

    @Transactional
    @Override
    public void updateFeed(Long feedId, FeedUpdateReqDto reqDto, List<MultipartFile> newFiles) throws IOException {
        Member member = getCurrentUser();
        Feed feed = findIfFeedExist(feedId);
        verifyIfFeedIsMine(feed, member);

        List<Media> toRemove = feed.getMedia().stream()
                .filter(file -> !reqDto.keepFileIds().contains(file.getId()))
                .toList();





    }

    @Override
    public void deleteFeed(Long feedId) {
        Member member = getCurrentUser();
        Feed feed = findIfFeedExist(feedId);
        verifyIfFeedIsMine(feed, member);

        feedRepository.delete(feed);
    }

    private void verifyIfFeedIsMine(Feed feed, Member member) {
        if(!feed.getMember().equals(member)){
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
