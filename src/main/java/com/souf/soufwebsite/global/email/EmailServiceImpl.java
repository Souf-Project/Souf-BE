package com.souf.soufwebsite.global.email;

import lombok.RequiredArgsConstructor;\
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
        // 6자리 인증번호 생성
        String code = String.format("%06d", new Random().nextInt(1000000));
        String redisKey = "email:verification:" + email;

        // Redis에 저장 (5분 동안 유효)
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);

        // 이메일 전송
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(title);
            message.setText(content);
            message.setFrom(fromEmail);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
