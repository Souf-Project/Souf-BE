package com.souf.soufwebsite.domain.file.event;

import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.global.common.PostType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MediaCleanupHelper {

    private final FileService fileService;

    public MediaCleanupHelper(FileService fileService) {
        this.fileService = fileService;
    }

    public List<String> purgeRemovedMedias(PostType postType, Long postId, List<String> keepUrls) {
        List<Media> mediaList = fileService.getMediaList(postType, postId);
        List<String> safeKeep = (keepUrls == null) ? List.of() : keepUrls;
        List<String> removedUrls = new java.util.ArrayList<>();

        for (Media media : mediaList) {
            String original = media.getOriginalUrl();
            boolean shouldRemove = (original == null) || !safeKeep.contains(original);
            if (shouldRemove) {
                if (original != null) removedUrls.add(original);
                String thumb = media.getThumbnailUrl();
                if (thumb != null) removedUrls.add(thumb);
                fileService.deleteMedia(media);
            }
        }
        return removedUrls;
    }
}