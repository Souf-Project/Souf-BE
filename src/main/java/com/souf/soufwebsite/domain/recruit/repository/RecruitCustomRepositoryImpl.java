package com.souf.soufwebsite.domain.recruit.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.recruit.dto.RecruitSearchReqDto;
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
    public Page<RecruitSimpleResDto> getRecruitList(Long first, Long second, Long third,
                                                    RecruitSearchReqDto searchReqDto, Pageable pageable) {

        List<RecruitSimpleResDto> recruitList = queryFactory
                .selectDistinct(Projections.constructor(
                        RecruitSimpleResDto.class,
                        recruit.id,
                        recruit.title,
                        recruitCategoryMapping.secondCategory.id,
                        recruit.content,
                        recruit.payment,
                        recruit.region,
                        recruit.deadline,
                        recruit.recruitCount,
                        recruit.lastModifiedTime
                        )
                ).from(recruit)
                .join(recruit.categories, recruitCategoryMapping)
                .where(
                        first != null ? recruitCategoryMapping.firstCategory.id.eq(first) : null,
                        second != null ? recruitCategoryMapping.secondCategory.id.eq(second) : null,
                        third != null ? recruitCategoryMapping.thirdCategory.id.eq(third) : null,
                        searchReqDto.title() != null ? recruit.title.eq(searchReqDto.title()) : null,
                        searchReqDto.content() != null ? recruit.content.eq(searchReqDto.content()) : null
                )
                .orderBy(recruit.lastModifiedTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(recruitList, pageable, recruitList.size());
    }
}
