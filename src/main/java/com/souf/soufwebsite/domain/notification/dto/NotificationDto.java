package com.souf.soufwebsite.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto implements Serializable {

    private Long targetMemberId;

    private Long notificationId;

    private String title;

    private String content;
}
