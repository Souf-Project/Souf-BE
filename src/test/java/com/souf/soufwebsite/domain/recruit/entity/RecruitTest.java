package com.souf.soufwebsite.domain.recruit.entity;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class RecruitTest {

    @DisplayName("올바른 Recruit 객체를 설계합니다.")
    @Test
    void createRecruitObject(){
     //given
        Member member = Mockito.mock(Member.class);
        RecruitReqDto reqDto = new RecruitReqDto("title", "content", "서울시 도봉구", "2025/03/24 10:30",
                "100만원", "우대사항1", FirstCategory.MUSIC);

     //when
        Recruit recruit = Recruit.of(reqDto, member);

     //then
        assertThat(recruit).isNotNull();
        assertThat(recruit.getTitle()).isEqualTo("title");
        assertThat(recruit.getRegion()).isEqualTo("서울시 도봉구");

    }

}