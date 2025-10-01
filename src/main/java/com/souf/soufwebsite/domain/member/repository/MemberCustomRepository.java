package com.souf.soufwebsite.domain.member.repository;

import com.souf.soufwebsite.domain.member.dto.ResDto.AdminMemberResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberSimpleResDto;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberCustomRepository {
//    Page<Member> findByCategory(Long first, Pageable pageable);

    Page<MemberSimpleResDto> getMemberList(Long first, Long second, Long third,
                                           Pageable pageable);

    Page<AdminMemberResDto> getMemberListInAdmin(RoleType memberType, String username, String nickname, Pageable pageable);
}
