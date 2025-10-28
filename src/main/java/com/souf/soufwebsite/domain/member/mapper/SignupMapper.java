package com.souf.soufwebsite.domain.member.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.souf.soufwebsite.domain.member.dto.reqDto.signup.MajorReqDto;
import com.souf.soufwebsite.domain.member.entity.profile.Specialty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SignupMapper {

    private final ObjectMapper objectMapper;

    public List<Specialty> toSpecialtyList(List<MajorReqDto> majorReqDtos) {
        if(majorReqDtos == null || majorReqDtos.isEmpty()){
            return List.of();
        }

        return majorReqDtos.stream()
                .map(m -> new Specialty(m.specialtyName(), m.specialty()))
                .collect(Collectors.toList());
    }
}
