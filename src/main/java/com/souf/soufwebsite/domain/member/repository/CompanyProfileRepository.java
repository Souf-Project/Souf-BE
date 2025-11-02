package com.souf.soufwebsite.domain.member.repository;

import com.souf.soufwebsite.domain.member.entity.profile.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {
}
