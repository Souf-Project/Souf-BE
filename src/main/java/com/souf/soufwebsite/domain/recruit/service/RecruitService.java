package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitCreateResDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitResDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitSimpleResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruitService {

    RecruitCreateResDto createRecruit(RecruitReqDto reqDto);

    void uploadRecruitMedia(MediaReqDto reqDto);

    Page<RecruitSimpleResDto> getRecruits(Long first, Long second, Long third, Pageable pageable);

    RecruitResDto getRecruitById(Long recruitId);

    void updateRecruit(Long recruitId, RecruitReqDto reqDto);

    void deleteRecruit(Long recruitId);
}
