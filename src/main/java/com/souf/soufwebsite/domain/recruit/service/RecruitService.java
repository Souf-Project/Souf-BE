package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.file.dto.FileReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitCreateResDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitResDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitSimpleResDto;

import java.util.List;

public interface RecruitService {

    RecruitCreateResDto createRecruit(RecruitReqDto reqDto);

    void uploadRecruitMedia(FileReqDto reqDto);

    List<RecruitSimpleResDto> getRecruits(Long first, Long second, Long third);

    RecruitResDto getRecruitById(Long recruitId);

    void updateRecruit(Long recruitId, RecruitReqDto reqDto);

    void deleteRecruit(Long recruitId);
}
