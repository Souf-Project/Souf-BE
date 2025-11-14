package com.souf.soufwebsite.domain.recruit.contract.entity;

public enum ContractStatus {

    PENDING_COUNTERPART, // 수급자 승인 전
    SIGNED, // 양측 확인 완료 후 계약서 생성
    CANCELLED // 취소
}
