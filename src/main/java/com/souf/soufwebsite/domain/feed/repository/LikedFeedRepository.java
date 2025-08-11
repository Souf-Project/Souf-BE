package com.souf.soufwebsite.domain.feed.repository;

import com.souf.soufwebsite.domain.feed.entity.LikedFeed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikedFeedRepository extends JpaRepository<LikedFeed, Long> {

    Optional<LikedFeed> findByFeedIdAndMemberId(Long feedId, Long memberId);

    void deleteByFeedIdAndMemberId(Long feedId, Long memberId);

    Optional<Long> countByFeedId(Long feedId);

    boolean existsByFeedIdAndMemberId(Long feedId, Long memberId);
}
