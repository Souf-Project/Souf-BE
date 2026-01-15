package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.city.entity.City;
import com.souf.soufwebsite.domain.city.entity.CityDetail;
import com.souf.soufwebsite.domain.city.exception.NotFoundCityDetailException;
import com.souf.soufwebsite.domain.city.exception.NotFoundCityException;
import com.souf.soufwebsite.domain.city.exception.NotMatchedCityDetailException;
import com.souf.soufwebsite.domain.city.exception.RequiredCityDetailException;
import com.souf.soufwebsite.domain.city.repository.CityDetailRepository;
import com.souf.soufwebsite.domain.city.repository.CityRepository;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.dto.video.VideoDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.event.MediaCleanupHelper;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.file.service.MediaCleanupPublisher;
import com.souf.soufwebsite.domain.file.service.S3UploaderService;
import com.souf.soufwebsite.domain.member.dto.reqDto.MemberIdReqDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.recruit.dto.req.MyRecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.req.RecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.req.RecruitSearchReqDto;
import com.souf.soufwebsite.domain.recruit.dto.res.*;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.entity.RecruitCategoryMapping;
import com.souf.soufwebsite.domain.recruit.event.RecruitChangedEvent;
import com.souf.soufwebsite.domain.recruit.exception.NotFoundRecruitException;
import com.souf.soufwebsite.domain.recruit.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.recruit.query.SubscriberQuery;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import com.souf.soufwebsite.global.common.category.service.CategoryService;
import com.souf.soufwebsite.global.common.viewCount.service.ViewCountService;
import com.souf.soufwebsite.global.redis.util.RedisUtil;
import com.souf.soufwebsite.global.slack.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.souf.soufwebsite.global.util.SecurityUtils.getCurrentMember;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitServiceImpl implements RecruitService {

    private final FileService fileService;
    private final S3UploaderService s3UploaderService;

    private final RecruitRepository recruitRepository;
    private final MemberRepository memberRepository;
    private final CityRepository cityRepository;
    private final CityDetailRepository cityDetailRepository;
    private final CategoryService categoryService;
    private final RedisUtil redisUtil;
//    private final IndexEventPublisherHelper indexEventPublisherHelper;
    private final SlackService slackService;
    private final ViewCountService viewCountService;

    private final MediaCleanupPublisher mediaCleanupPublisher;
    private final MediaCleanupHelper mediaCleanupHelper;

    private static final String AGG_KEY_FMT = "notif:agg:%d:%d"; // notif:agg:{memberId}:{firstId}
    private static final Duration AGG_WINDOW = java.time.Duration.ofHours(1);
    private final SubscriberQuery subscriberQuery;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public RecruitCreateResDto createRecruit(String email, RecruitReqDto reqDto) {
        Member member = findIfEmailExists(email);

        City city = cityRepository.findById(reqDto.cityId())
                .orElseThrow(NotFoundCityException::new);
        CityDetail cityDetail = validateCityOrThrow(city, reqDto.cityDetailId());

        Recruit recruit = Recruit.of(reqDto, member, city, cityDetail);
        injectCategories(reqDto, recruit);
        recruit = recruitRepository.save(recruit);

        List<Long> firstIds = extractFirstIds(reqDto);
        for (Long firstId : firstIds) {
            enqueueRecruitPublished(firstId);
        }
        log.info("CatPair completed");

//        indexEventPublisherHelper.publishIndexEvent(
//                EntityType.RECRUIT,
//                OperationType.CREATE,
//                "Recruit",
//                recruit
//        );

        PresignedUrlResDto logoResDto = null;
        if (reqDto.logoOriginalFileName() != null) {
            logoResDto = s3UploaderService.generatePresignedUploadUrl("logo", reqDto.logoOriginalFileName());
        }


        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl("recruit", reqDto.originalFileNames());
        VideoDto videoDto = fileService.configVideoUploadInitiation(reqDto.originalFileNames(), PostType.RECRUIT);

        String slackMsg = member.getNickname() + " 님이 공고문을 작성하였습니다.\n" +
                "https://www.souf.co.kr/recruitDetails/" + recruit.getId().toString() + "\n" +
                member.getNickname() + " 님을 다같이 환영해보아요:)";
        slackService.sendSlackMessage(slackMsg, "post");

        Recruit savedRecruit = recruit;
//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//            @Override public void afterCommit() {
//                publisher.publishEvent(new RecruitChangedEvent(
//                        savedRecruit.getId(),
//                        RecruitChangedEvent.ChangeType.CREATED,
//                        savedRecruit.isRecruitable(),
//                        savedRecruit.getDeadline()
//                ));
//            }
//        });

        return new RecruitCreateResDto(recruit.getId(), presignedUrlResDtos, logoResDto, videoDto);
    }

    @Override
    @Transactional
    public void uploadRecruitMedia(MediaReqDto reqDto) {
        Recruit recruit = findIfRecruitExist(reqDto.postId());

        fileService.uploadMetadata(reqDto, PostType.RECRUIT, recruit.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<RecruitSimpleResDto> getRecruits(RecruitSearchReqDto reqDto,
                                                 Pageable pageable) {
        if (reqDto.categories() != null && !reqDto.categories().isEmpty()) {
            categoryService.validate(reqDto.categories());
        }

        Page<RecruitSimpleResDto> page = recruitRepository.getRecruitList(reqDto, pageable);

        List<Long> writerIds = page.getContent().stream()
                .map(RecruitSimpleResDto::writerId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, Member> memberMap = memberRepository.findAllById(writerIds).stream()
                .collect(Collectors.toMap(Member::getId, m -> m));

        List<RecruitSimpleResDto> enriched = page.getContent().stream()
                .map(dto -> {
                    Member m = memberMap.get(dto.writerId());
                    String profileUrl = fileService.getMediaUrl(PostType.PROFILE, dto.writerId());
                    return dto.withWriter(m.getNickname(), profileUrl);
                })
                .toList();

        return new PageImpl<>(enriched, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    @Override
    public RecruitResDto getRecruitById(Long recruitId, String ip, String userAgent) {
        Member currentMember = getCurrentMember();

        Recruit recruit = findIfRecruitExist(recruitId);
        Member recruitMember = recruit.getMember();

        long totalViewCount = viewCountService.updateTotalViewCount(currentMember, PostType.RECRUIT, recruit.getId(), recruit.getViewCount(), ip, userAgent);

        List<Media> mediaList = fileService.getMediaList(PostType.RECRUIT, recruitId);
        String logoUrl = fileService.getMediaUrl(PostType.LOGO, recruit.getId());

        return RecruitResDto.from(recruitMember.getId(), recruit, totalViewCount, recruitMember.getNickname(), mediaList, logoUrl);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MyRecruitResDto> getMyRecruits(String email, MyRecruitReqDto reqDto, Pageable pageable) {
        Member me = findIfEmailExists(email);
        return recruitRepository.getMyRecruits(me, reqDto, pageable);
    }

    @Transactional
    @Override
    public RecruitCreateResDto updateRecruit(String email, Long recruitId, RecruitReqDto reqDto) {
        Member member = findIfEmailExists(email);
        Recruit recruit = findIfRecruitExist(recruitId);
        verifyIfRecruitIsMine(recruit, member);

        boolean beforeRecruitable = recruit.isRecruitable();
        LocalDateTime beforeDeadline = recruit.getDeadline();

        City city = cityRepository.findById(reqDto.cityId())
                .orElseThrow(NotFoundCityException::new);
        CityDetail cityDetail = validateCityOrThrow(city, reqDto.cityDetailId());

        updateRemainingImages(reqDto, recruit);
        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl("recruit", reqDto.originalFileNames());
        VideoDto videoDto = fileService.configVideoUploadInitiation(reqDto.originalFileNames(), PostType.RECRUIT);

        PresignedUrlResDto logoResDto = null;
        if (reqDto.logoOriginalFileName() != null) {
            mediaCleanupPublisher.publish(PostType.LOGO, recruitId);
            logoResDto = s3UploaderService.generatePresignedUploadUrl("logo", reqDto.logoOriginalFileName());
        }

        recruit.updateRecruit(reqDto, city, cityDetail);
        recruit.clearCategories();
        injectCategories(reqDto, recruit);

//        indexEventPublisherHelper.publishIndexEvent(
//                EntityType.RECRUIT,
//                OperationType.UPDATE,
//                "Recruit",
//                recruit
//        );

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                boolean statusOrDeadlineChanged =
                        beforeRecruitable != recruit.isRecruitable()
                                || !Objects.equals(beforeDeadline, recruit.getDeadline());

                publisher.publishEvent(new RecruitChangedEvent(
                        recruit.getId(),
                        statusOrDeadlineChanged
                                ? RecruitChangedEvent.ChangeType.STATUS_OR_DEADLINE_UPDATED
                                : RecruitChangedEvent.ChangeType.CONTENT_UPDATED,
                        recruit.isRecruitable(),
                        recruit.getDeadline()
                ));
            }
        });

        return new RecruitCreateResDto(recruit.getId(), presignedUrlResDtos, logoResDto, videoDto);
    }

    @Override
    @Transactional
    public void deleteRecruit(String email, Long recruitId) {
        Member member = findIfEmailExists(email);
        Recruit recruit = findIfRecruitExist(recruitId);
        verifyIfRecruitIsMine(recruit, member);

        String recruitViewKey = getRecruitViewKey(recruit.getId());
        redisUtil.deleteKey(recruitViewKey);

        recruitRepository.delete(recruit);

//        indexEventPublisherHelper.publishIndexEvent(
//                EntityType.RECRUIT,
//                OperationType.DELETE,
//                "Recruit",
//                recruit.getId()
//        );

        mediaCleanupPublisher.publish(PostType.RECRUIT, recruitId);
        mediaCleanupPublisher.publish(PostType.LOGO, recruitId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                publisher.publishEvent(
                        new RecruitChangedEvent(
                            recruit.getId(),
                            RecruitChangedEvent.ChangeType.DELETED,
                            false,
                            null
                        )
                );
            }
        });
    }

    @Override
    @Cacheable(value = "popularRecruits",
            key = "'recruit:popular'")
    public List<RecruitPopularityResDto> getPopularRecruits() {
        LocalDateTime now = LocalDateTime.now();
        List<Recruit> popularRecruits = recruitRepository.findTop5ByRecruitableAndDeadlineAfterOrderByDeadlineAsc(now);

        log.info("공고문 로직 실행 중");

        List<RecruitPopularityResDto> recruitPopularityResDtos = popularRecruits.stream().map(
                r -> {
                    String mediaUrl = fileService.getMediaUrl(PostType.PROFILE, r.getMember().getId());
                    return RecruitPopularityResDto.of(r, mediaUrl);
                }
        ).toList();
        log.info("size: {}", recruitPopularityResDtos.size());

        return recruitPopularityResDtos;
    }

    @Transactional
    @Override
    public void updateRecruitable(Long recruitId, MemberIdReqDto reqDto) {
        Recruit recruit = findIfRecruitExist(recruitId);
        Member member = memberRepository.findById(reqDto.memberId()).orElseThrow(NotFoundMemberException::new);

        verifyIfRecruitIsMine(recruit, member); // 소지 여부 확인

        boolean beforeRecruitable = recruit.isRecruitable();
        LocalDateTime beforeDeadline = recruit.getDeadline();

        recruit.updateRecruitable();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                boolean statusOrDeadlineChanged =
                        beforeRecruitable != recruit.isRecruitable()
                                || !Objects.equals(beforeDeadline, recruit.getDeadline());

                if (statusOrDeadlineChanged) {
                    publisher.publishEvent(new RecruitChangedEvent(
                            recruit.getId(),
                            RecruitChangedEvent.ChangeType.STATUS_OR_DEADLINE_UPDATED,
                            recruit.isRecruitable(),
                            recruit.getDeadline()
                    ));
                }
            }
        });
    }

    private Member findIfEmailExists(String email) {
        return memberRepository.findByEmail(email).orElseThrow(NotFoundMemberException::new);
    }

    private void verifyIfRecruitIsMine(Recruit recruit, Member member) {
        if(!recruit.getMember().getId().equals(member.getId())){
            throw new NotValidAuthenticationException();
        }
    }

    private Recruit findIfRecruitExist(Long id) {
        return recruitRepository.findById(id).orElseThrow(NotFoundRecruitException::new);
    }

    private void injectCategories(RecruitReqDto reqDto, Recruit recruit) {
        for(CategoryDto dto : reqDto.categoryDtos()){
            FirstCategory firstCategory = categoryService.findIfFirstIdExists(dto.firstCategory());
            SecondCategory secondCategory = categoryService.findIfSecondIdExists(dto.secondCategory());
            ThirdCategory thirdCategory = categoryService.findIfThirdIdExists(dto.thirdCategory());

            categoryService.validate(dto.firstCategory(), dto.secondCategory(), dto.thirdCategory());
            RecruitCategoryMapping recruitCategoryMapping = RecruitCategoryMapping.of(recruit, firstCategory, secondCategory, thirdCategory);
            recruit.addCategory(recruitCategoryMapping);
        }
    }

    private String getRecruitViewKey(Long recruitId) {
        return "recruit:view:" + recruitId;
    }

    private CityDetail validateCityOrThrow(City city, Long cityDetailId) {

        if ("지역 무관".equals(city.getName())) {
            return null;
        }
        if (cityDetailId == null) {
            throw new RequiredCityDetailException();
        }

        CityDetail cityDetail = cityDetailRepository.findById(cityDetailId)
                .orElseThrow(NotFoundCityDetailException::new);
        // cityDetail과 city가 연관이 있는지 유효성 검사 추가
        if(!cityDetail.getCity().getId().equals(city.getId())){
            throw new NotMatchedCityDetailException();
        }

        return cityDetail;
    }

    private void updateRemainingImages(RecruitReqDto reqDto, Recruit recruit) {
        List<String> removed = mediaCleanupHelper.purgeRemovedMedias(
                PostType.RECRUIT,
                recruit.getId(),
                reqDto.existingImageUrls()
        );

        // 삭제할 URL이 있으면 S3 삭제 이벤트 발행
        if (!removed.isEmpty()) {
            mediaCleanupPublisher.publishUrls(PostType.RECRUIT, recruit.getId(), removed);
        }
    }

    // reqDto에서 (first, second) 페어를 추출 (third는 무시)
    private List<Long> extractFirstIds(RecruitReqDto reqDto) {
        if (reqDto.categoryDtos() == null) return List.of();
        return reqDto.categoryDtos().stream()
                .map(CategoryDto::firstCategory)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private void enqueueRecruitPublished(Long firstId) {
        var subscriberIds = subscriberQuery.findSubscriberIdsByFirst(firstId);
        log.info("첫번째 카테고리 ID: {} 에 대한 구독자 수: {}", firstId, subscriberIds.size());
        if (subscriberIds == null || subscriberIds.isEmpty()) return;

        for (Long memberId : subscriberIds) {
            String key = AGG_KEY_FMT.formatted(memberId, firstId);
            redisTemplate.opsForHash().increment(key, "count", 1);
            redisTemplate.expire(key, AGG_WINDOW);
        }
    }
}

