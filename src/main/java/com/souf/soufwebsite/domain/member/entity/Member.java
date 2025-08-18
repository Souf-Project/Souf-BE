package com.souf.soufwebsite.domain.member.entity;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.member.dto.ReqDto.UpdateReqDto;
import com.souf.soufwebsite.global.common.BaseEntity;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.exception.NotDuplicateCategoryException;
import com.souf.soufwebsite.global.common.category.exception.NotExceedCategoryLimitException;
import com.souf.soufwebsite.global.util.HashUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "is_deleted = false")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    @Size(min = 5, max = 30)
    private String email;

    @NotEmpty
    @Column(nullable = false)
    @Size(min = 8, max = 255)
    private String password;

    @NotEmpty
    @Column(nullable = false)
    @Size(min = 2, max = 20)
    private String username;

    @NotEmpty
    @Column(nullable = false)
    @Size(min = 2, max = 20)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Size(max = 100)
    private String intro;

    @Column(length = 300)
    private String personalUrl;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberCategoryMapping> categories = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Feed> feeds = new ArrayList<>();

    // 이용약관 동의서 속성
    @Column(name = "personal_info_agreement", nullable = false)
    private boolean personalInfoAgreement = false;

    @Column(name = "marketing_agreement", nullable = false)
    private boolean marketingAgreement = false;

    @Builder
    public Member(String email, String password, String username, String nickname, RoleType role, Boolean marketingAgreement) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.personalInfoAgreement = true;
        this.marketingAgreement = marketingAgreement;
    }

    // 회원 정보 업데이트 (업데이트 가능한 필드만 반영)
    public void updateInfo(UpdateReqDto dto) {
        if (dto.username() != null) this.username = dto.username();
        if (dto.nickname() != null) this.nickname = dto.nickname();
        if (dto.intro() != null) this.intro = dto.intro();
        if (dto.personalUrl() != null) this.personalUrl = dto.personalUrl();
        if (dto.marketingAgreement() != null) this.marketingAgreement = dto.marketingAgreement();
    }

    public void updateRole(RoleType newRole) {
        this.role = newRole;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void addCategory(MemberCategoryMapping mapping) {
        for (MemberCategoryMapping existing : this.categories) {
            if (existing.isSameCategorySet(mapping)) {
                throw new NotDuplicateCategoryException();
            }
        }

        if (this.categories.size() > 3) {
            throw new NotExceedCategoryLimitException();
        }
        this.categories.add(mapping);
    }

    public void updateCategory(CategoryDto oldDto, MemberCategoryMapping newMapping) {
        removeCategory(oldDto);
        addCategory(newMapping);
    }

    public void removeCategory(CategoryDto dto) {
        categories.removeIf(mapping ->
                mapping.getFirstCategory().getId().equals(dto.firstCategory()) &&
                        mapping.getSecondCategory().getId().equals(dto.secondCategory()) &&
                        mapping.getThirdCategory().getId().equals(dto.thirdCategory())
        );
    }

    public void clearCategories() {
        for (MemberCategoryMapping mapping : categories) {
            mapping.disconnectMember();
        }
        categories.clear();
    }

    public void softDelete() { // SHA-256 같은 방식
        this.email = "deleted:" + HashUtil.sha256(this.email);
        this.username = "탈퇴한 회원";
        this.intro = "탈퇴한 회원입니다.";
        this.personalUrl = null;
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}