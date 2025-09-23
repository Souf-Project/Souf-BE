package com.souf.soufwebsite.domain.review.repository;

import com.souf.soufwebsite.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
