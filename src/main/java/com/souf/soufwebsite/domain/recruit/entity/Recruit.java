package com.souf.soufwebsite.domain.recruit.entity;

import com.souf.soufwebsite.domain.recruit.dto.RecruitReqDto;
import com.souf.soufwebsite.domain.user.entity.User;
import com.souf.soufwebsite.global.common.BaseEntity;
import com.souf.soufwebsite.global.common.FirstCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.datetime.DateFormatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    @Column
    private LocalDateTime deadline;

    @Column(nullable = false)
    private String payment;

    @Column
    private String preferentialTreatment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FirstCategory firstCategory;

    // 작성자 (userId)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Builder
    public Recruit(RecruitReqDto reqDto, LocalDateTime deadline, User user) {
        this.title = reqDto.title();
        this.content = reqDto.content();
        this.region = reqDto.region();
        this.deadline = deadline;
        this.payment = reqDto.payment();
        this.preferentialTreatment = reqDto.preferentialTreatment();
        this.firstCategory = reqDto.firstCategory();
        this.user = user;
    }

    public static Recruit of(RecruitReqDto reqDto, LocalDateTime deadline, User user) {
        return Recruit.builder()
                .reqDto(reqDto)
                .deadline(deadline)
                .user(user)
                .build();
    }

    public void updateRecruit(RecruitReqDto reqDto) {
        this.title = reqDto.title();
        this.content = reqDto.content();
        this.region = reqDto.region();
        this.deadline = getDeadLine(reqDto.deadline());
        this.payment = reqDto.payment();
        this.preferentialTreatment = reqDto.preferentialTreatment();
        this.firstCategory = reqDto.firstCategory();
    }

    private static LocalDateTime getDeadLine(String deadLine){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        return LocalDateTime.parse(deadLine, formatter);
    }
}