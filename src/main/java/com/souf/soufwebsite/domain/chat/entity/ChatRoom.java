package com.souf.soufwebsite.domain.chat.entity;

import com.souf.soufwebsite.domain.application.entity.Application;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.contract.entity.Contract;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_Id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChatRoomStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @OneToMany(mappedBy = "chatRoom")
    private List<Contract> contracts = new ArrayList<>();

    public ChatRoom(Member sender, Member receiver, Application application) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = ChatRoomStatus.ACTIVE;
    }

    public boolean hasParticipant(Member member) {
        return Objects.equals(this.getSender().getId(), member.getId()) ||
                Objects.equals(this.getReceiver().getId(), member.getId());
    }

    public void addContract(Contract contract) {
        this.contracts.add(contract);
    }

    public void close() {
        this.status = ChatRoomStatus.CLOSED;
    }

}