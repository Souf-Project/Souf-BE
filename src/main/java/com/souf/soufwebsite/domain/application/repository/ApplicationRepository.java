package com.souf.soufwebsite.domain.application.repository;

import com.souf.soufwebsite.domain.application.entity.Application;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByMemberAndRecruit(Member member, Recruit recruit);
    Page<Application> findByMember(Member member, Pageable pageable);
    Page<Application> findByRecruit(Recruit recruit, Pageable pageable);

    Optional<Application> findByMemberAndRecruit(Member member, Recruit recruit);
    Optional<Application> findByIdAndRecruit_Id(Long applicationId, Long recruitId);
}
