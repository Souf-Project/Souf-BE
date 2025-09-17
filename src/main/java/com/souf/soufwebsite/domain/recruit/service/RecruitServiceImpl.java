package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.city.entity.City;
import com.souf.soufwebsite.domain.city.entity.CityDetail;
import com.souf.soufwebsite.domain.city.repository.CityDetailRepository;
import com.souf.soufwebsite.domain.city.repository.CityRepository;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.dto.ReqDto.MemberIdReqDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.opensearch.EntityType;
import com.souf.soufwebsite.domain.opensearch.OperationType;
import com.souf.soufwebsite.domain.opensearch.event.IndexEventPublisherHelper;
import com.souf.soufwebsite.domain.recruit.dto.req.MyRecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.req.RecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.req.RecruitSearchReqDto;
import com.souf.soufwebsite.domain.recruit.dto.res.*;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.entity.RecruitCategoryMapping;
import com.souf.soufwebsite.domain.recruit.exception.NotFoundRecruitException;
import com.souf.soufwebsite.domain.recruit.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import com.souf.soufwebsite.global.common.category.service.CategoryService;
import com.souf.soufwebsite.global.redis.util.RedisUtil;
import com.souf.soufwebsite.global.slack.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitServiceImpl implements RecruitService {

    private final FileService fileService;
    private final RecruitRepository recruitRepository;
    private final MemberRepository memberRepository;
    private final CityRepository cityRepository;
    private final CityDetailRepository cityDetailRepository;
    private final CategoryService categoryService;
    private final RedisUtil redisUtil;
    private final IndexEventPublisherHelper indexEventPublisherHelper;
    private final SlackService slackService;

    @Override
    @Transactional
    public RecruitCreateResDto createRecruit(String email, RecruitReqDto reqDto) {
        Member member = findIfEmailExists(email);

        City city = cityRepository.findById(reqDto.cityId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 City ID입니다."));
        CityDetail cityDetail = validateCityOrThrow(city, reqDto.cityDetailId());

        Recruit recruit = Recruit.of(reqDto, member, city, cityDetail);
        injectCategories(reqDto, recruit);
        recruit = recruitRepository.save(recruit);

        indexEventPublisherHelper.publishIndexEvent(
                EntityType.RECRUIT,
                OperationType.CREATE,
                "Recruit",
                recruit
        );

        String recruitViewKey = getRecruitViewKey(recruit.getId());
        redisUtil.set(recruitViewKey);

        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl("recruit", reqDto.originalFileNames());

        String slackMsg = member.getNickname() + " 님이 공고문을 작성하였습니다.\n" +
                "https://www.souf.co.kr/recruitDetails/" + recruit.getId().toString() + "\n" +
                member.getNickname() + " 님을 다같이 환영해보아요:)";
        slackService.sendSlackMessage(slackMsg, "post");
        return new RecruitCreateResDto(recruit.getId(), presignedUrlResDtos);
    }

    @Override
    @Transactional
    public void uploadRecruitMedia(MediaReqDto reqDto) {
        Recruit recruit = findIfRecruitExist(reqDto.postId());
        fileService.uploadMetadata(reqDto, PostType.RECRUIT, recruit.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<RecruitSimpleResDto> getRecruits(Long first,
                                                 Long second,
                                                 Long third,
                                                 RecruitSearchReqDto reqDto,
                                                 Pageable pageable) {
        categoryService.validate(first, second, third);

        return recruitRepository.getRecruitList(first, second, third, reqDto, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public RecruitResDto getRecruitById(Long recruitId) {
        Recruit recruit = findIfRecruitExist(recruitId);
        Member recruitMember = recruit.getMember();

        String recruitViewKey = getRecruitViewKey(recruit.getId());
        redisUtil.increaseCount(recruitViewKey);

        List<Media> mediaList = fileService.getMediaList(PostType.RECRUIT, recruitId);

        return RecruitResDto.from(recruitMember.getId(), recruit, recruitMember.getNickname(), mediaList);
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

        City city = cityRepository.findById(reqDto.cityId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 City ID입니다."));
        CityDetail cityDetail = validateCityOrThrow(city, reqDto.cityDetailId());

        updateRemainingImages(reqDto, recruit);
        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl("recruit", reqDto.originalFileNames());

        recruit.updateRecruit(reqDto, city, cityDetail);
        recruit.clearCategories();
        injectCategories(reqDto, recruit);

        indexEventPublisherHelper.publishIndexEvent(
                EntityType.RECRUIT,
                OperationType.UPDATE,
                "Recruit",
                recruit
        );

        return new RecruitCreateResDto(recruit.getId(), presignedUrlResDtos);
    }

    @Override
    public void deleteRecruit(String email, Long recruitId) {
        Member member = findIfEmailExists(email);
        Recruit recruit = findIfRecruitExist(recruitId);
        verifyIfRecruitIsMine(recruit, member);

        String recruitViewKey = getRecruitViewKey(recruit.getId());
        redisUtil.deleteKey(recruitViewKey);

        recruitRepository.delete(recruit);

        indexEventPublisherHelper.publishIndexEvent(
                EntityType.RECRUIT,
                OperationType.DELETE,
                "Recruit",
                recruit.getId()
        );
    }

    @Override
    @Cacheable(value = "popularRecruits",
            key = "'page:' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public List<RecruitPopularityResDto> getPopularRecruits(Pageable pageable) {
        Page<Recruit> popularRecruits = recruitRepository.findByRecruitableTrueOrderByViewCountDesc(pageable);

        log.info("공고문 로직 실행 중");

        Page<RecruitPopularityResDto> recruitPopularityResDtos = popularRecruits.map(r -> {
            String mediaUrl = fileService.getMediaUrl(PostType.PROFILE, r.getMember().getId());
            return RecruitPopularityResDto.of(r, mediaUrl);
        });
        log.info("size: {}", recruitPopularityResDtos.getSize());

        return recruitPopularityResDtos.getContent();
    }

    @Transactional
    @Override
    public void updateRecruitable(Long recruitId, MemberIdReqDto reqDto) {
        Recruit recruit = findIfRecruitExist(recruitId);
        Member member = memberRepository.findById(reqDto.memberId()).orElseThrow(NotFoundMemberException::new);

        verifyIfRecruitIsMine(recruit, member); // 소지 여부 확인

        recruit.updateRecruitable();
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
            throw new IllegalArgumentException("세부 지역은 필수입니다.");
        }
        return cityDetailRepository.findById(cityDetailId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 cityDetail ID입니다."));
    }

    private void updateRemainingImages(RecruitReqDto reqDto, Recruit recruit) {
        List<Media> mediaList = fileService.getMediaList(PostType.RECRUIT, recruit.getId());
        for (Media media : mediaList) {
            if (!reqDto.existingImageUrls().contains(media.getOriginalUrl())) {
                fileService.deleteMedia(media);  // DB에서만 삭제되도록 수정
            }
        }
    }

}
