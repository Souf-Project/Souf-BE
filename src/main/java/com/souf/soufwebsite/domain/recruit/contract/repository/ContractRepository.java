package com.souf.soufwebsite.domain.recruit.contract.repository;

import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.contract.entity.Contract;
import com.souf.soufwebsite.domain.recruit.contract.entity.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByContractUuid(String contractUuid);

    Optional<Contract> findByChatRoom(ChatRoom chatRoom);

    @Query("select c from Contract c where c.chatRoom = :chatRoom and (c.beneficiary = :member or c.orderer = :member)")
    List<Contract> findByMember(@Param(value = "chatRoom") ChatRoom chatRoom, @Param(value = "member") Member member);

    @Query("select c from Contract as c where c.beneficiary = :beneficiary and c.chatRoom = :chatRoom and c.contractStatus = :contractStatus")
    Optional<Contract> findByBeneficiaryAndChatRoomAndContractStatus_PendingCounterpart(
            @Param(value = "beneficiary") Member beneficiary, @Param(value = "chatRoom") ChatRoom chatRoom, @Param(value = "contractStatus") ContractStatus contractStatus);
}
