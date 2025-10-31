package com.souf.soufwebsite.domain.member.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.exception.NotValidFileTypeException;
import com.souf.soufwebsite.domain.file.service.S3UploaderService;
import com.souf.soufwebsite.domain.member.dto.reqDto.signup.*;
import com.souf.soufwebsite.domain.member.entity.ApprovedStatus;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.profile.ClubProfile;
import com.souf.soufwebsite.domain.member.entity.profile.CompanyProfile;
import com.souf.soufwebsite.domain.member.entity.profile.Specialty;
import com.souf.soufwebsite.domain.member.entity.profile.StudentProfile;
import com.souf.soufwebsite.domain.member.exception.NotValidRoleTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class SignupMapper {

    private final ObjectMapper objectMapper;
    private final S3UploaderService s3UploaderService;

    public List<Specialty> toSpecialtyList(StudentProfile studentProfile, List<MajorReqDto> majorReqDtos) {
        if(majorReqDtos == null || majorReqDtos.isEmpty()){
            return List.of();
        }

        return majorReqDtos.stream()
                .map(m -> {
                    Specialty specialty = new Specialty(m.specialtyName(), m.specialty());
                    studentProfile.addSpecialty(specialty);
                    return specialty;
                })
                .collect(Collectors.toList());
    }

    public PresignedUrlResDto signupByRole(Member member, SignupReqDto reqDto) {

        PresignedUrlResDto presignedUrlResDto = new PresignedUrlResDto("", "", "");
        switch (reqDto.roleType()) {
            case STUDENT -> {
                StudentSignupReqDto s = (StudentSignupReqDto) reqDto;

                StudentProfile studentProfile = new StudentProfile(s);
                toSpecialtyList(studentProfile, s.getMajorReqDtos());

                if (s.getSchoolAuthenticatedImageFileName() != null) {
                    presignedUrlResDto = s3UploaderService.generatePresignedUploadUrl("profile/authentication", s.getSchoolAuthenticatedImageFileName());
                }

                member.attachStudentProfile(studentProfile);
            }
            case CLUB -> {
                ClubSignupReqDto c = (ClubSignupReqDto) reqDto;

                if(c.getClubAuthenticationMethod() != null){
                    ClubProfile clubProfile = new ClubProfile(c);

                    member.attachClubProfile(clubProfile);
                    if(c.getIntro() != null) {
                        member.updateIntroduction(c.getIntro());
                    }
                }
            }
            case MEMBER -> {
                CompanySignupReqDto co = (CompanySignupReqDto) reqDto;
                if(co.getIsCompany().equals(Boolean.TRUE) && co.getBusinessRegistrationNumber() != null){
                    CompanyProfile companyProfile = new CompanyProfile(co);

                    if (co.getBusinessRegistrationFile() != null) {
                        if(co.getBusinessRegistrationFile().endsWith(".pdf"))
                            presignedUrlResDto = s3UploaderService.generatePresignedUploadUrl("profile/authentication", co.getBusinessRegistrationFile());
                        else throw new NotValidFileTypeException();
                    }

                    member.attachCompanyProfile(companyProfile);
                }
                else
                    member.updateApprovedStatus(ApprovedStatus.APPROVED);
            }
            default -> throw new NotValidRoleTypeException();
        }

        return presignedUrlResDto;
    }
}
