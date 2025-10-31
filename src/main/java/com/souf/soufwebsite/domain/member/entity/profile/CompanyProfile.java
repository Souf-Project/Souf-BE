package com.souf.soufwebsite.domain.member.entity.profile;

import com.souf.soufwebsite.domain.member.dto.reqDto.signup.CompanySignupReqDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "company_profiles")
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

    public CompanyProfile(CompanySignupReqDto reqDto){
        this.companyName = reqDto.getCompanyName();
        this.businessRegistrationNumber = reqDto.getBusinessRegistrationNumber();
        this.zipCode = reqDto.getAddressReqDto().zipCode();
        this.roadNameAddress = reqDto.getAddressReqDto().roadNameAddress();
        this.detailedAddress = reqDto.getAddressReqDto().detailedAddress();
        this.businessStatus = reqDto.getBusinessStatus();
        this.businessClassification = reqDto.getBusinessClassification();
    }

    public void attachMember(Member member) {
        this.member = member;
    }
}
