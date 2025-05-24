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

public interface FeedRepository extends JpaRepository<Feed, Long> {

    Page<Feed> findAllByMemberOrderByIdDesc(Member member, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Feed f set f.viewCount = f.viewCount + :count where f.id = :feedId")
    void increaseViewCount(@Param("feedId") Long feedId, @Param("count") Long count);

    Page<Feed> findByOrderByViewCountDesc(Pageable pageable);
}

