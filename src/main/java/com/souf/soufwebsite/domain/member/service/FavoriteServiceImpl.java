package com.souf.soufwebsite.domain.member.service;

import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.dto.ReqDto.FavoriteMemberReqDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.domain.member.entity.FavoriteMember;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.exception.NotFoundFavoriteException;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.FavoriteMemberRepository;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final MemberRepository memberRepository;
    private final FavoriteMemberRepository favoriteMemberRepository;
    private final FileService fileService;

    @Transactional
    @Override
    public void createFavoriteMember(FavoriteMemberReqDto favoriteMemberReqDto) {
        Member fromMember = findIfExists(favoriteMemberReqDto.fromMemberId());
        Member toMember = findIfExists(favoriteMemberReqDto.toMemberId());

        FavoriteMember favoriteMember = FavoriteMember.of(fromMember, toMember);
        favoriteMemberRepository.save(favoriteMember);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MemberResDto> getFavoriteMember(Long fromMemberId, Pageable pageable) {
        Member fromMember = findIfExists(fromMemberId);
        Page<FavoriteMember> favoriteMembers = favoriteMemberRepository.findWithToMemberByFromMember(fromMember, pageable);

        return favoriteMembers.map(favoriteMember -> {
            Member toMember = favoriteMember.getToMember();
            String profileImageUrl = fileService.getMediaUrl(PostType.PROFILE, toMember.getId());
            return MemberResDto.from(toMember, null, profileImageUrl, false);
        });
    }

    @Override
    public void deleteFavoriteMember(FavoriteMemberReqDto favoriteMemberReqDto) {
        Member fromMember = findIfExists(favoriteMemberReqDto.fromMemberId());
        Member toMember = findIfExists(favoriteMemberReqDto.toMemberId());
        FavoriteMember favoriteMember = favoriteMemberRepository.findByFromMemberAndToMember(fromMember, toMember)
                .orElseThrow(NotFoundFavoriteException::new);

        favoriteMemberRepository.delete(favoriteMember);
    }

    private Member findIfExists(Long fromMemberId) {
        return memberRepository.findById(fromMemberId).orElseThrow(NotFoundMemberException::new);
    }
}
