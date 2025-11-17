package com.souf.soufwebsite.domain.recruit.contract.controller;

import com.souf.soufwebsite.domain.recruit.contract.dto.req.BeneficiaryReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.JoinByInviteReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.OrdererReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.CreateInitialContractResDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.InitialContractResDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.PreviewBeneficiaryInfoRes;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.PreviewOrdererInfoRes;
import com.souf.soufwebsite.domain.recruit.contract.service.ContractService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
            @Valid @RequestBody OrdererReqDto ordererReqDto
    ){

        CreateInitialContractResDto result = contractService.createContractWithOrderer(email, roomId, ordererReqDto);

        return new SuccessResponse<>(result);
    }

    @GetMapping("/{roomId}/orderer/preview")
    public SuccessResponse<PreviewOrdererInfoRes> previewOrdererInfo(
            @PathVariable(name = "roomId") Long roomId,
            @CurrentEmail String email
    ){
        PreviewOrdererInfoRes result = contractService.previewOrdererInfo(email, roomId);

        return new SuccessResponse<>(result);
    }

    @GetMapping("/{roomId}/beneficiary/preview")
    public SuccessResponse<PreviewBeneficiaryInfoRes> previewBeneficiaryInfo(
            @PathVariable(name = "roomId") Long roomId,
            @CurrentEmail String email,
            @Valid @RequestBody JoinByInviteReqDto reqDto
    ) {

        PreviewBeneficiaryInfoRes result = contractService.previewBeneficiaryInfo(email, reqDto, roomId);

        return new SuccessResponse<>(result);
    }

    @PostMapping("/{roomId}/beneficiary/preview")
    public SuccessResponse<InitialContractResDto> previewInitialContract(
            @PathVariable(name = "roomId") Long roomId,
            @CurrentEmail String email,
            @Valid @RequestBody JoinByInviteReqDto reqDto
    ) {

        InitialContractResDto result = contractService.getIncompleteContractInfo(email, reqDto, roomId);

        return new SuccessResponse<>(result);
    }

    @PostMapping("/{roomId}/beneficiary/{contractId}")
    public SuccessResponse<String> createContract(
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "contractId") Long contractId,
            @CurrentEmail String email,
            @Valid @RequestBody BeneficiaryReqDto reqDto
    ) {

        String url = contractService.acceptContractByInvite(email, contractId, roomId, reqDto);

        return new SuccessResponse<>(url);
    }
}
