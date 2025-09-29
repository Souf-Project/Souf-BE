package com.souf.soufwebsite.global.common.viewCount.service;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.common.viewCount.dto.ViewCountResDto;

public interface ViewCountService {

    ViewCountResDto getViewCountFromMain();
    ViewCountResDto refreshViewCountCache();

    long updateTotalViewCount(Member member, PostType type, Long postId, Long currentViewCount, String ip, String userAgent);

    void deleteViewCountFromRedis(PostType type, Long postId);

    void initiateRedisKey();
}
