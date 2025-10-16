package com.souf.soufwebsite.domain.member.controller.memberClub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberClubSuccessMessage {
    JOIN_CLUB_SUCCESS("동아리 지원이 완료되었습니다."),
    LEAVE_CLUB_SUCCESS("동아리 탈퇴가 완료되었습니다."),
    MY_CLUBS_READ_SUCCESS("내 동아리 목록을 불러왔습니다."),
    CLUB_MEMBERS_READ_SUCCESS("동아리 구성원 목록을 불러왔습니다.");

    private final String message;
}
