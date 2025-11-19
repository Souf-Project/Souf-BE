package com.souf.soufwebsite.domain.recruit.contract.entity;

import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "contract_chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    public ContractChatRoom(Contract contract, ChatRoom chatRoom) {
        this.contract = contract;
        this.chatRoom = chatRoom;

        contract.addContractChatRoom(this);
        chatRoom.addContractChatRoom(this);
    }
}
