package com.souf.soufwebsite.domain.file.repository;

import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.entity.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    @Query("select m from Media m where m.postType = :postType and m.postId = :postId")
    List<Media> findByPostTypeAndPostId(PostType postType, Long postId);

    @Modifying
    @Query("delete from Media m where m.postType = :postType and m.postId = :postId")
    void deleteAllByPostTypeAndPostId(PostType postType, Long postId);

    Media findByOriginalUrlContains(String originalUrl);
}