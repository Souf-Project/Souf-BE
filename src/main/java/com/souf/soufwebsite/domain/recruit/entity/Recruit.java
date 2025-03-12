package com.souf.soufwebsite.domain.recruit.entity;

import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import com.souf.soufwebsite.domain.user.entity.User;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recruit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitId")
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    @Size(max = 50)
    private String region;

    // 마감일자
    private LocalDateTime deadline;

    // 작성자 (userId)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Builder
    public Recruit(Long id, String title, String content, String region, LocalDateTime deadline, User user) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.region = region;
        this.deadline = deadline;
        this.user = user;
    }

    public static Recruit of(RecruitReqDto reqDto, User user) {
        return Recruit.builder()
                .title(reqDto.title())
                .content(reqDto.content())
                .region(reqDto.region())
                .deadline(getDeadLine(reqDto.deadLine()))
                .user(user)
                .build();
    }

    private static LocalDateTime getDeadLine(String deadLine){
        return LocalDateTime.parse(deadLine);
    }
}