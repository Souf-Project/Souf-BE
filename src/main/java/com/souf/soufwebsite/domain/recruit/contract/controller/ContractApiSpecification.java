package com.souf.soufwebsite.domain.recruit.contract.controller;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.BeneficiaryReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.JoinByInviteReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.OrdererReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.*;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "계약서 도메인", description = "계약서를 작성 및 조회하는 기능 리스트입니다.")
public interface ContractApiSpecification {

    @Operation(summary = "계약서 초기 생성", description = "계약서 작성을 위해 발주자가 먼저 정보를 기입합니다.")
    @PostMapping("/{roomId}/orderer")
    SuccessResponse<CreateInitialContractResDto> createInitialContract(
            @PathVariable(name = "roomId") Long roomId,
            @CurrentEmail String email,
            @RequestBody OrdererReqDto ordererReqDto
    );

    @Operation(summary = "계약서 서명 후 최종 업로드", description = "계약서에 서로 서명 후 최종 완료된 계약서를 제출합니다.(발주자만 가능)")
    @PostMapping("{roomId}/orderer")
    SuccessResponse<PresignedUrlResDto> createInitialContract(
            @CurrentEmail String email,
            @PathVariable(name = "roomId") Long roomId,
            @Valid @RequestBody MediaReqDto mediaReqDto
    );

    @Operation(summary = "발주자 정보 불러오기", description = "계약서 작성 시 기존에 존재한 발주자의 정보를 불러옵니다.")
    @GetMapping("/{roomId}/orderer/preview")
    SuccessResponse<PreviewOrdererInfoRes> previewOrdererInfo(
            @PathVariable(name = "roomId") Long roomId,
            @CurrentEmail String email
    );

    @Operation(summary = "수급자 정보 불러오기", description = "계약서 작성 시 기존에 존재하는 수급자의 정보를 불러옵니다.")
    @GetMapping("/{roomId}/beneficiary/preview")
    SuccessResponse<PreviewBeneficiaryInfoRes> previewBeneficiaryInfo(
            @PathVariable(name = "roomId") Long roomId,
            @CurrentEmail String email,
            @RequestBody JoinByInviteReqDto reqDto
    );

    @Operation(summary = "수급자 계약서 조회", description = "발주자가 작성한 계약서 정보를 초대받은 수급자가 조회합니다.")
    @PatchMapping("/{roomId}/beneficiary/preview/contract")
    SuccessResponse<InitialContractResDto> previewInitialContract(
            @PathVariable(name = "roomId") Long roomId,
            @CurrentEmail String email,
            @RequestBody JoinByInviteReqDto reqDto
    );

    @Operation(summary = "계약서 생성", description = "수급자가 최종 정보를 확인하고 개인 정보를 기입한 후, 계약서를 생성합니다.")
    @PostMapping("/{roomId}/beneficiary")
    SuccessResponse<CreateContractPdfResDto> createContract(
            @PathVariable(name = "roomId") Long roomId,
            @CurrentEmail String email,
            @RequestBody BeneficiaryReqDto reqDto
    );

    @Operation(summary = "계약서 pdf 조회", description = "최종 업로드한 계약서 pdf를 조회합니다.")
    @GetMapping("/{roomId}")
    SuccessResponse<MediaResDto> getSignedContractPdf(
            @CurrentEmail String email,
            @PathVariable(name = "roomId") Long roomId
    );


}
