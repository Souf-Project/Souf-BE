package com.souf.soufwebsite.domain.recruit.entity;

import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import com.souf.soufwebsite.domain.user.entity.User;
import com.souf.soufwebsite.global.common.FirstCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecruitTest {

    @DisplayName("올바른 Recruit 객체를 설계합니다.")
    @Test
    void createRecruitObject(){
     //given
        RecruitReqDto reqDto = new RecruitReqDto("title", "content", "서울시 도봉구", "2025/03/24 10:30",
                "100만원", "우대사항1", FirstCategory.MUSIC);

     //when
        Recruit recruit = Recruit.of(reqDto, null);

     //then
        assertThat(recruit).isNotNull();
    }

}