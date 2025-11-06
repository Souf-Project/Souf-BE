package com.souf.soufwebsite.domain.member.mapper;

import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.exception.NotValidFileTypeException;
import com.souf.soufwebsite.domain.file.service.S3UploaderService;
import com.souf.soufwebsite.domain.member.dto.reqDto.addInfo.AddCompanyInfoReqDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.addInfo.AddStudentInfoReqDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.signup.MajorReqDto;
import com.souf.soufwebsite.domain.member.entity.ApprovedStatus;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.profile.CompanyProfile;
import com.souf.soufwebsite.domain.member.entity.profile.Specialty;
import com.souf.soufwebsite.domain.member.entity.profile.StudentProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AddInfoMapper {

    private final S3UploaderService s3UploaderService;

    private List<Specialty> toSpecialtyList(StudentProfile studentProfile, List<MajorReqDto> majorReqDtos) {
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

    public PresignedUrlResDto mapStudentAddInfo(Member member, AddStudentInfoReqDto reqDto) {
        PresignedUrlResDto presignedUrlResDto = new PresignedUrlResDto("", "", "");

        member.setPhoneNumberIfAbsent(reqDto.phoneNumber());
        StudentProfile studentProfile = new StudentProfile(reqDto);
        toSpecialtyList(studentProfile, reqDto.majorReqDtos());

        if (reqDto.schoolAuthenticatedImageFileName() != null && !reqDto.schoolAuthenticatedImageFileName().isEmpty()) {
            presignedUrlResDto = s3UploaderService.generatePresignedUploadUrl("profile/authentication", reqDto.schoolAuthenticatedImageFileName());
        }

        member.attachStudentProfile(studentProfile);

        member.updateApprovedStatus(ApprovedStatus.PENDING);

        return presignedUrlResDto;
    }

    public PresignedUrlResDto mapCompanyAddInfo(Member member, AddCompanyInfoReqDto reqDto) {
        PresignedUrlResDto presignedUrlResDto = new PresignedUrlResDto("", "", "");

        member.setPhoneNumberIfAbsent(reqDto.phoneNumber());
        if(reqDto.isCompany().equals(Boolean.TRUE) && reqDto.businessRegistrationNumber() != null){
            CompanyProfile companyProfile = new CompanyProfile(reqDto);

            if (reqDto.businessRegistrationFile() != null) {
                if(reqDto.businessRegistrationFile().endsWith(".pdf"))
                    presignedUrlResDto = s3UploaderService.generatePresignedUploadUrl("profile/authentication", reqDto.businessRegistrationFile());
                else throw new NotValidFileTypeException();
            }

            member.attachCompanyProfile(companyProfile);

            member.updateApprovedStatus(ApprovedStatus.PENDING);
        }
        else
            member.updateApprovedStatus(ApprovedStatus.APPROVED);
        return presignedUrlResDto;
    }
}