package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.file.dto.FileReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitCreateReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitResDto;
import com.souf.soufwebsite.global.common.FirstCategory;

import java.util.List;

public interface RecruitService {

    RecruitCreateReqDto createRecruit(RecruitReqDto reqDto);

    void uploadRecruitMedia(FileReqDto reqDto);

    List<RecruitResDto> getRecruits(FirstCategory categoryName);

    RecruitResDto getRecruitById(Long recruitId);

    void updateRecruit(Long recruitId, RecruitReqDto reqDto);

    void deleteRecruit(Long recruitId);
}
