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

    @Column(nullable = false)
    private double temperature;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberCategoryMapping> categories = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Feed> feeds = new ArrayList<>();

    // 이용약관 동의서 속성
    @Column(name = "personal_info_agreement", nullable = false)
    private boolean personalInfoAgreement = false;

    @Column(name = "marketing_agreement", nullable = false)
    private boolean marketingAgreement = false;

    // 누적 신고 횟수
    @Column(name = "cumulative_report_count")
    private Integer cumulativeReportCount;

    // === 다대다(자기참조) 연결 ===
    @OneToMany(mappedBy = "student", cascade = CascadeType.PERSIST)
    @Where(clause = "is_deleted = false")
    private List<MemberClubMapping> membershipsAsStudent = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.PERSIST)
    @Where(clause = "is_deleted = false")
    private List<MemberClubMapping> membershipsAsClub = new ArrayList<>();


    @Builder
    public Member(String email, String password, String username, String nickname, RoleType role, Boolean marketingAgreement) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.personalInfoAgreement = true;
        this.marketingAgreement = marketingAgreement;
        this.cumulativeReportCount = 0;
        this.temperature = 36.5;
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

    // ====== 동아리 가입/탈퇴 편의 메서드 ======
    public MemberClubMapping joinClub(Member club, String roleInClub) {
        if (this.role != RoleType.STUDENT) throw new IllegalStateException("학생만 동아리에 가입할 수 있습니다.");
        if (club == null || club.role != RoleType.CLUB) throw new IllegalArgumentException("유효한 동아리 계정이 아닙니다.");

        // 중복 가입 방지(소프트삭제 제외)
        boolean exists = membershipsAsStudent.stream()
                .anyMatch(m -> m.getClub().getId().equals(club.getId()));
        if (exists) return null; // 이미 가입됨

        MemberClubMapping m = MemberClubMapping.create(this, club, roleInClub);
        membershipsAsStudent.add(m);
        club.membershipsAsClub.add(m);
        return m;
    }

    public void leaveClub(Member club) {
        membershipsAsStudent.stream()
                .filter(m -> m.getClub().equals(club))
                .findFirst()
                .ifPresent(MemberClubMapping::softDelete);
    }

    public void softDelete() { // SHA-256 같은 방식
        this.email = "deleted:" + HashUtil.sha256(this.email);
        this.username = "탈퇴한 회원";
        this.intro = "탈퇴한 회원입니다.";
        this.personalUrl = null;
        this.isDeleted = true;

        new ArrayList<>(membershipsAsStudent).forEach(MemberClubMapping::softDelete);
        new ArrayList<>(membershipsAsClub).forEach(MemberClubMapping::softDelete);
    }
}