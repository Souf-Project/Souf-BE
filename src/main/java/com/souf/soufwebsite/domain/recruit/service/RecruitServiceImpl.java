package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.city.entity.City;
import com.souf.soufwebsite.domain.city.repository.CityRepository;
import com.souf.soufwebsite.domain.citydetail.entity.CityDetail;
import com.souf.soufwebsite.domain.citydetail.repository.CityDetailRepository;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.dto.*;
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
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitServiceImpl implements RecruitService {

    private final FileService fileService;
    private final RecruitRepository recruitRepository;
    private final CityRepository cityRepository;
    private final CityDetailRepository cityDetailRepository;
    private final CategoryService categoryService;
    private final RedisUtil redisUtil;

    private Member getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    @Override
    @Transactional
    public RecruitCreateResDto createRecruit(RecruitReqDto reqDto) {
        Member member = getCurrentUser();

        City city = cityRepository.findById(reqDto.cityId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 City ID입니다."));
        CityDetail cityDetail = validateCityOrThrow(city, reqDto.cityDetailId());

        Recruit recruit = Recruit.of(reqDto, member, city, cityDetail);
        injectCategories(reqDto, recruit);
        recruit = recruitRepository.save(recruit);

        String recruitViewKey = getRecruitViewKey(recruit.getId());
        redisUtil.set(recruitViewKey);

        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl("recruit", reqDto.originalFileNames());

        return new RecruitCreateResDto(recruit.getId(), presignedUrlResDtos);
    }

    @Override
    @Transactional
    public void uploadRecruitMedia(MediaReqDto reqDto) {
        Recruit recruit = findIfRecruitExist(reqDto.postId());
        List<Media> mediaList = fileService.uploadMetadata(reqDto, PostType.RECRUIT, recruit.getId());
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
    public Page<MyRecruitResDto> getMyRecruits(Pageable pageable) {
        Member me = getCurrentUser();
        return recruitRepository.findByMember(me, pageable)
                .map(r -> {
                    String status = r.isRecruitable() ? "모집 중" : "마감";

                    List<CategoryDto> categories = r.getCategories().stream()
                            .map(m -> new CategoryDto(
                                    m.getFirstCategory().getId(),
                                    m.getSecondCategory().getId(),
                                    m.getThirdCategory().getId()
                            ))
                            .collect(Collectors.toList());

                    return new MyRecruitResDto(
                            r.getId(),
                            r.getTitle(),
                            r.getDeadline(),
                            categories,
                            status,
                            r.getRecruitCount()
                    );
                });
    }

    @Transactional
    @Override
    public RecruitCreateResDto updateRecruit(Long recruitId, RecruitReqDto reqDto) {
        Member member = getCurrentUser();
        Recruit recruit = findIfRecruitExist(recruitId);
        verifyIfRecruitIsMine(recruit, member);

        City city = cityRepository.findById(reqDto.cityId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 City ID입니다."));
        CityDetail cityDetail = validateCityOrThrow(city, reqDto.cityDetailId());

        fileService.clearMediaList(PostType.RECRUIT, recruit.getId());
        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl("recruit", reqDto.originalFileNames());

        recruit.updateRecruit(reqDto, city, cityDetail);
        recruit.clearCategories();
        injectCategories(reqDto, recruit);

        return new RecruitCreateResDto(recruit.getId(), presignedUrlResDtos);
    }

    @Override
    public void deleteRecruit(Long recruitId) {
        Member member = getCurrentUser();
        Recruit recruit = findIfRecruitExist(recruitId);
        verifyIfRecruitIsMine(recruit, member);

        String recruitViewKey = getRecruitViewKey(recruit.getId());
        redisUtil.deleteKey(recruitViewKey);

        recruitRepository.delete(recruit);
    }

    @Override
    public Page<RecruitPopularityResDto> getPopularRecruits(Pageable pageable) {
        Page<Recruit> popularRecruits = recruitRepository.findByOrderByViewCountDesc(pageable);

        return popularRecruits.map(
                RecruitPopularityResDto::of
        );
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
            log.info("f: {}, s: {}, t: {}", firstCategory.getName(), secondCategory.getName(), thirdCategory.getName());

            categoryService.validate(firstCategory.getId(), secondCategory.getId(), thirdCategory.getId());
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

}
