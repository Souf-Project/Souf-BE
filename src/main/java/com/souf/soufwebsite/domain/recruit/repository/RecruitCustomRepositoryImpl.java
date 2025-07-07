package com.souf.soufwebsite.domain.recruit.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.recruit.dto.RecruitSearchReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitSimpleResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.souf.soufwebsite.domain.recruit.entity.QRecruit.recruit;
import static com.souf.soufwebsite.domain.recruit.entity.QRecruitCategoryMapping.recruitCategoryMapping;

@Repository
@RequiredArgsConstructor
public class RecruitCustomRepositoryImpl implements RecruitCustomRepository{

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<RecruitSimpleResDto> getRecruitList(Long first, Long second, Long third,
                                                    RecruitSearchReqDto searchReqDto, Pageable pageable) {

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
                .leftJoin(recruit.cityDetail)
                .where(
                        first != null ? recruitCategoryMapping.firstCategory.id.eq(first) : null,
                        second != null ? recruitCategoryMapping.secondCategory.id.eq(second) : null,
                        third != null ? recruitCategoryMapping.thirdCategory.id.eq(third) : null,
                        searchReqDto.title() != null ? recruit.title.contains(searchReqDto.title()) : null,
                        searchReqDto.content() != null ? recruit.content.contains(searchReqDto.content()) : null
                )
                .orderBy(recruit.lastModifiedTime.desc())
                .fetch();

        // 중복 제거 및 병합 처리
        Map<Long, RecruitSimpleResDto> mergedMap = new LinkedHashMap<>();

        for (Tuple t : tuples) {
            Long recruitId = t.get(recruit.id);
            Long secondCatId = t.get(recruitCategoryMapping.secondCategory.id);

            if (!mergedMap.containsKey(recruitId)) {
                String cityDetailName = Optional.ofNullable(t.get(recruit.cityDetail.name)).orElse("");

                RecruitSimpleResDto dto = RecruitSimpleResDto.of(
                        recruitId,
                        t.get(recruit.title),
                        secondCatId,
                        t.get(recruit.content),
                        t.get(recruit.minPayment),
                        t.get(recruit.maxPayment),
                        t.get(recruit.city.name),
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
}
