package com.souf.soufwebsite.domain.feed.repository;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    Page<Feed> findAllByMemberOrderByIdDesc(Member member, Pageable pageable);
}

