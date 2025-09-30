package com.souf.soufwebsite.domain.file.entity;

import com.souf.soufwebsite.global.common.BaseEntity;
import com.souf.soufwebsite.global.common.PostType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Table(name = "media")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Media extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_Id")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String originalUrl;

     @Column
     private String thumbnailUrl;

    // @NotNull
    // private String detailThumbnailUrl;

    @NotNull
    private String fileName;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @NotNull
    @Column(nullable = false)
    private Long postId;

    public static Media of(String url, String name, MediaType type, PostType postType, Long postId) {
        return Media.builder()
                .originalUrl(url)
                .fileName(name)
                .mediaType(type)
                .postType(postType)
                .postId(postId)
                .build();
    }

    public void addThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
