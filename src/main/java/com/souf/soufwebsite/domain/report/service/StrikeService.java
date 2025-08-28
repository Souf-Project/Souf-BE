package com.souf.soufwebsite.domain.report.service;

import com.souf.soufwebsite.domain.notification.service.NotificationService;
import com.souf.soufwebsite.domain.report.entity.BanType;
import com.souf.soufwebsite.domain.report.entity.Sanction;
import com.souf.soufwebsite.domain.report.entity.SanctionType;
import com.souf.soufwebsite.domain.report.exception.DuplicateSanctionException;
import com.souf.soufwebsite.domain.report.repository.SanctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StrikeService {

    private final SanctionRepository sanctionRepository;
    private final BanService banService;
    private final NotificationService notificationService;

    public record BanDecision(BanType type, Duration duration) {
        public static BanDecision perm() { return new BanDecision(BanType.PERM, null); }
    }

    @Transactional
    public void addStrike(Long memberId, Long reportId){
        // 제재 리스트에 중복되어 들어가있지는 않는지 확인
        if (sanctionRepository.existsBySanctionTypeAndReportId(SanctionType.STRIKE, reportId)){
            throw new DuplicateSanctionException();
        }

        // 중복되지 않은 새로운 제재면 생성후 저장
        sanctionRepository.save(Sanction.strike(memberId, reportId));
        int strikes = sanctionRepository.countStrikes(memberId);

        decide(strikes).ifPresent(
                banDecision -> {
                    if (banDecision.type == BanType.PERM){
                        banService.issuePermBan(memberId, "3 strikes");
                        sanctionRepository.save(Sanction.permBan(memberId, "3 strikes"));
                    } else {
                        banService.issueTempBan(memberId, banDecision.duration, "strikes = " + strikes);
                        sanctionRepository.save(Sanction.tempBan(memberId, banDecision.duration,"strikes = " + strikes));
                    }
                }
        );
    }

    private static Optional<BanDecision> decide(int strikes) {
        if (strikes <= 0) return Optional.empty();
        if (strikes == 1) return Optional.of(new BanDecision(BanType.TEMP, Duration.ofDays(3)));
        if (strikes == 2) return Optional.of(new BanDecision(BanType.TEMP, Duration.ofDays(7)));
        return Optional.of(BanDecision.perm());
    }
}
