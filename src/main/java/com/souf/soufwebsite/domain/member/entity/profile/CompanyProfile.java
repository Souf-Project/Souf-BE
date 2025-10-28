package com.souf.soufwebsite.domain.member.entity.profile;

import com.souf.soufwebsite.domain.member.dto.reqDto.signup.CompanySignupReqDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "company_profiles")
public class CompanyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    private String businessRegistrationNumber;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String roadNameAddress;

    @Column(nullable = false)
    private String detailedAddress;

    private String businessStatus;

    private String businessClassification;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public CompanyProfile(CompanySignupReqDto reqDto){
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
}
