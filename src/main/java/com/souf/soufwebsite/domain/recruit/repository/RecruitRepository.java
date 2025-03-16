package com.souf.soufwebsite.domain.recruit.repository;

import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.global.common.FirstCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {

    List<Recruit> findAllByFirstCategoryOrderByIdDesc(FirstCategory firstCategory);
}
