package com.souf.soufwebsite.domain.recruit.repository;

import com.souf.soufwebsite.domain.recruit.dto.RecruitResDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitSimpleResDto;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface RecruitCustomRepository {

    List<RecruitSimpleResDto> getRecruitList(Long first, Long second, Long third);
}
