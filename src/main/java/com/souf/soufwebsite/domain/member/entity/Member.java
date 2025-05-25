package com.souf.soufwebsite.domain.member.entity;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.member.dto.ReqDto.UpdateReqDto;
import com.souf.soufwebsite.global.common.BaseEntity;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberCategoryMapping> categories = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "media_id")
    private Media media;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Feed> feeds = new ArrayList<>();

    @Builder
    public Member(String email, String password, String username, String nickname, RoleType role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
    }

    // 회원 정보 업데이트 (업데이트 가능한 필드만 반영)
    public void updateInfo(UpdateReqDto dto) {
        if (dto.username() != null) this.username = dto.username();
        if (dto.nickname() != null) this.nickname = dto.nickname();
        if (dto.intro() != null) this.intro = dto.intro();
        if (dto.personalUrl() != null) this.personalUrl = dto.personalUrl();
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
                throw new IllegalArgumentException("이미 존재하는 카테고리 세트입니다.");
            }
        }

        if (this.categories.size() >= 3) {
            throw new IllegalStateException("카테고리는 최대 3개까지만 등록할 수 있습니다.");
        }

        this.categories.add(mapping);
        mapping.setMember(this);
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
}