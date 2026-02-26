package com.souf.soufwebsite.domain.member.service.admin;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.event.FeedChangedEvent;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.file.service.MediaCleanupPublisher;
import com.souf.soufwebsite.domain.member.dto.reqDto.admin.BulkDeleteReqDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.admin.BulkRestoreReqDto;
import com.souf.soufwebsite.domain.member.dto.resDto.admin.BulkResultResDto;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.common.viewCount.service.ViewCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminFeedBulkService {

    private final FeedRepository feedRepository;
    private final ViewCountService viewCountService;
    private final MediaCleanupPublisher mediaCleanupPublisher;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public BulkResultResDto bulkSoftDelete(BulkDeleteReqDto reqDto) {
        List<Long> ids = normalizeIds(reqDto.ids());
        if (ids.isEmpty()) return emptyResDto();

        List<Long> existing = feedRepository.findExistingIdsIncludingDeleted(ids);
        Set<Long> existingSet = new HashSet<>(existing);

        List<Long> notFound = ids.stream().filter(id -> !existingSet.contains(id)).toList();
        List<Long> alreadyDeleted = feedRepository.findDeletedIds(existing);
        Set<Long> alreadyDeletedSet = new HashSet<>(alreadyDeleted);

        List<Long> targets = existing.stream().filter(id -> !alreadyDeletedSet.contains(id)).toList();
        if (targets.isEmpty()) return new BulkResultResDto(ids.size(), 0, notFound, alreadyDeleted, List.of());

        List<Feed> feeds = feedRepository.findAllByIdsIncludingDeleted(targets);

        List<Long> successIds = new ArrayList<>();
        List<BulkResultResDto.FailedItem> failed = new ArrayList<>();

        for (Feed feed : feeds) {
            try {
                Long feedId = feed.getId();

                viewCountService.deleteViewCountFromRedis(PostType.FEED, feedId);

                feed.softDeleteByAdmin(reqDto.reason());

                mediaCleanupPublisher.publish(PostType.FEED, feedId);

                successIds.add(feedId);
            } catch (Exception e) {
                failed.add(new BulkResultResDto.FailedItem(feed.getId(), "UNKNOWN"));
            }
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                for (Long id : successIds) {
                    publisher.publishEvent(new FeedChangedEvent(id, FeedChangedEvent.ChangeType.DELETED));
                }
            }
        });

        return new BulkResultResDto(ids.size(), successIds.size(), notFound, alreadyDeleted, failed);
    }

    @Transactional
    public BulkResultResDto bulkRestore(BulkRestoreReqDto reqDto) {
        List<Long> ids = normalizeIds(reqDto.ids());
        if (ids.isEmpty()) return emptyResDto();

        List<Long> existing = feedRepository.findExistingIdsIncludingDeleted(ids);
        Set<Long> existingSet = new HashSet<>(existing);

        List<Long> notFound = ids.stream().filter(id -> !existingSet.contains(id)).toList();
        List<Long> alreadyRestored = feedRepository.findNotDeletedIds(existing);
        Set<Long> alreadyRestoredSet = new HashSet<>(alreadyRestored);

        List<Long> targets = existing.stream().filter(id -> !alreadyRestoredSet.contains(id)).toList();
        if (targets.isEmpty()) return new BulkResultResDto(ids.size(), 0, notFound, alreadyRestored, List.of());

        List<Feed> feeds = feedRepository.findAllByIdsIncludingDeleted(targets);

        List<Long> successIds = new ArrayList<>();
        List<BulkResultResDto.FailedItem> failed = new ArrayList<>();

        for (Feed feed : feeds) {
            try {
                feed.restoreByAdmin();
                successIds.add(feed.getId());
            } catch (Exception e) {
                failed.add(new BulkResultResDto.FailedItem(feed.getId(), "UNKNOWN"));
            }
        }

        return new BulkResultResDto(ids.size(), successIds.size(), notFound, alreadyRestored, failed);
    }

    private static List<Long> normalizeIds(List<Long> ids) {
        if (ids == null) return List.of();
        return ids.stream().filter(Objects::nonNull).distinct().toList();
    }
    private static BulkResultResDto emptyResDto() {
        return new BulkResultResDto(0, 0, List.of(), List.of(), List.of());
    }
}