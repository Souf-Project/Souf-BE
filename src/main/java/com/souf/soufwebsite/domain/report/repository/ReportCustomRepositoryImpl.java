package com.souf.soufwebsite.domain.report.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.member.dto.ResDto.AdminReportResDto;
import com.souf.soufwebsite.domain.member.entity.QMember;
import com.souf.soufwebsite.domain.report.entity.QReportReason;
import com.souf.soufwebsite.domain.report.entity.QReportReasonMapping;
import com.souf.soufwebsite.domain.report.entity.ReportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.souf.soufwebsite.domain.report.entity.QReport.report;
import static com.souf.soufwebsite.domain.report.entity.QReportReasonMapping.reportReasonMapping;

@Repository
@RequiredArgsConstructor
public class ReportCustomRepositoryImpl implements ReportCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminReportResDto> getReportListInAdmin(PostType postType, LocalDate startDate, LocalDate endDate, String nickname, Pageable pageable) {
        QMember reportedMember = new QMember("reportedMember");
        QMember reporter       = new QMember("reporter");
        QReportReasonMapping rrm = QReportReasonMapping.reportReasonMapping;
        QReportReason reason = QReportReason.reportReason;

        BooleanBuilder condition = new BooleanBuilder();
        BooleanExpression postTypeCondition = extractedPostType(postType);
        BooleanExpression dateCondition = extractedDate(startDate, endDate);
        BooleanExpression nicknameCondition = extractedNickname(nickname);

        if(postTypeCondition != null){
            condition.and(postTypeCondition);
        }
        if(dateCondition != null){
            condition.and(dateCondition);
        }
        if(nicknameCondition != null){
            condition.and(nicknameCondition);
        }

        List<Long> reportIds = queryFactory
                .select(report.id)
                .from(report)
                .where(condition)
                .orderBy(report.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (reportIds.isEmpty()) {
            long total0 = Optional.ofNullable(
                    queryFactory.select(report.count()).from(report).where(condition).fetchOne()
            ).orElse(0L);
            return new PageImpl<>(Collections.emptyList(), pageable, total0);
        }

        // reason 빼고 Tuple로 가져오기.
        List<Tuple> baseRows = queryFactory
                .select(
                        report.id,
                        report.postId,
                        report.postType,
                        report.postTitle,
                        reportedMember.id,
                        reportedMember.nickname,
                        reporter.id,
                        reporter.nickname,
                        report.createdTime,
                        report.description,
                        report.status
                )
                .from(report)
                .join(report.reportedMember, reportedMember)
                .join(report.reporter, reporter)
                .leftJoin(report.reportReasonMappings, reportReasonMapping)
                .where(report.id.in(reportIds))
                .fetch();

        Map<Long, AdminReportResDto> resDtoMap = new HashMap<>();

        record Base(
                Long id, Long postId, PostType postType, String postTitle,
                Long reportedMemberId, String reportedMemberNickname,
                Long reporterId, String reporterNickname,
                LocalDateTime createdTime, String description, ReportStatus status
        ) {}
        Map<Long, Base> baseMap = new HashMap<>(baseRows.size() * 2);
        for (Tuple t : baseRows) {
            Base b = new Base(
                    t.get(report.id),
                    t.get(report.postId),
                    t.get(report.postType),
                    t.get(report.postTitle),
                    t.get(reportedMember.id),
                    t.get(reportedMember.nickname),
                    t.get(reporter.id),
                    t.get(reporter.nickname),
                    t.get(report.createdTime),
                    t.get(report.description),
                    t.get(report.status)
            );
            baseMap.put(b.id(), b);
        }

        // 사유 ID만 별도 조회 → 자바에서 그룹핑(중복 제거)

        List<Tuple> reasonRows = queryFactory
                .select(rrm.report.id, reason.id)
                .from(rrm)
                .join(rrm.reportReason, reason)
                .where(rrm.report.id.in(reportIds))
                .fetch();

        Map<Long, LinkedHashSet<Long>> reasonMap = new HashMap<>();
        for (Tuple t : reasonRows) {
            Long rid = t.get(rrm.report.id);
            Long reasonId = t.get(reason.id);
            if (rid == null || reasonId == null) continue; // leftJoin인 경우 방어
            reasonMap.computeIfAbsent(rid, k -> new LinkedHashSet<>()).add(reasonId);
        }

        // 원래 페이징 순서대로 DTO 조립 (List<Long>만 담음)
        List<AdminReportResDto> content = reportIds.stream()
                .map(baseMap::get)
                .filter(Objects::nonNull)
                .map(b -> new AdminReportResDto(
                        b.id(), b.postId(), b.postType(), b.postTitle(),
                        b.reportedMemberId(), b.reportedMemberNickname(),
                        b.reporterId(), b.reporterNickname(),
                        b.createdTime(), b.description(), b.status(),
                        reasonMap.getOrDefault(b.id(), new LinkedHashSet<>()).stream().toList() // List<Long>
                ))
                .toList();

        // total count
        long total = Optional.ofNullable(
                queryFactory.select(report.count()).from(report).where(condition).fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression extractedPostType(PostType postType) {
        if(postType == null){
            return null;
        }

        return report.postType.eq(postType);
    }

    private BooleanExpression extractedDate(LocalDate startDate, LocalDate endDate) {
        BooleanExpression predicate = null;

        if (startDate != null) {
            predicate = report.createdTime.goe(startDate.atStartOfDay());
        }
        if (endDate != null) {
            BooleanExpression endPredicate =
                    report.createdTime.loe(endDate.atTime(LocalTime.MAX));
            predicate = predicate == null ? endPredicate : predicate.and(endPredicate);
        }
        return predicate;
    }

    private BooleanExpression extractedNickname(String nickname) {
        if(nickname == null){
            return null;
        }

        BooleanExpression findInReporter = report.reporter.nickname.eq(nickname);
        BooleanExpression findInReported = report.reportedMember.nickname.eq(nickname);

        return findInReported.and(findInReporter);
    }
}
