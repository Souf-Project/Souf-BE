package com.souf.soufwebsite.global.email;

public interface EmailService {
    boolean sendEmail(String email, String title, String content);
}
