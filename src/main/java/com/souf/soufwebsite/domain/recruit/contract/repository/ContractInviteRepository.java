package com.souf.soufwebsite.domain.recruit.contract.repository;

import com.souf.soufwebsite.domain.recruit.contract.entity.ContractInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractInviteRepository extends JpaRepository<ContractInvite, String> {
    Optional<ContractInvite> findByChatRoomIdAndBeneficiaryIdAndIsConsumedIsNull(Long chatRoomId, Long beneficiaryId);
}
