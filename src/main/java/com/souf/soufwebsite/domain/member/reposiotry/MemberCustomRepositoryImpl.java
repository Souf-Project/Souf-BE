package com.souf.soufwebsite.domain.member.reposiotry;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.member.controller.MemberController;
import com.souf.soufwebsite.domain.member.dto.ReqDto.MemberSearchReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberSimpleResDto;
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
    private final FeedRepository feedRepository;

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

//    @Override
//    public Page<MemberSimpleResDto> getMemberList(Long first, Long second, Long third,
//                                                  MemberSearchReqDto searchReqDto, Pageable pageable) {
//
//        // 1. 기본 조회
//        List<MemberSimpleResDto> userList = queryFactory
//                .selectDistinct(Projections.constructor(
//                        MemberSimpleResDto.class,
//                        member.profileImageUrl,
//                        member.nickname,
//                        member.intro
//                ))
//                .from(member)
//                .leftJoin(member.categories, memberCategoryMapping)
//                .where(
//                        first != null ? memberCategoryMapping.firstCategory.id.eq(first) : null,
//                        second != null ? memberCategoryMapping.secondCategory.id.eq(second) : null,
//                        third != null ? memberCategoryMapping.thirdCategory.id.eq(third) : null,
//                        searchReqDto.keyword() != null ? (
//                                member.nickname.contains(searchReqDto.keyword())
//                                        .or(member.intro.contains(searchReqDto.keyword()))
//                        ) : null
//                )
//                .orderBy(member.lastModifiedTime.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        Long total = queryFactory
//                .select(member.countDistinct())
//                .from(member)
//                .leftJoin(member.categories, memberCategoryMapping)
//                .where(
//                        first != null ? memberCategoryMapping.firstCategory.id.eq(first) : null,
//                        second != null ? memberCategoryMapping.secondCategory.id.eq(second) : null,
//                        third != null ? memberCategoryMapping.thirdCategory.id.eq(third) : null,
//                        searchReqDto.keyword() != null ? (
//                                member.nickname.contains(searchReqDto.keyword())
//                                        .or(member.intro.contains(searchReqDto.keyword()))
//                        ) : null
//                )
//                .fetchOne();
//
//        List<MemberSimpleResDto> finalizedList = userList.stream()
//                .map(dto -> {
//                    String presignedProfile = fileService.getPresignedUrl(dto.profileImageUrl());
//
//                    Member member = memberRepository.findByNickname(dto.nickname())
//                            .orElseThrow();
//
//                    List<Feed> feeds = feedRepository.findTop3ByMemberOrderByViewCountDesc(member);
//
//                    //각 피드의 썸네일 → presigned URL 변환
//                    List<MemberSimpleResDto.PopularFeedDto> feedDtos = feeds.stream()
//                            .map(feed -> {
//                                MediaResDto thumbnail = MediaResDto.fromFeedThumbnail(feed);
//                                String presignedImageUrl = fileService.getPresignedUrl(thumbnail.fileUrl());
//                                return new MemberSimpleResDto.PopularFeedDto(presignedImageUrl);
//                            })
//                            .toList();
//
//                    return new MemberSimpleResDto(presignedProfile, dto.nickname(), dto.intro(), feedDtos);
//                })
//                .toList();
//
//        return new PageImpl<>(finalizedList, pageable, total == null ? 0L : total);
//    }
}
