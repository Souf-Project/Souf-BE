package com.souf.soufwebsite.domain.recruit.repository;

import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RecruitRepository extends JpaRepository<Recruit, Long>, RecruitCustomRepository {

    @Modifying
    @Query("update Recruit r set r.viewCount = r.viewCount + :count where r.id = :recruitId")
    void increaseViewCount(@Param("recruitId") Long recruitId, @Param("count") Long count);

    @Query("select r from Recruit r where r.recruitable=true and r.deadline > :now order by r.deadline limit 5")
    List<Recruit> findTop5ByRecruitableAndDeadlineAfterOrderByDeadlineDesc(@Param("now") LocalDateTime now);


    List<Recruit> findByRecruitableTrue();

    // 관리자 페이지 피드 조회
    @Query(
            value = """
                SELECT r
                FROM Recruit r
                    JOIN FETCH r.member m
                 WHERE (:nickname is null or m.nickname = :nickname)
                      and (:title is null or r.title = :title)
        """,
            countQuery = """
        SELECT COUNT(r)
        FROM Recruit r
        JOIN r.member m
        WHERE (:nickname is null or m.nickname = :nickname)
                      and (:title is null or r.title = :title)
        """
    )
    Page<Recruit> findByMemberAndTopic(
            @Param("nickname") String nickname,
            @Param("title") String title,
            Pageable pageable);
}
