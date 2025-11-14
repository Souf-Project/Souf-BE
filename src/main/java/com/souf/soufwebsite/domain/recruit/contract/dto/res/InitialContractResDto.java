package com.souf.soufwebsite.domain.recruit.contract.dto.res;

import com.souf.soufwebsite.domain.recruit.contract.entity.Contract;
import com.souf.soufwebsite.domain.recruit.contract.entity.Project;

import java.time.LocalDate;

public record InitialContractResDto(

        String contractUuid,
        String ordererName,
        String companyName,
        String contactPhone,
        String contactEmail,
        String managerWithPosition,

        String projectName,
        String projectStartDate,
        String projectEndDate,
        Long totalContractAmount,
        Double downPaymentPercentage,
        Long downPaymentAmount,
        Integer inspectionDays,

        Boolean copyrightApproved,
        String confidentialityPeriod,
        String warrantyPeriod,
        String competentCourt

) {

    public static InitialContractResDto of(Contract contract, Project project) {
        return new InitialContractResDto(
                contract.getContractUuid(),
                screenUsername(contract.getOrdererName()),
                contract.getCompanyName(),
                contract.getContactPhone(),
                contract.getContactEmail(),
                contract.getManagerWithPosition(),

                project.getProjectName(),
                convertLocalDateToString(project.getProjectStartDate()),
                convertLocalDateToString(project.getProjectEndDate()),
                project.getTotalContractAmount(),
                project.getDownPaymentPercentage(),
                project.getDownPaymentAmount(),
                project.getInspectionDays(),

                contract.getCopyrightApproved(),
                contract.getConfidentialityPeriod(),
                contract.getWarrantyPeriod(),
                contract.getCompetentCourt()
        );
    }

    private static String screenUsername(String username){

        return username.charAt(0) + "**";
    }

    private static String convertLocalDateToString(LocalDate date) {
        return date.toString();
    }
}
