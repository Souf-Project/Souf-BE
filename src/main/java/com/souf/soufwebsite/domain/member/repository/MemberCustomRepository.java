package com.souf.soufwebsite.domain.member.repository;

import com.souf.soufwebsite.domain.member.dto.ReqDto.MemberSearchReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberSimpleResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberCustomRepository {
//    Page<Member> findByCategory(Long first, Pageable pageable);

    Page<MemberSimpleResDto> getMemberList(Long first, Long second, Long third,
                                           MemberSearchReqDto searchReqDto, Pageable pageable);
}
