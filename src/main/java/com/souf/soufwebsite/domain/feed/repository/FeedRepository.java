package com.souf.soufwebsite.domain.feed.repository;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.member.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedCustomRepository {

    Page<Feed> findAllByMemberOrderByIdDesc(Member member, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Feed f set f.viewCount = f.viewCount + :count where f.id = :feedId")
    void increaseTotalViewCount(@Param("feedId") Long feedId, @Param("count") Long count);

    @Transactional
    @Modifying
    @Query("update Feed f set f.weeklyViews = f.weeklyViews + :count where f.id = :feedId")
    void increaseWeeklyViewCount(@Param("feedId") Long feedId, @Param("count") Long count);

    @Query("select f from Feed f join fetch f.member m order by f.weeklyViews desc limit 6")
    List<Feed> findTop6ByOrderByWeeklyViewCountDesc();

    List<Feed> findTop3ByMemberOrderByViewCountDesc(Member member);

    // 관리자 페이지 피드 조회
    @Query(
            value = """
                SELECT f
                FROM Feed f
                    JOIN FETCH f.member m
                WHERE (:nickname is null or m.nickname = :nickname)
                      and (:title is null or f.topic = :title)
        """,
            countQuery = """
        SELECT COUNT(f)
        FROM Feed f
        JOIN f.member m
         WHERE (:nickname is null or m.nickname = :nickname)
                      and (:title is null or f.topic = :title)
        """
    )
    Page<Feed> findByMemberAndTopic(
            @Param("nickname") String nickname,
            @Param("title") String title,
            Pageable pageable);
}

