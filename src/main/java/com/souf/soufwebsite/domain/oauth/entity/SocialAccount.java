package com.souf.soufwebsite.domain.oauth.entity;

import com.souf.soufwebsite.domain.oauth.SocialProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "social_account",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_provider_user", columnNames = {"provider", "social_id"})
        },
        indexes = {
                @Index(name = "idx_member_id", columnList = "member_id")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SocialProvider provider;  // KAKAO, GOOGLE, ...

    @Column(name = "social_id", nullable = false, length = 100)
    private String providerUserId;    // 소셜 고유 id

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 선택: 소셜에서 내려주는 부가정보(이메일은 종종 null)
    @Column(length = 200) private String providerEmail;
    @Column(length = 100) private String displayName;
    @Column(length = 300) private String profileImageUrl;

    private LocalDateTime connectedAt;

    @Builder
    public SocialAccount(SocialProvider provider, String providerUserId, Member member,
                         String providerEmail, String displayName, String profileImageUrl) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.member = member;
        this.providerEmail = providerEmail;
        this.displayName = displayName;
        this.profileImageUrl = profileImageUrl;
        this.connectedAt = LocalDateTime.now();
    }
}

