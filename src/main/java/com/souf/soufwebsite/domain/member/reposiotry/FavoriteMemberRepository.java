package com.souf.soufwebsite.domain.member.reposiotry;

import com.souf.soufwebsite.domain.member.entity.FavoriteMember;
import com.souf.soufwebsite.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteMemberRepository extends JpaRepository<FavoriteMember, Long> {

    @Query("SELECT fm FROM FavoriteMember fm JOIN FETCH fm.toMember WHERE fm.fromMember = :from order by fm.createdTime")
    Page<FavoriteMember> findWithToMemberByFromMember(@Param("from") Member from, Pageable pageable);
}
