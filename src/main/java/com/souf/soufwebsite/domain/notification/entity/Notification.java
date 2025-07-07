package com.souf.soufwebsite.domain.notification.entity;

import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    @Size(min = 3, max = 30)
    private String title;

    @NotNull
    @Column(nullable = false)
    @Size(min = 30, max = 500)
    private String content;

    @Column(nullable = false)
    private Boolean isRead;

}
