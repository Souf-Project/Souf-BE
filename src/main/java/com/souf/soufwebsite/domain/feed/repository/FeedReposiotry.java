package com.souf.soufwebsite.domain.feed.repository;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedReposiotry extends JpaRepository<Feed, Long> {

    List<Feed> findAllByOrderByIdDesc();
}
