package com.souf.soufwebsite.domain.member.dto.resDto.info;


import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberCategoryMapping;
import com.souf.soufwebsite.domain.member.entity.profile.CompanyProfile;
import com.souf.soufwebsite.domain.member.entity.profile.StudentProfile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberResAssembler {

    public static MemberInfoResDto<? extends MemberInfo> from(Member m, List<MemberCategoryMapping> categories, String profileImageUrl) {

        return switch (m.getRole()) {
            case STUDENT -> {
                StudentProfile sp = m.getStudentProfile();
                if (sp == null) {
                    yield MemberInfoResDto.from(m, categories, profileImageUrl, null);
                }

                List<SpecialtyInfo> specialties = List.of();
                if (sp.getSpecialties() != null) {
                    specialties = sp.getSpecialties().stream()
                            .map(s -> new SpecialtyInfo(
                                    s.getSpecialtyName(),
                                    s.getSpecialtyType()
                            ))
                            .collect(Collectors.toList());
                }

                yield MemberInfoResDto.from(m, categories, profileImageUrl,
                        new StudentInfo(
                                sp.getSchoolName(),
                                sp.getEducationType(),
                                sp.getSchoolEmail(),
                                specialties
                        ));
            }
            case MEMBER -> {
                CompanyProfile cp = m.getCompanyProfile();
                yield (cp == null)
                        ? MemberInfoResDto.from(m, categories, profileImageUrl, null)
                        : MemberInfoResDto.from(m, categories, profileImageUrl,
                        new CompanyInfo(
                                cp.getCompanyName(),
                                cp.getBusinessRegistrationNumber(),
                                cp.getZipCode(),
                                cp.getRoadNameAddress(),
                                cp.getDetailedAddress(),
                                cp.getBusinessStatus(),
                                cp.getBusinessClassification())
                );
            }
            case CLUB, ADMIN -> MemberInfoResDto.from(m, categories, profileImageUrl, null);

        };
    }
}