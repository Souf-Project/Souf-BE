package com.souf.soufwebsite.domain.member.reposiotry;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.souf.soufwebsite.domain.member.entity.QMember.member;
import static com.souf.soufwebsite.domain.member.entity.QMemberCategoryMapping.memberCategoryMapping;
import static com.souf.soufwebsite.global.common.category.entity.QFirstCategory.firstCategory;

@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Member> findByCategory(Long first, Pageable pageable) {
        // 1) BooleanBuilder 를 사용해 동적 where 절 구성
        BooleanBuilder builder = new BooleanBuilder();
        if (first != null) {
            builder.and(memberCategoryMapping.firstCategory.id.eq(first));
        }

        // 2) 페이징된 회원 목록 조회 (1차 카테고리만 join 및 필터)
        List<Member> content = queryFactory
                .selectDistinct(member)
                .from(member)
                .join(member.categories, memberCategoryMapping)
                .join(memberCategoryMapping.firstCategory, firstCategory)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(member.lastModifiedTime.desc())
                .fetch();

        // 3) 전체 개수(count)를 위한 쿼리
        Long total = queryFactory
                .select(member.countDistinct())
                .from(member)
                .join(member.categories, memberCategoryMapping)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(
                content,
                pageable,
                total != null ? total : 0L
        );
    }

}
