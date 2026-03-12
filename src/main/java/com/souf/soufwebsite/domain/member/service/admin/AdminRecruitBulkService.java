package com.souf.soufwebsite.domain.member.service.admin;

import com.souf.soufwebsite.domain.file.service.MediaCleanupPublisher;
import com.souf.soufwebsite.domain.member.dto.admin.reqDto.BulkDeleteReqDto;
import com.souf.soufwebsite.domain.member.dto.admin.reqDto.BulkRestoreReqDto;
import com.souf.soufwebsite.domain.member.dto.admin.resDto.BulkResultResDto;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.event.RecruitChangedEvent;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminRecruitBulkService {

    private final RecruitRepository recruitRepository;
    private final RedisUtil redisUtil;
    private final MediaCleanupPublisher mediaCleanupPublisher;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public BulkResultResDto bulkSoftDelete(BulkDeleteReqDto reqDto) {
        List<Long> ids = normalizeIds(reqDto.ids());
        if (ids.isEmpty()) return emptyResDto();

        List<Long> existing = recruitRepository.findExistingIdsIncludingDeleted(ids);
        Set<Long> existingSet = new HashSet<>(existing);

        List<Long> notFound = ids.stream().filter(id -> !existingSet.contains(id)).toList();
        List<Long> alreadyDeleted = recruitRepository.findDeletedIds(existing);
        Set<Long> alreadyDeletedSet = new HashSet<>(alreadyDeleted);

        List<Long> targets = existing.stream().filter(id -> !alreadyDeletedSet.contains(id)).toList();
        if (targets.isEmpty()) return new BulkResultResDto(ids.size(), 0, notFound, alreadyDeleted, List.of());

        List<Recruit> recruits = recruitRepository.findAllByIdsIncludingDeleted(targets);

        List<Long> successIds = new ArrayList<>();
        List<BulkResultResDto.FailedItem> failed = new ArrayList<>();

        for (Recruit recruit : recruits) {
            try {
                Long recruitId = recruit.getId();

                redisUtil.deleteKey(getRecruitViewKey(recruitId));

                recruit.softDeleteByAdmin(reqDto.reason());

                mediaCleanupPublisher.publish(PostType.RECRUIT, recruitId);
                mediaCleanupPublisher.publish(PostType.LOGO, recruitId);

                successIds.add(recruitId);
            } catch (Exception e) {
                failed.add(new BulkResultResDto.FailedItem(recruit.getId(), "UNKNOWN"));
            }
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                for (Long id : successIds) {
                    publisher.publishEvent(
                            new RecruitChangedEvent(id, RecruitChangedEvent.ChangeType.DELETED, false, null)
                    );
                }
            }
        });

        return new BulkResultResDto(ids.size(), successIds.size(), notFound, alreadyDeleted, failed);
    }

    @Transactional
    public BulkResultResDto bulkRestore(BulkRestoreReqDto reqDto) {
        List<Long> ids = normalizeIds(reqDto.ids());
        if (ids.isEmpty()) return emptyResDto();

        List<Long> existing = recruitRepository.findExistingIdsIncludingDeleted(ids);
        Set<Long> existingSet = new HashSet<>(existing);

        List<Long> notFound = ids.stream().filter(id -> !existingSet.contains(id)).toList();
        List<Long> alreadyRestored = recruitRepository.findNotDeletedIds(existing);
        Set<Long> alreadyRestoredSet = new HashSet<>(alreadyRestored);

        List<Long> targets = existing.stream().filter(id -> !alreadyRestoredSet.contains(id)).toList();
        if (targets.isEmpty()) return new BulkResultResDto(ids.size(), 0, notFound, alreadyRestored, List.of());

        List<Recruit> recruits = recruitRepository.findAllByIdsIncludingDeleted(targets);

        List<Long> successIds = new ArrayList<>();
        List<BulkResultResDto.FailedItem> failed = new ArrayList<>();

        for (Recruit recruit : recruits) {
            try {
                recruit.restoreByAdmin();
                successIds.add(recruit.getId());
            } catch (Exception e) {
                failed.add(new BulkResultResDto.FailedItem(recruit.getId(), "UNKNOWN"));
            }
        }

        return new BulkResultResDto(ids.size(), successIds.size(), notFound, alreadyRestored, failed);
    }

    private String getRecruitViewKey(Long recruitId) {
        return "recruit:view:" + recruitId;
    }

    private static List<Long> normalizeIds(List<Long> ids) {
        if (ids == null) return List.of();
        return ids.stream().filter(Objects::nonNull).distinct().toList();
    }

    private static BulkResultResDto emptyResDto() {
        return new BulkResultResDto(0, 0, List.of(), List.of(), List.of());
    }
}