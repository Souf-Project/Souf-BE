package com.souf.soufwebsite.domain.recruit.contract.dto.res;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.profile.StudentProfile;

public record PreviewBeneficiaryInfoRes(
        String beneficiaryName,
        String schoolName,
        String email,
        String phoneNumber
) {
    public static PreviewBeneficiaryInfoRes of(Member member, StudentProfile studentProfile) {

        if(studentProfile == null){
            return new PreviewBeneficiaryInfoRes(
                    member.getUsername(),
                    "",
                    member.getEmail() == null ? "" : member.getEmail(),
                    member.getPhoneNumber() == null ? "" : member.getPhoneNumber()
            );
        }

        return new PreviewBeneficiaryInfoRes(
                member.getUsername(),
                studentProfile.getSchoolName(),
                member.getEmail(),
                member.getPhoneNumber()
        );
    }
}
