package com.souf.soufwebsite.domain.socialAccount.repository;

import com.souf.soufwebsite.domain.socialAccount.SocialProvider;
import com.souf.soufwebsite.domain.socialAccount.entity.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// SocialAccountRepository.java
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderUserId(SocialProvider provider, String providerUserId);
}
