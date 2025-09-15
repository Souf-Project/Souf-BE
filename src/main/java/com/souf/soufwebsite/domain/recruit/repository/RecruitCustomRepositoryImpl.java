package com.souf.soufwebsite.domain.recruit.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.recruit.dto.RecruitSearchReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitSimpleResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.souf.soufwebsite.domain.recruit.entity.QRecruit.recruit;
import static com.souf.soufwebsite.domain.recruit.entity.QRecruitCategoryMapping.recruitCategoryMapping;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RecruitCustomRepositoryImpl implements RecruitCustomRepository{

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<RecruitSimpleResDto> getRecruitList(Long first, Long second, Long third,
                                                    RecruitSearchReqDto searchReqDto, Pageable pageable) {

        OrderSpecifier<?>[] orderSpecifiers = buildOrderSpecifiers(searchReqDto);

        List<Tuple> tuples = queryFactory
                .select(
                        recruit.id,
                        recruit.title,
                        recruitCategoryMapping.secondCategory.id,
                        recruit.content,
                        recruit.minPayment,
                        recruit.maxPayment,
                        recruit.city.name,
                        recruit.cityDetail.name,
                        recruit.deadline,
                        recruit.recruitCount,
                        recruit.recruitable,
                        recruit.lastModifiedTime
                )
                .from(recruit)
                .join(recruit.categories, recruitCategoryMapping)
                .leftJoin(recruit.city)
                .leftJoin(recruit.cityDetail)
                .where(
                        first != null ? recruitCategoryMapping.firstCategory.id.eq(first) : null,
                        second != null ? recruitCategoryMapping.secondCategory.id.eq(second) : null,
                        third != null ? recruitCategoryMapping.thirdCategory.id.eq(third) : null,
                        searchReqDto.title() != null ? recruit.title.contains(searchReqDto.title()) : null,
                        searchReqDto.content() != null ? recruit.content.contains(searchReqDto.content()) : null
                )
                .orderBy(orderSpecifiers)
                .fetch();

        // 중복 제거 및 병합 처리
        Map<Long, RecruitSimpleResDto> mergedMap = new LinkedHashMap<>();

        for (Tuple t : tuples) {
            Long recruitId = t.get(recruit.id);
            Long secondCatId = t.get(recruitCategoryMapping.secondCategory.id) == null
                    ? 0L : t.get(recruitCategoryMapping.secondCategory.id);

            if (!mergedMap.containsKey(recruitId)) {
                String cityName = Optional.ofNullable(t.get(recruit.city.name)).orElse("");
                String cityDetailName = Optional.ofNullable(t.get(recruit.cityDetail.name)).orElse("");

                RecruitSimpleResDto dto = RecruitSimpleResDto.of(
                        recruitId,
                        t.get(recruit.title),
                        secondCatId,
                        t.get(recruit.content),
                        t.get(recruit.minPayment),
                        t.get(recruit.maxPayment),
                        cityName,
                        cityDetailName,
                        t.get(recruit.deadline),
                        t.get(recruit.recruitCount),
                        Boolean.TRUE.equals(t.get(recruit.recruitable)),
                        t.get(recruit.lastModifiedTime)
                );
                mergedMap.put(recruitId, dto);
            } else {
                if(secondCatId != null)
                    mergedMap.get(recruitId).addSecondCategory(secondCatId);
            }
        }

        List<RecruitSimpleResDto> mergedList = new ArrayList<>(mergedMap.values());

        // 수동 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), mergedList.size());
        List<RecruitSimpleResDto> paged = mergedList.subList(start, end);

        return new PageImpl<>(paged, pageable, mergedList.size());
    }

    private NumberExpression<Integer> maxPaymentNumber() {
        return Expressions.numberTemplate(Integer.class,
                "cast(nullif(replace(replace(trim({0}), '만원', ''), ',', ''), '') as integer)",
                recruit.maxPayment
        );
    }

    private OrderSpecifier<?>[] buildOrderSpecifiers(RecruitSearchReqDto req) {
        RecruitSearchReqDto.SortKey key = req.sortKeyOrDefault();
        RecruitSearchReqDto.SortDir dir = req.sortDirOrDefault();
        Order o = (dir == RecruitSearchReqDto.SortDir.ASC) ? Order.ASC : Order.DESC;

        // 1차 정렬 기준 + 동점시 최신순 보정
        return switch (key) {
            case VIEWS   -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(o, recruit.viewCount),
                    new OrderSpecifier<>(Order.DESC, recruit.lastModifiedTime) };
            case PAYMENT -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(o, maxPaymentNumber()),
                    new OrderSpecifier<>(Order.DESC, recruit.lastModifiedTime) };
            case RECENT  -> new OrderSpecifier<?>[]{ new OrderSpecifier<>(o, recruit.lastModifiedTime) };
        };
    }
}
