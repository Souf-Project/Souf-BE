package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitResDto;
import com.souf.soufwebsite.global.common.FirstCategory;

import java.util.List;

public interface RecruitService {

    void createRecruit(RecruitReqDto reqDto);

    List<RecruitResDto> getRecruits(FirstCategory categoryName);

    RecruitResDto getRecruitById(Long recruitId);

    void updateRecruit(Long recruitId, RecruitReqDto reqDto);

    void deleteRecruit(Long recruitId);
}
