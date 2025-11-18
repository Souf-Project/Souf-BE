package com.souf.soufwebsite.domain.recruit.contract.dto.res;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.profile.CompanyProfile;

public record PreviewOrdererInfoRes(
        String companyName,
        String ordererName,
        String businessRegistrationNumber,
        String roadAddress,
        String contactPhone,
        String contactEmail
) {
    public static PreviewOrdererInfoRes from(Member member, CompanyProfile companyProfile) {

        if(companyProfile == null){
            return new PreviewOrdererInfoRes(
                    "",
                    member.getUsername(),
                    "",
                    "",
                    member.getPhoneNumber(),
                    member.getEmail()
            );
        }

        return new PreviewOrdererInfoRes(
                companyProfile.getCompanyName(),
                member.getUsername(),
                companyProfile.getBusinessRegistrationNumber(),
                companyProfile.getRoadNameAddress(),
                member.getPhoneNumber(),
                member.getEmail()
        );
    }
}
