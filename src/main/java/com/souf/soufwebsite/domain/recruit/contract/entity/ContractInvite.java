package com.souf.soufwebsite.domain.recruit.contract.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@Table(name = "contract_invites")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractInvite {

    @Id
    @Column(length = 96)
    private String token;

    private String contractUuid;

    private Long beneficiaryId;
    private Long chatRoomId;

    private Instant expiresAt;
    private Instant lastAccessedAt;

    @Column(nullable = false)
    private boolean revoked = false;

    private Instant isConsumed;

    public ContractInvite(String token, String contractUuid, Long beneficiaryId, Long chatRoomId, Instant expiresAt) {
        this.token = token;
        this.contractUuid = contractUuid;
        this.beneficiaryId = beneficiaryId;
        this.chatRoomId = chatRoomId;
        this.expiresAt = expiresAt;
    }

    public void updateViewTiming() {
        this.lastAccessedAt = Instant.now();
    }

    public void consume() {
        this.isConsumed = Instant.now();
    }
}
