package com.souf.soufwebsite.domain.feed.repository.likedFeed;

import com.souf.soufwebsite.domain.feed.entity.LikedFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikedFeedRepository extends JpaRepository<LikedFeed, Long>, LikedFeedCustomRepository {

    Optional<LikedFeed> findByFeedIdAndMemberId(Long feedId, Long memberId);

    void deleteByFeedIdAndMemberId(Long feedId, Long memberId);

    Optional<Long> countByFeedId(Long feedId);

    boolean existsByFeedIdAndMemberId(Long feedId, Long memberId);

    void deleteAllByMemberId(Long memberId);

    @Query("""
        select lf.feedId
          from LikedFeed lf
         where lf.memberId = :memberId
           and lf.feedId in :feedIds
    """)
    List<Long> findLikedFeedIds(@Param("memberId") Long memberId,
                                @Param("feedIds") List<Long> feedIds);

}
