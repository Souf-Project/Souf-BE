package com.souf.soufwebsite.domain.socialAccount.client;

import com.souf.soufwebsite.domain.socialAccount.dto.SocialUserInfo;

public interface SocialApiClient {
    SocialUserInfo getUserInfoByCode(String code);
}
