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
import java.util.List;
import java.util.Optional;

import static com.souf.soufwebsite.domain.report.entity.QReport.report;

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

        List<AdminReportResDto> reportResDtos = queryFactory.select(
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
                                report.reportReason,
                                report.description,
                                report.status
                        )
                )
                .from(report)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                queryFactory
                        .select(report.count())
                        .from(report)
                        .where(condition)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(reportResDtos, pageable, total);
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
            predicate = report.reportDate.goe(startDate.atStartOfDay());
        }
        if (endDate != null) {
            BooleanExpression endPredicate =
                    report.reportDate.loe(endDate.atTime(LocalTime.MAX));
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
