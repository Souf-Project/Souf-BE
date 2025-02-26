package com.souf.soufwebsite.domain.user.entity;

import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    @Size(min = 5, max = 30)
    private String email;
    private String password;
    private String username;
    private String nickname;
    private LocalDate birth;
    private String intro;

    @Enumerated(EnumType.STRING)
    private RoleType role;
}
