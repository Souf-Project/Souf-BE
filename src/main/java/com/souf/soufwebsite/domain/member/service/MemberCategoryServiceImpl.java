package com.souf.soufwebsite.domain.member.service;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberCategoryMapping;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberCategoryMappingRepository;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.entity.SecondCategory;
import com.souf.soufwebsite.global.common.category.entity.ThirdCategory;
import com.souf.soufwebsite.global.common.category.exception.NotFoundCategoryMappingException;
import com.souf.soufwebsite.global.common.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberCategoryServiceImpl implements MemberCategoryService {

    private final MemberRepository memberRepository;
    private final MemberCategoryMappingRepository mappingRepository;
    private final CategoryService categoryService;

    @Override
    @Transactional
    public void addCategory(Long memberId, CategoryDto dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        FirstCategory first = categoryService.findIfFirstIdExists(dto.firstCategory());
        SecondCategory second = categoryService.findIfSecondIdExists(dto.secondCategory());
        ThirdCategory third = categoryService.findIfThirdIdExists(dto.thirdCategory());
        categoryService.validate(first.getId(), second.getId(), third.getId());

        MemberCategoryMapping mapping = MemberCategoryMapping.of(member, first, second, third);
        member.addCategory(mapping);
        mappingRepository.save(mapping);
    }

    @Override
    @Transactional
    public void removeCategory(Long memberId, CategoryDto dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        member.removeCategory(dto);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void updateCategory(Long memberId, CategoryDto oldDto, CategoryDto newDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        FirstCategory first = categoryService.findIfFirstIdExists(newDto.firstCategory());
        SecondCategory second = categoryService.findIfSecondIdExists(newDto.secondCategory());
        ThirdCategory third = categoryService.findIfThirdIdExists(newDto.thirdCategory());
        categoryService.validate(first.getId(), second.getId(), third.getId());

        boolean exists = member.getCategories().stream()
                .anyMatch(mapping ->
                        mapping.getFirstCategory().getId().equals(oldDto.firstCategory()) &&
                                mapping.getSecondCategory().getId().equals(oldDto.secondCategory()) &&
                                mapping.getThirdCategory().getId().equals(oldDto.thirdCategory())
                );
        if (!exists) {
            throw new NotFoundCategoryMappingException();
        }

        MemberCategoryMapping newMapping = MemberCategoryMapping.of(member, first, second, third);
        member.updateCategory(oldDto, newMapping);
        mappingRepository.save(newMapping);
    }

    @Override
    @Transactional
    public List<CategoryDto> getCategoriesOfMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        return member.getCategories().stream()
                .map(mapping -> new CategoryDto(
                        mapping.getFirstCategory().getId(),
                        mapping.getSecondCategory().getId(),
                        mapping.getThirdCategory().getId()
                ))
                .toList();
    }
}