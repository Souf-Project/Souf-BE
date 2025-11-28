package com.souf.soufwebsite.domain.recruit.contract.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateContractPdfResDto(
        @Schema(description = "계약서 아이디 입니다.")
        Long contractId,

        @Schema(description = "S3에 저장된 계약서 pdf 경로입니다.")
        String pdfUrl
) {
}
