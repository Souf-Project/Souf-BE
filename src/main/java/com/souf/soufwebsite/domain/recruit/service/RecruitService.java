package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.MemberIdReqDto;
import com.souf.soufwebsite.domain.recruit.dto.req.MyRecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.req.RecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.req.RecruitSearchReqDto;
import com.souf.soufwebsite.domain.recruit.dto.res.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecruitService {

    RecruitCreateResDto createRecruit(String email, RecruitReqDto reqDto);

    void uploadRecruitMedia(MediaReqDto reqDto);

    Page<RecruitSimpleResDto> getRecruits(RecruitSearchReqDto searchReqDto, Pageable pageable);

    Page<MyRecruitResDto> getMyRecruits(String email, MyRecruitReqDto reqDto, Pageable pageable);

    RecruitResDto getRecruitById(Long recruitId, String ip, String userAgent);

    RecruitCreateResDto updateRecruit(String email, Long recruitId, RecruitReqDto reqDto);

    void deleteRecruit(String email, Long recruitId);

    List<RecruitPopularityResDto> getPopularRecruits();

    void updateRecruitable(Long recruitId, MemberIdReqDto reqDto);
}
