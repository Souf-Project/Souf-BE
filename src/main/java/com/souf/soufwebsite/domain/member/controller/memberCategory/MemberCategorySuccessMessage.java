package com.souf.soufwebsite.domain.member.controller.memberCategory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberCategorySuccessMessage {

    CATEGORY_ADD_SUCCESS("관심 분야를 추가하였습니다."),
    CATEGORY_REMOVE_SUCCESS("관심 분야를 삭제되었습니다."),
    CATEGORY_UPDATE_SUCCESS("관심 분야를 수정되었습니다."),
    CATEGORY_GET_SUCCESS("관심 분야 목록을 불러왔습니다.");

    private final String message;
}