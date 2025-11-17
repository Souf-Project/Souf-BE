package com.souf.soufwebsite.domain.recruit.contract.entity;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.BeneficiaryReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.OrdererReqDto;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Entity
@Table(name = "contracts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contract extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String contractUuid;

    @Column(nullable = false)
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderer_id", nullable = true)
    private Member orderer;

    @Column(nullable = false)
    private String ordererName;

    @Column
    private String companyName;

    @Column
    private String businessRegistrationNumber;

    @Column
    private String roadAddress;

    @Column(nullable = false)
    private String contactPhone;

    @Column(nullable = false)
    private String contactEmail;

    @Column
    private String managerWithPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiary", nullable = true)
    private Member beneficiary;

    @Column(nullable = false)
    private String beneficiaryName;

    @Column
    private LocalDate beneficiaryBirth;

    @Column
    private String beneficiarySchoolInfo;

    @Column
    private String beneficiaryEmail;

    @Column
    private String beneficiaryPhone;

    @Column
    private String beneficiaryBankAccount;

    @OneToOne(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private Project project;

    @Column(nullable = false)
    private Boolean copyrightApproved;

    @Column(nullable = false)
    private String confidentialityPeriod;

    @Column(nullable = false)
    private String warrantyPeriod;

    @Column(nullable = false)
    private String competentCourt;

    @Column(nullable = false)
    private ContractStatus contractStatus;

    @Version
    private Long version;

    public Contract(OrdererReqDto ordererReqDto, Long roomId, Member orderer, Member beneficiary) {
        this.contractUuid = makeContractNo();
        this.roomId = roomId;
        this.ordererName = ordererReqDto.ordererPersonalInfoReqDto().ceoName();
        this.companyName = ordererReqDto.ordererPersonalInfoReqDto().companyName();
        this.businessRegistrationNumber = ordererReqDto.ordererPersonalInfoReqDto().businessRegistrationNumber();
        this.roadAddress = ordererReqDto.ordererPersonalInfoReqDto().roadNameAddress();
        this.contactPhone = ordererReqDto.ordererPersonalInfoReqDto().companyPhoneNumber();
        this.contactEmail = ordererReqDto.ordererPersonalInfoReqDto().contactEmail();
        this.managerWithPosition = ordererReqDto.ordererPersonalInfoReqDto().managerWithPosition();
        this.copyrightApproved = ordererReqDto.copyrightApproved();
        this.confidentialityPeriod = ordererReqDto.confidentialityPeriod();
        this.warrantyPeriod = ordererReqDto.warrantyPeriod();
        this.competentCourt = ordererReqDto.competentCourt();
        this.orderer = orderer;
        this.beneficiary = beneficiary;
        this.contractStatus = ContractStatus.PENDING_COUNTERPART;
    }

    public void updateBeneficiaryInfo(BeneficiaryReqDto reqDto){
        this.beneficiaryName = reqDto.username();
        this.beneficiaryBirth = reqDto.birth();
        this.beneficiarySchoolInfo = reqDto.schoolName();
        this.beneficiaryEmail = reqDto.email();
        this.beneficiaryPhone = reqDto.phoneNumber();
        this.beneficiaryBankAccount = combineBanknameAndAccount(reqDto.bank(), reqDto.bankAccount());
        this.contractStatus = ContractStatus.CREATING_CONTRACT;
    }

    public void completeCreatingContract(){
        this.contractStatus = ContractStatus.COMPLETED;
    }

    public void attachProject(Project project) {
        this.project = project;
        project.attachContract(this);
    }

    private String combineBanknameAndAccount(String bankName, String account) {
        return bankName + " " + account;
    }

    private String makeContractNo() {
        String date = LocalDate.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.BASIC_ISO_DATE);
        return "SOUF-CT-" + date + "-" + UUID.randomUUID();
    }

}
