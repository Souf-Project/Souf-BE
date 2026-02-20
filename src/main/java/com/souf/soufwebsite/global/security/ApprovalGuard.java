package com.souf.soufwebsite.global.security;

import com.souf.soufwebsite.domain.member.entity.ApprovedStatus;
import com.souf.soufwebsite.domain.member.exception.NotApprovedAccountException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("approvalGuard")
public class ApprovalGuard {

    public boolean approved(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotApprovedAccountException();
        }

        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

        boolean approved = principal.getApprovedStatus().equals(ApprovedStatus.APPROVED);
        if(!approved) {
            throw new NotApprovedAccountException();
        }

        return true;
    }
}
