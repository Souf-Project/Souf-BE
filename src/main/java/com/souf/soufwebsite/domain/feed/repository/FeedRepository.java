package com.souf.soufwebsite.domain.feed.repository;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    Page<Feed> findAllByMemberOrderByIdDesc(Member member, Pageable pageable);

    @Modifying
    @Query("update Feed f set f.viewCount = f.viewCount + :count where f.id = :feedId")
    void increaseViewCount(@Param("feedId") Long feedId, @Param("count") Long count);
}

