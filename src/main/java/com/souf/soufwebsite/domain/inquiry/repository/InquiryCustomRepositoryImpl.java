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
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class InquiryCustomRepositoryImpl implements InquiryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<InquiryResDto> getInquiryListInAdmin(InquiryType inquiryType, InquiryStatus inquiryStatus, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(getInquiryType(inquiryType));
        builder.and(getInquiryStatus(inquiryStatus));

        List<Inquiry> result = queryFactory.selectFrom(inquiry)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(inquiry.id.countDistinct())
                .from(inquiry)
                .where(builder)
                .fetchOne();
        long totalCount = total == null ? 0 : total;

        List<InquiryResDto> resDtoList = result.stream().map(
                r -> new InquiryResDto(r.getId(), r.getTitle(), r.getContent())
        ).toList();

        return new PageImpl<>(resDtoList, pageable, totalCount);
    }

    private BooleanExpression getInquiryType(InquiryType inquiryType) {
        return hasText(String.valueOf(inquiryType)) ? inquiry.inquiryType.eq(inquiryType) : null;
    }

    private BooleanExpression getInquiryStatus(InquiryStatus inquiryStatus) {
        return hasText(String.valueOf(inquiryStatus)) ? inquiry.inquiryStatus.eq(inquiryStatus) : null;
    }
}
