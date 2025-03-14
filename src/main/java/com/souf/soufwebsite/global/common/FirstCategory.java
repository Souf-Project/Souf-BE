package com.souf.soufwebsite.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FirstCategory {

    VISION("시각 디자인"),
    SPACE("공간, 제품 디자인"),
    MUSIC("음악"),
    PHOTO("사진"),
    VIDEO("영상");

    private final String category;
}
