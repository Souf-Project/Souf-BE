package com.souf.soufwebsite.domain.oauth.client;

import com.souf.soufwebsite.domain.oauth.dto.SocialMemberInfo;

public interface SocialApiClient {
    SocialMemberInfo getMemberInfoByCode(String code);
}
