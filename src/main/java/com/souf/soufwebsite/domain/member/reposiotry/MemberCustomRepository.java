package com.souf.soufwebsite.domain.member.reposiotry;

import com.souf.soufwebsite.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberCustomRepository {
    Page<Member> searchMembers(String keyword, Pageable pageable);
}
