package com.souf.soufwebsite.domain.member.entity.profile;

import com.souf.soufwebsite.domain.member.dto.reqDto.signup.ClubSignupReqDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "club_profiles")
@NoArgsConstructor
public class ClubProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clubAuthenticationMethod;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public ClubProfile(ClubSignupReqDto reqDto) {
        this.clubAuthenticationMethod = reqDto.getClubAuthenticationMethod();
    }

    public void attachMember(Member member) {
        this.member = member;
    }
}
