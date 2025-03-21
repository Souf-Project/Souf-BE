package com.souf.soufwebsite.domain.user.entity;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.file.entity.File;
import com.souf.soufwebsite.domain.user.dto.ReqDto.UserReqDto;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long id;

    @NotNull
    @Column(nullable = false)
    @Size(min = 5, max = 30)
    private String email;

    @NotNull
    @Column(nullable = false)
    @Size(min = 8, max = 20)
    private String password;

    @NotNull
    @Column(nullable = false)
    @Size(min = 2, max = 20)
    private String username;

    @NotNull
    @Column(nullable = false)
    @Size(min = 2, max = 20)
    private String nickname;

    private LocalDate birth;

    @Size(max = 100)
    private String intro;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @OneToOne
    @JoinColumn(name = "fileId")
    private File file;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL)
    private List<Feed> feeds = new ArrayList<>();

    @Builder
    public User(String email, String password, String username, String nickname) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
    }

    // 회원 정보 업데이트 (업데이트 가능한 필드만 반영)
    public void updateUser(UserReqDto dto) {
        this.username = dto.username();
        this.nickname = dto.nickname();
        this.birth = dto.birth();
        this.intro = dto.intro();
        // 필요 시 다른 필드도 업데이트
    }
}
