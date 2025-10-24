package com.souf.soufwebsite.domain.file.service;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.dto.video.VideoDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.entity.MediaType;
import com.souf.soufwebsite.domain.file.repository.MediaRepository;
import com.souf.soufwebsite.global.common.PostType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MediaRepository mediaRepository;
    private final S3UploaderService s3UploaderService;

    private final Set<String> videoExtensions = Set.of("mp4", "mov", "avi", "mkv", "webm", "flv");

    public VideoDto configVideoUploadInitiation(List<String> originalFilenames, PostType type) {
        List<String> videoFiles = originalFilenames.stream()
                .filter(f -> videoExtensions.contains(extractExtension(f).toLowerCase()))
                .toList();

        if (videoFiles.isEmpty()) {
            return null; // 또는 throw new IllegalArgumentException("No video files");
        }

        String videoString = videoFiles.get(0); // 혹은 여러 개면 join해서
        if (type == PostType.FEED) {
            return s3UploaderService.initiateUpload("feed", videoString);
        } else if(type == PostType.RECRUIT)
            return s3UploaderService.initiateUpload("recruit", videoString);
        else {
            return s3UploaderService.initiateUpload("chat", videoString);
        }
    }

    public List<PresignedUrlResDto> generatePresignedUrl(String path, List<String> fileNames){
        // 비디오 확장자 제외
        // Presigned URL 생성
        return fileNames.stream()
                .filter(f -> !videoExtensions.contains(extractExtension(f).toLowerCase())) // 비디오 확장자 제외
                .map(f -> s3UploaderService.generatePresignedUploadUrl(path, f)) // Presigned URL 생성
                .collect(Collectors.toList());
    }

    public List<PresignedUrlResDto> generatePresignedUrl(String path, PostType postType, Long postId, List<String> candidateNames) {
        if (candidateNames == null || candidateNames.isEmpty()) return List.of();

        // DB에 이미 저장된 fileName 집합
        var existingNames = mediaRepository.findByPostTypeAndPostId(postType, postId).stream()
                .map(Media::getFileName)
                .filter(n -> n != null && !n.isBlank())
                .collect(Collectors.toSet());

        List<String> newOnes = candidateNames.stream()
                .filter(n -> n != null && !n.isBlank())
                .filter(n -> !videoExtensions.contains(extractExtension(n).toLowerCase()))
                .filter(n -> !existingNames.contains(n))
                .toList();

        return newOnes.stream()
                .map(f -> s3UploaderService.generatePresignedUploadUrl(path, f))
                .toList();
    }

    @Transactional
    public List<Media> uploadMetadata(MediaReqDto files, PostType postType, Long postId){
        List<Media> mediaList = new ArrayList<>();
        for(int i=0;i<files.fileUrl().size();i++){
            PostType temp = postType;
            if(!files.filePurpose().isEmpty() && files.filePurpose().get(i).equals("LOGO")){
                temp = PostType.LOGO;
            }
            Media media = Media.of(files.fileUrl().get(i), files.fileName().get(i),
                    MediaType.valueOf(files.fileType().get(i)), temp, postId);
            mediaRepository.save(media);
            mediaList.add(media);
        }

        return mediaList;
    }

    public String getMediaUrl(PostType postType, Long postId){
        List<Media> byPostTypeAndPostId = mediaRepository.findByPostTypeAndPostId(postType, postId);
        return byPostTypeAndPostId.isEmpty() ? "" : byPostTypeAndPostId.get(0).getOriginalUrl();
    }

    public List<Media> getMediaList(PostType postType, Long postId){
        return mediaRepository.findByPostTypeAndPostId(postType, postId);
    }

    public void clearMediaList(PostType postType, Long postId){
        mediaRepository.deleteAllByPostTypeAndPostId(postType, postId);
    }

    public void deleteMedia(Media media) {
        mediaRepository.delete(media);
    }

    private String extractExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
