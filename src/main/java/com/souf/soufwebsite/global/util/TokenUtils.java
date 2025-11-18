package com.souf.soufwebsite.global.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenUtils {

    private static final SecureRandom secureRandom = new SecureRandom();

    public String newOpaqueToken(int bytes) {
        byte[] buf = new byte[bytes];

        secureRandom.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }
}
