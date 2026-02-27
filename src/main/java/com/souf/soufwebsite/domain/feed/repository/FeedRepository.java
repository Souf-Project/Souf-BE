package com.souf.soufwebsite.domain.feed.repository;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.member.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedCustomRepository {
    Slice<Feed> findAllByMemberOrderByIdDesc(Member member, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Feed f set f.weeklyViews = 0")
    void updateWeeklyViews();

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
    @EntityGraph(attributePaths = "member")
    @Query("""
        SELECT f
        FROM Feed f
        JOIN f.member m
        WHERE (:nickname IS NULL OR m.nickname = :nickname)
          AND (:title IS NULL OR f.topic = :title)
        ORDER BY f.createdTime DESC, f.id DESC
    """)
    Page<Feed> findByMemberAndTopic(
            @Param("nickname") String nickname,
            @Param("title") String title,
            Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Feed f set f.likedCount = f.likedCount + 1 where f.id = :feedId")
    int incrementLikedCount(Long feedId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Feed f set f.likedCount = f.likedCount - 1 where f.id = :feedId and f.likedCount > 0")
    int decrementLikedCount(Long feedId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Feed f set f.commentCount = f.commentCount + 1 where f.id = :feedId")
    int incrementCommentCount(@Param("feedId") Long feedId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Feed f set f.commentCount = f.commentCount - 1 where f.id = :feedId and f.commentCount > 0")
    int decrementCommentCount(@Param("feedId") Long feedId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Feed f
           set f.commentCount = case
               when f.commentCount >= :count then f.commentCount - :count
               else 0
           end
         where f.id = :feedId
    """)
    int decrementCommentCountBy(@Param("feedId") Long feedId, @Param("count") int count);
}

