package com.souf.soufwebsite.domain.member.repository;

import com.souf.soufwebsite.domain.member.entity.profile.ClubProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubProfileRepository extends JpaRepository<ClubProfile, Long> {
}
