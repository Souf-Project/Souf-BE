package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.global.common.FirstCategory;
import com.souf.soufwebsite.global.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.ExpectedCount.times;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruitServiceImplTest {

    @InjectMocks
    private RecruitServiceImpl recruitService;

    @Mock
    private RecruitRepository recruitRepository;

    private Recruit recruit;
    private Member member;

    @BeforeEach
    void setUp() {
        member = Mockito.mock(Member.class);
        recruit = Mockito.mock(Recruit.class);
        ReflectionTestUtils.setField(member, "id", 1L);
        ReflectionTestUtils.setField(recruit, "id", 1L);
    }

    @DisplayName("공고문을 성공적으로 생성합니다.")
    @Test
    void createRecruit(){
        RecruitReqDto reqDto = new RecruitReqDto("title1", "content1", "서울시 도봉구", "2025/03/24 10:30",
                "100만원", "우대사항1", FirstCategory.MUSIC);

        given(recruitRepository.save(any(Recruit.class))).willAnswer(invocation -> invocation.getArgument(0));

        //when
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentMember).thenReturn(member);

            // Recruit.of()는 static이 아닌 정적 팩토리 메서드이므로 stub 가능
            mockStatic(Recruit.class).when(() -> Recruit.of(reqDto, member)).thenReturn(recruit);

            // when
            recruitService.createRecruit(reqDto);

            // then
            verify(recruitRepository, Mockito.times(1)).save(recruit);
        }
    }
}