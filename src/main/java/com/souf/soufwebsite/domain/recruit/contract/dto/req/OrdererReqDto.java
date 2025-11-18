package com.souf.soufwebsite.domain.recruit.contract.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record OrdererReqDto(

        @Valid
        OrdererPersonalInfoReqDto ordererPersonalInfoReqDto,

        @Schema(description = "외주 작업명을 기입해주세요.", example = "iOS/Android 앱 프로토타입 제작 및 백엔드 연동")
        @NotBlank(message = "작업명은 필수입니다.")
        String projectName,

        @Schema(description = "작업 시작 날짜를 기입해주세요.", example = "2025-11-25")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "외주 시작 날짜는 필수입니다.")
        LocalDate projectStartDate,

        @Schema(description = "작업 종료 날짜를 기입해주세요.", example = "2026-03-30")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @FutureOrPresent(message = "외주 종료 날짜는 미래나 현재여야 합니다.")
        @NotNull
        LocalDate projectEndDate,

        @Schema(description = "작업 총 진행일을 기입해주세요.", example = "53")
        @PositiveOrZero(message = "진행일은 반드시 양수 또는 0이어야 합니다.")
        @NotNull
        Integer projectProgressingDays,

        @Schema(description = "총 계약 금액을 기입해주세요.", example = "500000")
        @Positive(message = "총 계약 금액은 반드시 양수여야 합니다.")
        @NotNull(message = "총 계약 금액은 필수입니다.")
        Long totalContractAmount,
        @Schema(description = "선금 지급 비율을 소수점으로 기입해주세요.", example = "20.0")
        @PositiveOrZero(message = "선금 지급 비율은 반드시 양수 또는 0이어야 합니다.")
        @NotNull(message = "선금 지급 비율은 필수입니다.")
        Double downPaymentPercentage,
        @Schema(description = "선금 급액을 기입해주세요.", example = "100000")
        @PositiveOrZero(message = "선금 금액은 반드시 양수 또는 0이어야 합니다.")
        @NotNull(message = "선금 금액은 필수입니다.")
        Long downPaymentAmount,

        @Schema(description = "검수 기한을 기입해주세요.", example = "5")
        @PositiveOrZero(message = "검수 기한은 양수 또는 0이어야 합니다.")
        @NotNull(message = "검수 기한은 필수입니다.")
        Integer inspectionDays,

        @Schema(description = "저작권/지식 재산권 허용 여부는 기본값 TRUE입니다.", example = "FALSE")
        @NotNull(message = "저작권/지식 재산권 허용 여부는 필수입니다.")
        Boolean copyrightApproved,

        @Schema(description = "비밀유지 기간은 단위와 함께 문자열로 전송해주세요.", example = "3일 or 3개월 or 1년")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Future(message = "외주 종료 날짜는 미래나 현재여야 합니다.")
        @NotNull(message = "비밀유지 기간은 필수입니다.")
        LocalDate confidentialityPeriod,

        @Schema(description = "보증 및 유지보수 기간은 단위와 함께 문자열로 전송해주세요.", example = "3일 or 3개월 or 1년")
        @NotBlank(message = "보증 및 유지보수 기간은 필수입니다.")
        String warrantyPeriod,

        @Schema(description = "관할 법원을 기입해주세요. 기본값은 서울중앙지방법원으로 설정해주세요.", example = "서울중앙지방법원")
        @NotBlank(message = "관할법원은 필수입니다. 기본값: 서울중앙지방법원")
        String competentCourt,

        @Schema(description = "발주자 아이디를 입력해주세요.", example = "1")
        @NotNull(message = "발주자 PK는 필수입니다.")
        Long ordererId,

        @Schema(description = "수급자 아이디를 입력해주세요.", example = "2")
        @NotNull(message = "수급자 PK는 필수입니다.")
        Long beneficiaryId
) {

        @AssertTrue(message = "종료 날짜가 시작 날짜보다 빠를 수 없습니다.")
        public boolean isEndAfterStart() {
                return !projectEndDate.isBefore(projectStartDate);
        }

        @AssertTrue(message = "진행일이 시작 날짜와 종료 날짜 사이 기간과 일치하지 않습니다.")
        public boolean isProgressingDaysMatch(){
                long between = ChronoUnit.DAYS.between(projectStartDate, projectEndDate);
                return between == projectProgressingDays;
        }

        @AssertTrue(message = "선금 금액이 선금 비율과 총 계약 금액에 부합하지 않습니다.")
        public boolean isDownPaymentConsistent(){
                BigDecimal total = BigDecimal.valueOf(totalContractAmount);
                BigDecimal pct = BigDecimal.valueOf(downPaymentPercentage)
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                BigDecimal expected = total.multiply(pct).setScale(0, RoundingMode.HALF_UP);

                return expected.longValue() == downPaymentAmount;
        }
}
