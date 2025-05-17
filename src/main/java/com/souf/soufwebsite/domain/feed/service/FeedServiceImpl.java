package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedCreateResDto;
import com.souf.soufwebsite.domain.feed.dto.FeedReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;
import com.souf.soufwebsite.domain.feed.dto.FeedUpdateReqDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.entity.Tag;
import com.souf.soufwebsite.domain.feed.exception.NotFoundFeedException;
import com.souf.soufwebsite.domain.feed.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.file.dto.FileReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.entity.File;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.global.common.category.CategoryService;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;
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

        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl(reqDto.originalFileNames());

        return new FeedCreateResDto(feed.getId(), presignedUrlResDtos);
    }

    @Override
    public void uploadFeedMedia(FileReqDto fileReqDto) {
        Feed feed = findIfFeedExist(fileReqDto.postId());
        List<File> fileList = fileService.uploadMetadata(fileReqDto);

        for(File f : fileList){
            feed.addFileOnFeed(f);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<FeedResDto> getFeeds() {
        List<Feed> feeds = feedRepository.findAllByOrderByIdDesc();

        return feeds.stream()
                .map(feed -> FeedResDto.from(feed, feed.getMember().getNickname()))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public FeedResDto getFeedById(Long feedId) {
        Feed feed = findIfFeedExist(feedId);

        return FeedResDto.from(feed, feed.getMember().getNickname());
    }

    @Transactional
    @Override
    public void updateFeed(Long feedId, FeedUpdateReqDto reqDto, List<MultipartFile> newFiles) throws IOException {
        Member member = getCurrentUser();
        Feed feed = findIfFeedExist(feedId);
        verifyIfFeedIsMine(feed, member);

        List<File> toRemove = feed.getFiles().stream()
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
}
