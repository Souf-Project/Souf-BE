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

@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Member> searchMembers(String keyword, Pageable pageable) {
        QMember member = QMember.member;

        BooleanExpression condition = member.role.eq(RoleType.MEMBER)
                .and(
                        member.username.containsIgnoreCase(keyword)
                                .or(member.email.containsIgnoreCase(keyword))
                );

        List<Member> members = queryFactory
                .selectFrom(member)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(member.count())
                .from(member)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(members, pageable, total);
    }
}
