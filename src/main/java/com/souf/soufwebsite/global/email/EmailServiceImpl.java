package com.souf.soufwebsite.global.email;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public boolean sendEmail(String email, String title, String content) {
        // 이메일 전송
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(email);
            helper.setSubject(title);
            helper.setText(content, true); // true = HTML 허용

            // 보내는 사람 이름 지정 가능
            helper.setFrom(new InternetAddress("souf-official@souf.co.kr", "스프관리자"));

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // 로그로 예외 확인
            return false;
        }
    }
}
