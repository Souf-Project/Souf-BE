package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.dto.RecruitCreateResDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitResDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitSimpleResDto;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.entity.RecruitCategoryMapping;
import com.souf.soufwebsite.domain.recruit.exception.NotFoundRecruitException;
import com.souf.soufwebsite.domain.recruit.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.recruit.repository.RecruitCategoryMappingRepository;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import com.souf.soufwebsite.global.common.category.service.CategoryService;
import com.souf.soufwebsite.global.redis.util.RedisUtil;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitServiceImpl implements RecruitService {

    private final FileService fileService;
    private final RecruitRepository recruitRepository;
    private final RecruitCategoryMappingRepository recruitCategoryMappingRepository;
    private final CategoryService categoryService;
    private final RedisUtil redisUtil;

    private Member getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    @Override
    @Transactional
    public RecruitCreateResDto createRecruit(RecruitReqDto reqDto) {
        Member member = getCurrentUser();
        Recruit recruit = Recruit.of(reqDto, member);
        recruit = recruitRepository.save(recruit);
        injectCategories(reqDto, recruit);

        String recruitViewKey = getRecruitViewKey(recruit.getId());
        redisUtil.set(recruitViewKey);

        List<PresignedUrlResDto> presignedUrlResDtos = fileService.generatePresignedUrl("recruit", reqDto.originalFileNames());

        return new RecruitCreateResDto(recruit.getId(), presignedUrlResDtos);
    }

    @Override
    @Transactional
    public void uploadRecruitMedia(MediaReqDto reqDto) {
        Recruit recruit = findIfRecruitExist(reqDto.postId());
        List<Media> mediaList = fileService.uploadMetadata(reqDto);

        for(Media f : mediaList){
            recruit.addMediaOnRecruit(f);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<RecruitSimpleResDto> getRecruits(Long first, Long second, Long third) {

        return recruitRepository.getRecruitList(first, second, third);
    }

    @Transactional(readOnly = true)
    @Override
    public RecruitResDto getRecruitById(Long recruitId) {
        Member member = getCurrentUser();
        Recruit recruit = findIfRecruitExist(recruitId);

        String recruitViewKey = getRecruitViewKey(recruit.getId());
        redisUtil.increaseCount(recruitViewKey);

        return RecruitResDto.from(recruit, member.getNickname());
    }

    @Transactional
    @Override
    public void updateRecruit(Long recruitId, RecruitReqDto reqDto) {
        Member member = getCurrentUser();
        Recruit recruit = findIfRecruitExist(recruitId);
        verifyIfRecruitIsMine(recruit, member);

        recruit.updateRecruit(reqDto);
        recruit.clearCategories();
        injectCategories(reqDto, recruit);
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

    private void verifyIfRecruitIsMine(Recruit recruit, Member member) {
        if(!recruit.getMember().equals(member)){
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
            RecruitCategoryMapping recruitCategoryMapping = RecruitCategoryMapping.of(recruit, firstCategory, secondCategory, thirdCategory);
            recruitCategoryMappingRepository.save(recruitCategoryMapping);
            recruit.addCategory(recruitCategoryMapping);
        }
    }

    private String getRecruitViewKey(Long recruitId) {
        return "recruit:view:" + recruitId;
    }
}
