package com.souf.soufwebsite.domain.member.service;

import com.souf.soufwebsite.domain.member.repository.MemberCategoryMappingRepository;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.global.common.category.service.CategoryService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MemberCategoryServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberCategoryMappingRepository mappingRepository;
    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private MemberCategoryServiceImpl memberCategoryService;

    public MemberCategoryServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void 멤버_카테고리_단건_추가() {
//        // given
//        Long memberId = 1L;
//        CategoryDto dto = new CategoryDto(10L, 20L, 30L);
//
//        Member member = new Member("test@test.com", "pw123456", "username", "nickname");
//        FirstCategory first = mock(FirstCategory.class);
//        SecondCategory second = mock(SecondCategory.class);
//        ThirdCategory third = mock(ThirdCategory.class);
//
//        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(member));
//        when(categoryService.findIfFirstIdExists(10L)).thenReturn(first);
//        when(categoryService.findIfSecondIdExists(20L)).thenReturn(second);
//        when(categoryService.findIfThirdIdExists(30L)).thenReturn(third);
//
//        // when
//        memberCategoryService.addCategory(memberId, dto);
//
//        // then
//        verify(mappingRepository, times(1)).save(any(MemberCategoryMapping.class));
//    }
}