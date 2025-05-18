package com.souf.soufwebsite.domain.file.repository;

import com.souf.soufwebsite.domain.file.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
}