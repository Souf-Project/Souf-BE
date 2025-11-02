package com.souf.soufwebsite.domain.member.repository;

import com.souf.soufwebsite.domain.member.entity.profile.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
}
