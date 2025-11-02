package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.recruit.event.RecruitChangedEvent;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RecruitPopularListener {

    private final RecruitRepository recruitRepository;
    private final RecruitScheduledService recruitScheduledService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChanged(RecruitChangedEvent e) {
        switch (e.type()) {

            case CREATED -> { // (1) 새 공고 생성
                if (recruitRepository.isInTop5(e.id()))
                    recruitScheduledService.rebuildPopularRecruits();
            }

            case CONTENT_UPDATED -> { // (2) TOP5 내 내용만 수정
                if (recruitScheduledService.cacheContains(e.id()))
                    recruitScheduledService.patchOne(e.id());
            }

            case STATUS_OR_DEADLINE_UPDATED -> { // (3) 순위/자격 변동
                boolean inTop5Now = recruitRepository.isInTop5(e.id());
                boolean wasInCache = recruitScheduledService.cacheContains(e.id());
                if (inTop5Now || wasInCache) // 들어오거나/나가거나 모두 리빌드
                    recruitScheduledService.rebuildPopularRecruits();
            }

            case DELETED -> { // (4) TOP5 내 삭제
                if (recruitScheduledService.cacheContains(e.id()))
                    recruitScheduledService.rebuildPopularRecruits();
            }
        }
    }
}