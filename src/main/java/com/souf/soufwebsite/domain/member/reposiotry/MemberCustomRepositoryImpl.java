package com.souf.soufwebsite.domain.member.reposiotry;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.QMember;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.souf.soufwebsite.domain.member.entity.QMember.member;
import static com.souf.soufwebsite.domain.member.entity.QMemberCategoryMapping.memberCategoryMapping;
import static com.souf.soufwebsite.global.common.category.entity.QFirstCategory.firstCategory;
import static com.souf.soufwebsite.global.common.category.entity.QSecondCategory.secondCategory;
import static com.souf.soufwebsite.global.common.category.entity.QThirdCategory.thirdCategory;

@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Member> findByCategories(Long first, Long second, Long third, Pageable pageable) {
        List<Member> members = queryFactory
                .selectDistinct(member)
                .from(member)
                .join(member.categories, memberCategoryMapping)
                .join(memberCategoryMapping.firstCategory, firstCategory)
                .join(memberCategoryMapping.secondCategory, secondCategory)
                .join(memberCategoryMapping.thirdCategory, thirdCategory)
                .where(
                        first != null ? memberCategoryMapping.firstCategory.id.eq(first) : null,
                        second != null ? memberCategoryMapping.secondCategory.id.eq(second) : null,
                        third != null ? memberCategoryMapping.thirdCategory.id.eq(third) : null
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(member.lastModifiedTime.desc())
                .fetch();

        Long total = queryFactory
                .select(member.countDistinct())
                .from(member)
                .join(member.categories, memberCategoryMapping)
                .join(memberCategoryMapping.firstCategory, firstCategory)
                .join(memberCategoryMapping.secondCategory, secondCategory)
                .join(memberCategoryMapping.thirdCategory, thirdCategory)
                .where(
                        first != null ? memberCategoryMapping.firstCategory.id.eq(first) : null,
                        second != null ? memberCategoryMapping.secondCategory.id.eq(second) : null,
                        third != null ? memberCategoryMapping.thirdCategory.id.eq(third) : null
                )
                .fetchOne();

        return new PageImpl<>(members, pageable, total != null ? total : 0L);
    }
}
