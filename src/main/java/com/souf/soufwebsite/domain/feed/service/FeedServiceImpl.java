package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedCreateReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;
import com.souf.soufwebsite.domain.feed.dto.FeedUpdateReqDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.exception.NotFoundFeedException;
import com.souf.soufwebsite.domain.feed.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.file.entity.File;
import com.souf.soufwebsite.domain.file.repository.FileRepository;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.entity.Member;
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
    private final FileRepository fileRepository;

    private Member getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    @Override
    public void createFeed(FeedCreateReqDto reqDto, List<MultipartFile> files) {
        Member member = getCurrentUser();

        List<File> fileEntities = files.stream()
                .map(file -> {
                    try {
                        return fileService.uploadFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException("파일 업로드 실패: " + file.getOriginalFilename(), e);
                    }
                })
                .toList();

        Feed feed = Feed.of(reqDto.content(), member, fileEntities);
        feedRepository.save(feed);
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

        for (File file : toRemove) {
            feed.removeFile(file);
            fileRepository.delete(file); // 실제 DB에서도 삭제
        }

        // 2) 새로운 파일 추가
        if (newFiles != null) {
            for (MultipartFile multipartFile : newFiles) {
                File file = fileService.uploadFile(multipartFile);
                feed.addFile(file);
            }
        }

        // 3) 내용 업데이트
        feed.updateContent(reqDto.content());

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
