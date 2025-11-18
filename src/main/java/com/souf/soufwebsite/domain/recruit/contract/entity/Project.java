package com.souf.soufwebsite.domain.recruit.contract.entity;

import com.souf.soufwebsite.domain.recruit.contract.dto.req.OrdererReqDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "projects")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String projectName;

    @Column(nullable = false)
    private LocalDate projectStartDate;

    @Column(nullable = false)
    private LocalDate projectEndDate;

    @Column(nullable = false)
    private int projectProgressingDays;

    @Column(nullable = false)
    private Long totalContractAmount;

    @Column(nullable = false)
    private Double downPaymentPercentage;

    @Column(nullable = false)
    private Long downPaymentAmount;

    @Column(nullable = false)
    private int inspectionDays;

    @OneToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    public Project(OrdererReqDto ordererReqDto) {
        this.projectName = ordererReqDto.projectName();
        this.projectStartDate = ordererReqDto.projectStartDate();
        this.projectEndDate = ordererReqDto.projectEndDate();
        this.projectProgressingDays = ordererReqDto.projectProgressingDays();
        this.totalContractAmount = ordererReqDto.totalContractAmount();
        this.downPaymentPercentage = ordererReqDto.downPaymentPercentage();
        this.downPaymentAmount = ordererReqDto.downPaymentAmount();
        this.inspectionDays = ordererReqDto.inspectionDays();
    }

    public void attachContract(Contract contract) {
        this.contract = contract;
    }
}
