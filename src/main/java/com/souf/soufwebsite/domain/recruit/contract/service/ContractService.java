package com.souf.soufwebsite.domain.recruit.contract.service;

import com.souf.soufwebsite.domain.recruit.contract.dto.req.BeneficiaryReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.JoinByInviteReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.OrdererReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.CreateInitialContractResDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.InitialContractResDto;

public interface ContractService {

    CreateInitialContractResDto createContractWithOrderer(OrdererReqDto ordererReqDto);

    InitialContractResDto getIncompleteContractInfo(String email, JoinByInviteReqDto reqDto, Long currentChatRoomId);

    String acceptContractByInvite(String email, Long contractId, Long chatroomId, BeneficiaryReqDto beneficiaryReqDto);
    /*
    1. 발주자 계약서 데이터 받아 검증 후 저장 API
    2. 발주자 계약서 데이터 조회 API(계약서 데이터와 통합)
    3. 수급자 계약서 데이터 받아 검증 후 기존 엔티티 저장 API
    4. 승인 상태 되면 이벤트 발생시켜 API Gateway 호출 후 계약서 PDF 데이터 받아오기
     */
}
