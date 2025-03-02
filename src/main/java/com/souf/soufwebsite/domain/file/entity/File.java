package com.souf.soufwebsite.domain.file.entity;

import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fileId")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String fileUrl;

    @NotNull
    private String fileName;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

}
