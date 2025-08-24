package com.souf.soufwebsite.domain.report.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.member.dto.ResDto.AdminReportResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.Projections.list;
import static com.souf.soufwebsite.domain.member.entity.QMember.member;
import static com.souf.soufwebsite.domain.report.entity.QReport.report;
import static com.souf.soufwebsite.domain.report.entity.QReportReason.reportReason;
import static com.souf.soufwebsite.domain.report.entity.QReportReasonMapping.reportReasonMapping;

@Repository
@RequiredArgsConstructor
public class ReportCustomRepositoryImpl implements ReportCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminReportResDto> getReportListInAdmin(PostType postType, LocalDate startDate, LocalDate endDate, String nickname, Pageable pageable) {

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

        if (reportIds.isEmpty()){
           return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<AdminReportResDto> resDtoList = queryFactory
                .from(report)
                .join(report.reportedMember, member)
                .join(report.reporter, member)
                .leftJoin(report.reportReasonMappings, reportReasonMapping)
                .leftJoin(reportReasonMapping.reasonReason, reportReason)
                .where(report.id.in(reportIds))
                .transform(
                        groupBy(report.id).list(
                                Projections.constructor(
                                        AdminReportResDto.class,
                                        report.id,
                                        report.postId,
                                        report.postType,
                                        report.postTitle,
                                        report.reportedMember.id,
                                        report.reportedMember.nickname,
                                        report.reporter.id,
                                        report.reporter.nickname,
                                        report.createdTime,
                                        report.description,
                                        report.status,
                                        // 사유 리스트
                                        list(reportReason.id)
                                )
                        )
                );

        Map<Long, AdminReportResDto> byId = resDtoList.stream()
                .collect(Collectors.toMap(AdminReportResDto::reportId, Function.identity()));

        List<AdminReportResDto> content = reportIds.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .map(dto -> // 만약 혹시 모를 중복이 걱정되면 한 번 더 distinct 안전망
                        new AdminReportResDto(
                                dto.reportId(), dto.postId(), dto.postType(), dto.postTitle(),
                                dto.reportedPersonId(), dto.reportedPersonNickname(),
                                dto.reportingPersonId(), dto.reportingPersonNickname(),
                                dto.reportedDate(), dto.description(), dto.status(),
                                dto.reportId() == null ? List.of()
                                        : dto.reasons().stream().distinct().toList()
                        )
                )
                .toList();

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
