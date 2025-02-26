package com.souf.soufwebsite.domain.file.entity;

import com.souf.soufwebsite.domain.user.entity.User;
import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fileId")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private URL fileURL;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

}
