package com.souf.soufwebsite.domain.file.repository;

import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.global.common.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    @Query("select m from Media m where m.postType = :postType and m.postId = :postId")
    List<Media> findByPostTypeAndPostId(PostType postType, Long postId);

    @Query("""
    SELECT m
    FROM Media m
    WHERE m.postType = :postType
      AND m.postId IN :postIds
    ORDER BY m.id ASC
    """)
    List<Media> findByPostTypeAndPostIdIn(
            @Param("postType") PostType postType, @Param("postIds") List<Long> postIds
    );

    @Modifying
    @Query("delete from Media m where m.postType = :postType and m.postId = :postId")
    void deleteAllByPostTypeAndPostId(PostType postType, Long postId);

    boolean existsByPostTypeAndPostIdAndOriginalUrl(PostType postType, Long postId, String originalUrl);

    List<Media> findAllByPostTypeAndPostId(PostType postType, Long postId);

    Optional<Media> findFirstByOriginalUrlEndingWithIgnoreCase(String originalUrl);
}