package com.souf.soufwebsite.domain.recruit.contract.controller;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.BeneficiaryReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.OrdererReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.*;
import com.souf.soufwebsite.domain.recruit.contract.service.ContractService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.souf.soufwebsite.domain.recruit.contract.controller.ContractSuccessMessage.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contract")
public class ContractController implements ContractApiSpecification{

    private final ContractService contractService;

    @PostMapping("/{roomId}/orderer")
    public SuccessResponse<CreateInitialContractResDto> createInitialContract(
            @PathVariable(name = "roomId") Long roomId,
            @CurrentEmail String email,
            @Valid @RequestBody OrdererReqDto ordererReqDto,
            HttpServletResponse response
    ){

        CreateInitialContractResDto result = contractService.createContractWithOrderer(email, roomId, ordererReqDto, response);

        return new SuccessResponse<>(result, INITIAL_CONTRACT_CREATE.getMessage());
    }

    @PostMapping("{roomId}/orderer/upload")
    public SuccessResponse<PresignedUrlResDto> createInitialContract(
            @CurrentEmail String email,
            @PathVariable(name = "roomId") Long roomId,
            @Valid @RequestBody MediaReqDto mediaReqDto
    ) {
        PresignedUrlResDto result = contractService.uploadFinalContractMedia(email, roomId, mediaReqDto);

        return new SuccessResponse<>(result, CONTRACT_FILE_METADATA_CREATE.getMessage());
    }

    @GetMapping("/{roomId}/orderer/preview")
    public SuccessResponse<PreviewOrdererInfoRes> previewOrdererInfo(
            @PathVariable(name = "roomId") Long roomId,
            @CurrentEmail String email
    ){
        PreviewOrdererInfoRes result = contractService.previewOrdererInfo(email, roomId);

        return new SuccessResponse<>(result, PERSONAL_INFO_GET.getMessage());
    }

    @GetMapping("/{roomId}/beneficiary/preview")
    public SuccessResponse<PreviewBeneficiaryInfoRes> previewBeneficiaryInfo(
            @PathVariable(name = "roomId") Long roomId,
            @CurrentEmail String email,
            HttpServletRequest request
    ) {

        PreviewBeneficiaryInfoRes result = contractService.previewBeneficiaryInfo(email, request, roomId);

        return new SuccessResponse<>(result, PERSONAL_INFO_GET.getMessage());
    }

    @PatchMapping("/{roomId}/beneficiary/preview/contract")
    public SuccessResponse<InitialContractResDto> previewInitialContract(
            @PathVariable(name = "roomId") Long roomId,
            @CurrentEmail String email,
            HttpServletRequest request
    ) {

        InitialContractResDto result = contractService.getIncompleteContractInfo(email, request, roomId);

        return new SuccessResponse<>(result, INITIAL_CONTRACT_GET.getMessage());
    }

    @PostMapping("/{roomId}/beneficiary")
    public SuccessResponse<CreateContractPdfResDto> createContract(
            @PathVariable(name = "roomId") Long roomId,
            @CurrentEmail String email,
            @Valid @RequestBody BeneficiaryReqDto reqDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        CreateContractPdfResDto result = contractService.acceptContractByInvite(email, roomId, reqDto, request, response);

        return new SuccessResponse<>(result, COMPLETED_CONTRACT_CREATE.getMessage());
    }

    @GetMapping("/{roomId}")
    public SuccessResponse<MediaResDto> getSignedContractPdf(
            @CurrentEmail String email,
            @PathVariable(name = "roomId") Long roomId
    ) {
        MediaResDto result = contractService.getSignedContractPdfInChatRoom(email, roomId);

        return new SuccessResponse<>(result, COMPLETED_CONTRACT_GET.getMessage());
    }
}
