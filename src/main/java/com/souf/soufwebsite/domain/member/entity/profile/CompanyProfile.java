package com.souf.soufwebsite.domain.member.entity.profile;

import com.souf.soufwebsite.domain.member.dto.reqDto.addInfo.AddCompanyInfoReqDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.signup.CompanySignupReqDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "company_profiles")
@SQLRestriction("is_deleted = false")
public class CompanyProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String businessRegistrationNumber;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String roadNameAddress;

    @Column(nullable = false)
    private String detailedAddress;

    @Column(nullable = false)
    private String businessStatus;

    @Column(nullable = false)
    private String businessClassification;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public CompanyProfile(CompanySignupReqDto reqDto){
        this.companyName = reqDto.getCompanyName();
        this.businessRegistrationNumber = reqDto.getBusinessRegistrationNumber();
        this.zipCode = reqDto.getAddressReqDto().zipCode();
        this.roadNameAddress = reqDto.getAddressReqDto().roadNameAddress();
        this.detailedAddress = reqDto.getAddressReqDto().detailedAddress();
        this.businessStatus = reqDto.getBusinessStatus();
        this.businessClassification = reqDto.getBusinessClassification();
    }

    public CompanyProfile(AddCompanyInfoReqDto reqDto){
        this.companyName = reqDto.companyName();
        this.businessRegistrationNumber = reqDto.businessRegistrationNumber();
        this.zipCode = reqDto.addressReqDto().zipCode();
        this.roadNameAddress = reqDto.addressReqDto().roadNameAddress();
        this.detailedAddress = reqDto.addressReqDto().detailedAddress();
        this.businessStatus = reqDto.businessStatus();
        this.businessClassification = reqDto.businessClassification();
    }

    public void attachMember(Member member) {
        this.member = member;
    }

    public void softDelete(){
        this.isDeleted = true;
        this.companyName = "탈퇴한 회원";
        this.businessRegistrationNumber = "탈퇴한 회원";
        this.zipCode = "탈퇴한 회원";
        this.roadNameAddress = "탈퇴한 회원";
        this.detailedAddress = "탈퇴한 회원";
        this.businessStatus = "탈퇴한 회원";
        this.businessClassification = "탈퇴한 회원";
    }
}
