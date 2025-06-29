package com.souf.soufwebsite.domain.file.service;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.entity.MediaType;
import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.file.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MediaRepository mediaRepository;
    private final S3UploaderService s3UploaderService;

    public List<PresignedUrlResDto> generatePresignedUrl(String path, List<String> fileNames){
        List<PresignedUrlResDto> presignedUrlList = fileNames.stream().map(f ->
                        s3UploaderService.generatePresignedUploadUrl(path, f))
                .collect(Collectors.toList());
        return presignedUrlList;
    }

    public List<Media> uploadMetadata(MediaReqDto files, PostType postType, Long postId){
        List<Media> mediaList = new ArrayList<>();
        for(int i=0;i<files.fileUrl().size();i++){
            Media media = Media.of(files.fileUrl().get(i), files.fileName().get(i),
                    MediaType.valueOf(files.fileType().get(i)), postType, postId);
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
}
