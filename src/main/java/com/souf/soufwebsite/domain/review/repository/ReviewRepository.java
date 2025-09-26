package com.souf.soufwebsite.domain.review.repository;

import com.souf.soufwebsite.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Modifying
    @Query("update Review r set r.viewTotalCount = r.viewTotalCount + :count where r.id = :reviewId")
    void increaseViewCount(@Param("reviewId") Long reviewId, @Param("count") Long count);
}
