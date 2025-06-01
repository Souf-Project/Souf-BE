package com.souf.soufwebsite.domain.recruit.repository;

import com.souf.soufwebsite.domain.recruit.dto.RecruitSimpleResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruitCustomRepository {

    Page<RecruitSimpleResDto> getRecruitList(Long first, Pageable pageable);
}
