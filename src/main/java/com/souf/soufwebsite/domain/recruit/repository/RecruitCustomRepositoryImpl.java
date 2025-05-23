package com.souf.soufwebsite.domain.recruit.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.recruit.dto.RecruitSimpleResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.souf.soufwebsite.domain.recruit.entity.QRecruit.recruit;
import static com.souf.soufwebsite.domain.recruit.entity.QRecruitCategoryMapping.recruitCategoryMapping;

@Repository
@RequiredArgsConstructor
public class RecruitCustomRepositoryImpl implements RecruitCustomRepository{

    private final JPAQueryFactory queryFactory;


    @Override
    public List<RecruitSimpleResDto> getRecruitList(Long first, Long second, Long third) {
        return queryFactory
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
                        first != null ? recruitCategoryMapping.firstCategory.id.eq(first) : null,
                        second != null ? recruitCategoryMapping.secondCategory.id.eq(second) : null,
                        third != null ? recruitCategoryMapping.thirdCategory.id.eq(third) : null
                )
                .orderBy(recruit.lastModifiedTime.desc())
                .fetch();
    }
}
