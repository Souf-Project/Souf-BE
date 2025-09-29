package com.souf.soufwebsite.domain.review.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.recruit.dto.SortOption;
import com.souf.soufwebsite.domain.review.dto.ReviewSearchReqDto;
import com.souf.soufwebsite.domain.review.dto.ReviewSimpleResDto;
import com.souf.soufwebsite.domain.review.entity.Review;
import com.souf.soufwebsite.domain.review.entity.ReviewSortKey;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.souf.soufwebsite.domain.member.entity.QMember.member;
import static com.souf.soufwebsite.domain.recruit.entity.QRecruit.recruit;
import static com.souf.soufwebsite.domain.recruit.entity.QRecruitCategoryMapping.recruitCategoryMapping;
import static com.souf.soufwebsite.domain.review.entity.QReview.review;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory queryFactory;

    private final FileService fileService;


    @Override
    public Slice<ReviewSimpleResDto> getReviewBySlice(ReviewSearchReqDto req,
                                                      Pageable pageable) {

        BooleanExpression buildCategoryExistsOr = buildCategoryExistsOr(req.categories());
        OrderSpecifier<?>[] orderSpecifier = buildOrderSpecifiers(req);

        List<Long> reviewIds = queryFactory
                .select(review.id)
                .from(review)
                .join(review.recruit, recruit).fetchJoin()
                .where(buildCategoryExistsOr)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        if (reviewIds.isEmpty()) {
            return new SliceImpl<>(Collections.emptyList(), pageable, false);
        }

        List<Review> reviewList = queryFactory
                .selectFrom(review)
                .join(review.recruit, recruit).fetchJoin()
                .join(review.member, member).fetchJoin()
                .where(review.id.in(reviewIds.subList(0, Math.min(reviewIds.size(), pageable.getPageSize()))))
                .fetch();

        List<ReviewSimpleResDto> content = reviewList.stream().map(review -> {
            String mediaUrl = fileService.getMediaUrl(PostType.REVIEW, review.getId());

            return ReviewSimpleResDto.from(review, mediaUrl, review.getMember().getNickname(), review.getRecruit().getTitle());
        }).collect(Collectors.toList());


        boolean hasNextPage = reviewIds.size() > pageable.getPageSize();

        return new SliceImpl<>(content, pageable, hasNextPage);
    }

    private OrderSpecifier<?>[] buildOrderSpecifiers(ReviewSearchReqDto req) {
        ReviewSortKey key = req.sortOption().sortKeyOrDefault(ReviewSortKey.RECENT);
        SortOption.SortDir dir = req.sortOption().sortDirOrDefault();
        Order o = (dir == SortOption.SortDir.ASC) ? Order.ASC : Order.DESC;

        // 1차 정렬 기준 + 동점시 최신순 보정
        return switch (key) {
            case VIEWS   -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(o, review.viewTotalCount),
                    new OrderSpecifier<>(Order.DESC, review.createdTime) };
            case SCORE -> new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(o, review.score),
                    new OrderSpecifier<>(Order.DESC, review.createdTime)};
            case RECENT -> new OrderSpecifier<?>[]{new OrderSpecifier<>(o, review.createdTime)};
        };
    }

    private BooleanExpression buildCategoryExistsOr(List<CategoryDto> categories) {
        // 최적화: 전부 first-only면 IN 사용
        if(categories == null || categories.isEmpty()) {
            return null;
        }

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
