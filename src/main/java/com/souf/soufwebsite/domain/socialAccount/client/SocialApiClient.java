package com.souf.soufwebsite.domain.socialAccount.client;

import com.souf.soufwebsite.domain.socialAccount.SocialProvider;
import com.souf.soufwebsite.domain.socialAccount.dto.SocialUserInfo;

public interface SocialApiClient {
    SocialProvider getProvider();
    SocialUserInfo getUserInfoByCode(String code);
}
