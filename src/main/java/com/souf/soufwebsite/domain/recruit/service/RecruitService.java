package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.member.dto.ReqDto.MemberIdReqDto;
import com.souf.soufwebsite.domain.recruit.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruitService {

    RecruitCreateResDto createRecruit(RecruitReqDto reqDto);

    void uploadRecruitMedia(MediaReqDto reqDto);

    Page<RecruitSimpleResDto> getRecruits(Long first, Long second, Long third,
                                          RecruitSearchReqDto searchReqDto, Pageable pageable);

    Page<MyRecruitResDto> getMyRecruits(Pageable pageable);

    RecruitResDto getRecruitById(Long recruitId);

    RecruitCreateResDto updateRecruit(Long recruitId, RecruitReqDto reqDto);

    void deleteRecruit(Long recruitId);

    Page<RecruitPopularityResDto> getPopularRecruits(Pageable pageable);

    void updateRecruitable(Long recruitId, MemberIdReqDto reqDto);
}
