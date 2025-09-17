package com.souf.soufwebsite.domain.recruit.repository;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.dto.req.MyRecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.req.RecruitSearchReqDto;
import com.souf.soufwebsite.domain.recruit.dto.res.MyRecruitResDto;
import com.souf.soufwebsite.domain.recruit.dto.res.RecruitSimpleResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruitCustomRepository {

    Page<RecruitSimpleResDto> getRecruitList(Long first, Long second, Long third,
                                             RecruitSearchReqDto searchReqDto, Pageable pageable);

    Page<MyRecruitResDto> getMyRecruits(Member me, MyRecruitReqDto reqDto, Pageable pageable);
}
