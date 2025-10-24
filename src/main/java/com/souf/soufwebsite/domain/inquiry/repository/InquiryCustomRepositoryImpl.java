package com.souf.soufwebsite.domain.inquiry.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryResDto;
import com.souf.soufwebsite.domain.inquiry.entity.Inquiry;
import com.souf.soufwebsite.domain.inquiry.entity.InquiryStatus;
import com.souf.soufwebsite.domain.inquiry.entity.InquiryType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.souf.soufwebsite.domain.inquiry.entity.QInquiry.inquiry;
import static com.souf.soufwebsite.domain.member.entity.QMember.member;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class InquiryCustomRepositoryImpl implements InquiryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<InquiryResDto> getInquiryListInAdmin(String search, InquiryType inquiryType, InquiryStatus inquiryStatus, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(getInquiryType(inquiryType));
        builder.and(getInquiryStatus(inquiryStatus));
        builder.and(containsWord(search));

        List<Inquiry> inquiries = queryFactory.selectFrom(inquiry)
                .leftJoin(inquiry.member, member)
                .where(builder)
                .orderBy(inquiry.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<InquiryResDto> result = inquiries.stream().map(
                InquiryResDto::of
        ).toList();

        Long total = queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(result, pageable, total == null ? 0 : total);
    }

    private BooleanExpression getInquiryType(InquiryType inquiryType) {
        return (inquiryType != null) ? inquiry.inquiryType.eq(inquiryType) : null;
    }

    private BooleanExpression getInquiryStatus(InquiryStatus inquiryStatus) {
        return (inquiryStatus != null) ? inquiry.inquiryStatus.eq(inquiryStatus) : null;
    }

    private BooleanExpression containsWord(String search) {
        if (!hasText(search)) return null;
        String kw = search.trim();
        return inquiry.title.containsIgnoreCase(kw)
                .or(inquiry.content.containsIgnoreCase(kw));
    }
}
