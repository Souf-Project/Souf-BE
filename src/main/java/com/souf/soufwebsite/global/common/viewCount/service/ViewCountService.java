package com.souf.soufwebsite.global.common.viewCount.service;

import com.souf.soufwebsite.global.common.viewCount.dto.ViewCountResDto;

public interface ViewCountService {

    ViewCountResDto getViewCountFromMain();
    ViewCountResDto refreshViewCountCache();

    void initiateRedisKey();
}
