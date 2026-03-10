package com.souf.soufwebsite.domain.recruit.repository;

import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    // 관리자 페이지 공고문 조회
    @EntityGraph(attributePaths = "member")
    @Query("""
        SELECT r
        FROM Recruit r
        JOIN r.member m
        WHERE (:nickname IS NULL OR m.nickname = :nickname)
          AND (:title IS NULL OR r.title = :title)
        ORDER BY r.createdTime DESC, r.id DESC
    """)
    Page<Recruit> findByMemberAndTopic(
            @Param("nickname") String nickname,
            @Param("title") String title,
            Pageable pageable);

    @Query("""
       select r.id from Recruit r
       where r.recruitable = true and r.deadline > :now
       order by r.deadline asc, r.createdTime asc, r.id asc
    """)
    List<Long> findTopIds(@Param("now") LocalDateTime now, Pageable pageable);

    default boolean isInTop5(Long id) {
        LocalDateTime now = LocalDateTime.now();

        List<Long> top5 = findTopIds(now, PageRequest.of(0, 5));
        return top5.contains(id);
    }

    @Query("select r.id from Recruit r where r.id in :ids")
    List<Long> findExistingIdsIncludingDeleted(@Param("ids") List<Long> ids);

    @Query("select r.id from Recruit r where r.id in :ids and r.isDeleted = true")
    List<Long> findDeletedIds(@Param("ids") List<Long> ids);

    @Query("select r.id from Recruit r where r.id in :ids and r.isDeleted = false")
    List<Long> findNotDeletedIds(@Param("ids") List<Long> ids);

    @Query("select r from Recruit r where r.id in :ids")
    List<Recruit> findAllByIdsIncludingDeleted(@Param("ids") List<Long> ids);
}
