package com.souf.soufwebsite.domain.member.reposiotry;

import com.souf.soufwebsite.domain.member.entity.MemberCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCategoryMappingRepository extends JpaRepository<MemberCategoryMapping, Long> {
}
