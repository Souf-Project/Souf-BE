package com.souf.soufwebsite.domain.recruit.contract.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContractSuccessMessage {

    INITIAL_CONTRACT_CREATE("초기 계약서를 생성하였습니다."),
    PERSONAL_INFO_GET("계약서를 작성할 사용자의 정보를 조회하였습니다."),
    INITIAL_CONTRACT_GET("수급자가 초기 계약서 정보를 조회하였습니다."),
    COMPLETED_CONTRACT_CREATE("람다에서 계약서를 생성하였습니다."),
    COMPLETED_CONTRACT_GET("계약서 pdf를 조회합니다."),
    CONTRACT_FILE_METADATA_CREATE("계약서 메타데이터를 저장하였습니다.");

    private final String message;
}
