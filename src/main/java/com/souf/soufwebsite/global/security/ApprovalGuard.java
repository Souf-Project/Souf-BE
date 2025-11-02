package com.souf.soufwebsite.global.security;

import com.souf.soufwebsite.domain.member.entity.ApprovedStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("approvalGuard")
public class ApprovalGuard {

    public boolean approved(Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return principal.getApprovedStatus() == ApprovedStatus.APPROVED;
    }
}
