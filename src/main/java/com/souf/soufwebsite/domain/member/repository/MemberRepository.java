package com.souf.soufwebsite.domain.member.repository;

import com.souf.soufwebsite.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

//    Page<Member> findByNicknameContainingIgnoreCase(String keyword, Pageable pageable);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);
}
