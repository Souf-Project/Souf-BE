package com.souf.soufwebsite.global.util;

import com.souf.soufwebsite.global.exception.hash.NotAvailableAlgorithmException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());

            // 바이트 → HEX 문자열로 변환
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString(); // 길이 64짜리 HEX 문자열
        } catch (NoSuchAlgorithmException e) {
            throw new NotAvailableAlgorithmException();
        }
    }
}
