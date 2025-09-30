package com.souf.soufwebsite.domain.recruit.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.dto.SortOption;
import com.souf.soufwebsite.domain.recruit.dto.req.MyRecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.req.RecruitSearchReqDto;
import com.souf.soufwebsite.domain.recruit.dto.res.MyRecruitResDto;
import com.souf.soufwebsite.domain.recruit.dto.res.RecruitSimpleResDto;
import com.souf.soufwebsite.domain.recruit.entity.MyRecruitSortKey;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.entity.RecruitSortKey;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.souf.soufwebsite.domain.recruit.entity.QRecruit.recruit;
import static com.souf.soufwebsite.domain.recruit.entity.QRecruitCategoryMapping.recruitCategoryMapping;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RecruitCustomRepositoryImpl implements RecruitCustomRepository{

    private final JPAQueryFactory queryFactory;
    private final FileService fileService;

    @Override
    public Page<RecruitSimpleResDto> getRecruitList(RecruitSearchReqDto req,
                                                    Pageable pageable) {

        // 1) 동적 where (EXISTS 기반)
        BooleanExpression where = buildWhere(req);

        OrderSpecifier<?>[] orderSpecifiers = buildOrderSpecifiers(req);

        // STEP 1. 페이지에 들어갈 recruit.id만 정확히 선별
        List<Long> pageIds = queryFactory
                .select(recruit.id)
                .from(recruit)
                .where(where)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (pageIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 총 개수
        Long total = queryFactory
                .select(recruit.id.countDistinct())
                .from(recruit)
                .where(where)
                .fetchOne();
        long totalCount = total == null ? 0L : total;

        // STEP 2. 선별된 ID들로 상세 재조회(조인/병합)
        List<Tuple> tuples = queryFactory
                .select(
                        recruit.id,
                        recruit.title,
                        recruitCategoryMapping.secondCategory.id,
                        recruit.content,
                        recruit.price,
                        recruit.city.name,
                        recruit.cityDetail.name,
                        recruit.deadline,
                        recruit.recruitCount,
                        recruit.recruitable,
                        recruit.lastModifiedTime,
                        recruit.member.id
                )
                .from(recruit)
                .join(recruit.categories, recruitCategoryMapping)
                .leftJoin(recruit.city)
                .leftJoin(recruit.cityDetail)
                .where(recruit.id.in(pageIds))
                .fetch();

        // 병합
        Map<Long, RecruitSimpleResDto> byId = new LinkedHashMap<>();
        for (Tuple t : tuples) {
            Long id = t.get(recruit.id);
            Long secondCatId = Optional.ofNullable(t.get(recruitCategoryMapping.secondCategory.id)).orElse(0L);

            if (!byId.containsKey(id)) {
                String city = Optional.ofNullable(t.get(recruit.city.name)).orElse("");
                String cityDetail = Optional.ofNullable(t.get(recruit.cityDetail.name)).orElse("");

                RecruitSimpleResDto dto = RecruitSimpleResDto.of(
                        id,
                        t.get(recruit.title),
                        secondCatId,
                        t.get(recruit.content),
                        t.get(recruit.price),
                        city,
                        cityDetail,
                        t.get(recruit.deadline),
                        t.get(recruit.recruitCount),
                        Boolean.TRUE.equals(t.get(recruit.recruitable)),
                        t.get(recruit.lastModifiedTime),
                        t.get(recruit.member.id)
                );
                byId.put(id, dto);
            } else {
                byId.get(id).addSecondCategory(secondCatId);
            }
        }

        // STEP1에서 뽑은 정렬 순서를 보존하기 위해 pageIds 순서대로 재정렬
        List<RecruitSimpleResDto> ordered = pageIds.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new PageImpl<>(ordered, pageable, totalCount);
    }

    @Override
    public Page<MyRecruitResDto> getMyRecruits(Member me, MyRecruitReqDto req, Pageable pageable) {
        OrderSpecifier<?>[] orderSpecifiers = buildMyOrderSpecifiers(req);

        List<Recruit> rows = queryFactory
                .selectFrom(recruit)
                .where(recruit.member.eq(me))
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(recruit.count())
                .from(recruit)
                .where(recruit.member.eq(me))
                .fetchOne();

        String profileImageUrl = fileService.getMediaUrl(PostType.PROFILE, me.getId());

        List<MyRecruitResDto> content = rows.stream()
                .map(r -> {
                    String status = r.isRecruitable() ? "모집 중" : "마감";
                    List<CategoryDto> categories = r.getCategories().stream()
                            .map(m -> new CategoryDto(
                                    idOrNull(m.getFirstCategory()),
                                    idOrNull(m.getSecondCategory()),
                                    idOrNull(m.getThirdCategory())
                            ))
                            .toList();

                    return new MyRecruitResDto(
                            r.getId(),
                            r.getTitle(),
                            r.getDeadline(),
                            categories,
                            status,
                            r.getRecruitCount(),
                            me.getNickname(),
                            profileImageUrl
                    );
                })
                .toList();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private static Long idOrNull(Object c) {
        if (c == null) return null;
        if (c instanceof FirstCategory fc)  return fc.getId();
        if (c instanceof SecondCategory sc) return sc.getId();
        if (c instanceof ThirdCategory tc)  return tc.getId();
        return null;
    }

    private NumberExpression<Integer> maxPaymentNumber() {
        return Expressions.numberTemplate(Integer.class,
                "cast(nullif(replace(replace(trim({0}), '만원', ''), ',', ''), '') as integer)",
                recruit.price
        );
    }

    private OrderSpecifier<?>[] buildOrderSpecifiers(RecruitSearchReqDto req) {
        RecruitSortKey key = req.sortOption().sortKeyOrDefault(RecruitSortKey.RECENT);
        SortOption.SortDir dir = req.sortOption().sortDirOrDefault();
        Order o = (dir == SortOption.SortDir.ASC) ? Order.ASC : Order.DESC;

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

    private OrderSpecifier<?>[] buildMyOrderSpecifiers(MyRecruitReqDto req) {
        MyRecruitSortKey key = req.sortOption().sortKey();
        SortOption.SortDir dir = req.sortOption().sortDirOrDefault();
        Order o = (dir == SortOption.SortDir.ASC) ? Order.ASC : Order.DESC;

        return switch (key) {
            case RECENT -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(o, recruit.lastModifiedTime)
            };
            case VIEWS  -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(o, recruit.viewCount),
                    new OrderSpecifier<>(Order.DESC, recruit.lastModifiedTime)
            };
            case COUNT  -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(o, recruit.recruitCount),
                    new OrderSpecifier<>(Order.DESC, recruit.lastModifiedTime)
            };
        };
    }

    private BooleanExpression buildWhere(RecruitSearchReqDto req) {
        List<BooleanExpression> ands = new ArrayList<>();

        // 텍스트 필터
        if (req.title() != null && !req.title().isBlank()) {
            ands.add(recruit.title.contains(req.title()));
        }
        if (req.content() != null && !req.content().isBlank()) {
            ands.add(recruit.content.contains(req.content()));
        }

        // 카테고리(리스트 우선)
        if (req.categories() != null && !req.categories().isEmpty()) {
            ands.add(buildCategoryExistsOr(req.categories()));
        }

        // AND 결합
        return ands.stream().filter(Objects::nonNull).reduce(BooleanExpression::and).orElse(null);
    }

    /**
     * (F,S,T) 조합들의 OR: 각 조합을 EXISTS(매핑 테이블)로 감싸 중복 없이 필터링
     *  - (1-2-3) OR (2-*-*) OR (3-1-*)
     */
    private BooleanExpression buildCategoryExistsOr(List<CategoryDto> categories) {
        // 최적화: 전부 first-only면 IN 사용
        boolean onlyFirst = categories.stream().allMatch(c ->
                c.firstCategory() != null && c.secondCategory() == null && c.thirdCategory() == null);

        if (onlyFirst) {
            List<Long> firstIds = categories.stream()
                    .map(CategoryDto::firstCategory)
                    .distinct()
                    .collect(Collectors.toList());
            return recruit.id.in(
                    JPAExpressions
                            .select(recruitCategoryMapping.recruit.id)
                            .from(recruitCategoryMapping)
                            .where(recruitCategoryMapping.firstCategory.id.in(firstIds))
            );
        }

        // 일반: 각 조합 → EXISTS … AND 묶음 → 전체는 OR
        List<BooleanExpression> ors = new ArrayList<>();
        for (CategoryDto c : categories) {
            ors.add(existsCategory(c.firstCategory(), c.secondCategory(), c.thirdCategory()));
        }
        return ors.stream().filter(Objects::nonNull).reduce(BooleanExpression::or).orElse(null);
    }

    private BooleanExpression existsCategory(Long first, Long second, Long third) {
        if (first == null && second == null && third == null) {
            return null;
        }
        BooleanExpression cond = recruitCategoryMapping.recruit.id.eq(recruit.id);
        if (first != null)  cond = cond.and(recruitCategoryMapping.firstCategory.id.eq(first));
        if (second != null) cond = cond.and(recruitCategoryMapping.secondCategory.id.eq(second));
        if (third != null)  cond = cond.and(recruitCategoryMapping.thirdCategory.id.eq(third));

        return JPAExpressions
                .selectOne()
                .from(recruitCategoryMapping)
                .where(cond)
                .exists();
    }
}
