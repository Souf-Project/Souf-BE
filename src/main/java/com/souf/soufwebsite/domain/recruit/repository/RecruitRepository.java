package com.souf.soufwebsite.domain.recruit.repository;

import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecruitRepository extends JpaRepository<Recruit, Long>, RecruitCustomRepository {

    @Modifying
    @Query("update Recruit r set r.viewCount = r.viewCount + :count where r.id = :recruitId")
    void increaseViewCount(@Param("recruitId") Long recruitId, @Param("count") Long count);

    @Query("select r from Recruit r where r.recruitable=true and r.deadline > :now order by r.deadline limit 5")
    List<Recruit> findTop5ByRecruitableAndDeadlineAfterOrderByDeadlineAsc(@Param("now") LocalDateTime now);


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

    interface Elig {
        Boolean getRecruitable();
        LocalDateTime getDeadline();
    }

    @Query("select r.recruitable, r.deadline from Recruit r where r.id = :id")
    Optional<Elig> findEligibilityAndDeadline(@Param("id") Long id);

    @Query("""
        select count(r) from Recruit r
        where r.recruitable = true and r.deadline > :now
        and (r.deadline < :deadline or (r.deadline = :deadline and r.id < :id))
    """)
    long countEligibleBefore(@Param("now") LocalDateTime now,
                             @Param("deadline") LocalDateTime deadline,
                             @Param("id") Long id);

    default boolean isInTop5(Long id) {
        var now = LocalDateTime.now();
        var opt = findEligibilityAndDeadline(id);
        if (opt.isEmpty()) return false;

        var e = opt.get();
        if (!Boolean.TRUE.equals(e.getRecruitable())) return false;

        LocalDateTime deadline = e.getDeadline();
        if (deadline == null || !deadline.isAfter(now)) return false;

        long before = countEligibleBefore(now, deadline, id);
        return before + 1 <= 5;
    }

}
