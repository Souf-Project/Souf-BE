package com.souf.soufwebsite.domain.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.dto.ReqDto.MemberSearchReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberSimpleResDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.souf.soufwebsite.domain.member.entity.QMember.member;
import static com.souf.soufwebsite.domain.member.entity.QMemberCategoryMapping.memberCategoryMapping;

@Repository
@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final FeedRepository feedRepository;
    private final FileService fileService;

//    @Override
//    public Page<Member> findByCategory(Long first, Pageable pageable) {
//        // 1) BooleanBuilder 를 사용해 동적 where 절 구성
//        BooleanBuilder builder = new BooleanBuilder();
//        if (first != null) {
//            builder.and(memberCategoryMapping.firstCategory.id.eq(first));
//        }
//
//        // 2) 페이징된 회원 목록 조회 (1차 카테고리만 join 및 필터)
//        List<Member> content = queryFactory
//                .selectDistinct(member)
//                .from(member)
//                .join(member.categories, memberCategoryMapping)
//                .join(memberCategoryMapping.firstCategory, firstCategory)
//                .where(builder)
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .orderBy(member.lastModifiedTime.desc())
//                .fetch();
//
//        // 3) 전체 개수(count)를 위한 쿼리
//        Long total = queryFactory
//                .select(member.countDistinct())
//                .from(member)
//                .join(member.categories, memberCategoryMapping)
//                .where(builder)
//                .fetchOne();
//
//        return new PageImpl<>(
//                content,
//                pageable,
//                total != null ? total : 0L
//        );
//    }

    @Override
    public Page<MemberSimpleResDto> getMemberList(Long first, Long second, Long third,
                                                  MemberSearchReqDto searchReqDto, Pageable pageable) {

        List<Long> memberIds = queryFactory
                .select(member.id)
                .from(member)
                .leftJoin(member.categories, memberCategoryMapping)
                .where(
                        first != null ? memberCategoryMapping.firstCategory.id.eq(first) : null,
                        second != null ? memberCategoryMapping.secondCategory.id.eq(second) : null,
                        third != null ? memberCategoryMapping.thirdCategory.id.eq(third) : null,
                        searchReqDto.keyword() != null ? (
                                member.nickname.contains(searchReqDto.keyword()).or(member.intro.contains(searchReqDto.keyword()))
                        ) : null
                )
                .groupBy(member.id)
                .orderBy(member.lastModifiedTime.desc())
                .fetch();


        // 수동 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), memberIds.size());
        List<Long> pagedIds = memberIds.subList(start, end);

        // 2. 실제 멤버 정보 조회
        List<Member> members = queryFactory
                .selectFrom(member)
                .where(member.id.in(pagedIds))
                .orderBy(member.lastModifiedTime.desc())
                .fetch();


        // 3. DTO 변환
        List<MemberSimpleResDto> result = members.stream()
                .map(m -> {
                    Long memberId = m.getId();
                    String profileImageUrl = fileService.getMediaUrl(PostType.PROFILE, m.getId());
                    List<Feed> feeds = feedRepository.findTop3ByMemberOrderByViewCountDesc(m);
                    List<MemberSimpleResDto.PopularFeedDto> feedDtos = feeds.stream()
                            .map(feed -> {
                                List<Media> mediaList = fileService.getMediaList(PostType.FEED, feed.getId());
                                if (mediaList.isEmpty()) {
                                    return null; // 썸네일이 없는 경우 처리
                                }
                                String originalUrl = mediaList.get(0).getOriginalUrl(); // 썸네일은 첫 번째 media로 가정
                                return new MemberSimpleResDto.PopularFeedDto(originalUrl);
                            })
                            .filter(Objects::nonNull)
                            .toList();

                    return new MemberSimpleResDto(memberId, profileImageUrl, m.getNickname(), m.getIntro(), feedDtos);
                })
                .toList();

        return new PageImpl<>(result, pageable, memberIds.size());
    }

}
