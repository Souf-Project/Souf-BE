package com.souf.soufwebsite.domain.recruit.repository;

import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecruitRepository extends JpaRepository<Recruit, Long>, RecruitCustomRepository {

    @Modifying
    @Query("update Recruit r set r.viewCount = r.viewCount + :count where r.id = :recruitId")
    void increaseViewCount(@Param("recruitId") Long recruitId, @Param("count") Long count);
}
