package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import com.souf.soufwebsite.domain.recruit.dto.RecruitResDto;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.exception.NotFoundRecruitException;
import com.souf.soufwebsite.domain.recruit.exception.NotValidAuthenticationException;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.FirstCategory;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitServiceImpl implements RecruitService {

    private final RecruitRepository recruitRepository;

    private Member getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    @Override
    public void createRecruit(RecruitReqDto reqDto) {
        Member member = getCurrentUser();
        Recruit recruit = Recruit.of(reqDto, member);
        recruitRepository.save(recruit);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RecruitResDto> getRecruits(FirstCategory categoryName) {
        List<Recruit> recruits = recruitRepository.
                findAllByFirstCategoryOrderByIdDesc(categoryName);


        return recruits.stream().map(
                recruit -> RecruitResDto.from(recruit, recruit.getMember().getNickname()))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public RecruitResDto getRecruitById(Long recruitId) {
        Recruit recruit = findIfRecruitExist(recruitId);
        return RecruitResDto.from(recruit, recruit.getMember().getNickname());
    }

    @Transactional
    @Override
    public void updateRecruit(Long recruitId, RecruitReqDto reqDto) {
        Member member = getCurrentUser();
        Recruit recruit = findIfRecruitExist(recruitId);
        verifyIfRecruitIsMine(recruit, member);

        recruit.updateRecruit(reqDto);
    }

    @Override
    public void deleteRecruit(Long recruitId) {
        Member member = getCurrentUser();
        Recruit recruit = findIfRecruitExist(recruitId);
        verifyIfRecruitIsMine(recruit, member);

        recruitRepository.delete(recruit);
    }

    private void verifyIfRecruitIsMine(Recruit recruit, Member member) {
        if(!recruit.getMember().equals(member)){
            throw new NotValidAuthenticationException();
        }
    }

    private Recruit findIfRecruitExist(Long id) {
        return recruitRepository.findById(id).orElseThrow(NotFoundRecruitException::new);
    }
}
