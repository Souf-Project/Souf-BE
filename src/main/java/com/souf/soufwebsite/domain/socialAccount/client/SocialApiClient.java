package com.souf.soufwebsite.domain.socialAccount.client;

import com.souf.soufwebsite.domain.socialAccount.dto.SocialMemberInfo;

public interface SocialApiClient {
    SocialMemberInfo getMemberInfoByCode(String code);
}
