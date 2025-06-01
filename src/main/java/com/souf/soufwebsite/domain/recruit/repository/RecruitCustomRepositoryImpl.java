package com.souf.soufwebsite.domain.recruit.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.recruit.dto.RecruitSimpleResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.souf.soufwebsite.domain.recruit.entity.QRecruit.recruit;
import static com.souf.soufwebsite.domain.recruit.entity.QRecruitCategoryMapping.recruitCategoryMapping;

@Repository
@RequiredArgsConstructor
public class RecruitCustomRepositoryImpl implements RecruitCustomRepository{

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<RecruitSimpleResDto> getRecruitList(Long first, Pageable pageable) {
        List<RecruitSimpleResDto> recruitList = queryFactory
                .select(Projections.constructor(
                        RecruitSimpleResDto.class,
                        recruit.id,
                        recruit.title,
                        recruitCategoryMapping.secondCategory.id,
                        recruit.content,
                        recruit.payment,
                        recruit.region,
                        recruit.deadline,
                        recruit.recruitCount
                        )
                ).from(recruit)
                .join(recruit.categories, recruitCategoryMapping)
                .where(
                        first != null ? recruitCategoryMapping.firstCategory.id.eq(first) : null
                )
                .orderBy(recruit.lastModifiedTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(recruitList, pageable, recruitList.size());
    }
}
