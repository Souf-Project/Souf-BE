package com.souf.soufwebsite.domain.recruit.contract.repository;

import com.souf.soufwebsite.domain.recruit.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByContractUuid(String contractUuid);

    Optional<Contract> findByRoomId(Long roomId);
}
