package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedCreateReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;
import com.souf.soufwebsite.domain.feed.dto.FeedUpdateReqDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.exception.NotFoundFeedException;
import com.souf.soufwebsite.domain.feed.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.file.dto.FileDto;
import com.souf.soufwebsite.domain.file.entity.File;
import com.souf.soufwebsite.domain.file.entity.FileType;
import com.souf.soufwebsite.domain.file.repository.FileRepository;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.user.entity.User;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;

    private User getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    @Override
    public void createFeed(FeedCreateReqDto reqDto) {
        User user = getCurrentUser();
        List<File> fileEntities = new ArrayList<>();

        for (MultipartFile multipartFile : reqDto.files()) {
            try {
                // S3 업로드 + DB 저장 → File 엔티티 반환
                File file = fileService.uploadFile(multipartFile);
                fileEntities.add(file);
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 실패: " + multipartFile.getOriginalFilename(), e);
            }
        }

        Feed feed = Feed.of(reqDto.content(), user, fileEntities);
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
        Feed feed = findIfFeedExist(feedId);

        return FeedResDto.from(feed, feed.getUser().getNickname());
    }

    @Transactional
    @Override
    public void updateFeed(Long feedId, FeedUpdateReqDto reqDto) {
        User user = getCurrentUser();
        Feed feed = findIfFeedExist(feedId);
        verifyIfFeedIsMine(feed, user);

        feed.updateContent(reqDto.content());

        // 파일 삭제
        List<File> filesToRemove = feed.getFiles().stream()
                .filter(file -> !reqDto.keepFileIds().contains(file.getId()))
                .toList();

        for (File file : filesToRemove) {
            fileService.deleteFromS3(file.getFileUrl());
            feed.removeFile(file);
            fileRepository.delete(file);
        }

        for (MultipartFile mf : reqDto.newFiles()) {
            try {
                File file = fileService.uploadFile(mf);
                feed.addFile(file);
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 실패: " + mf.getOriginalFilename(), e);
            }
        }
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
