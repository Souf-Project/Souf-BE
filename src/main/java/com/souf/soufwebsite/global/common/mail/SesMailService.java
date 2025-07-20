package com.souf.soufwebsite.global.common.mail;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendTemplatedEmailRequest;
import com.souf.soufwebsite.global.util.SesTemplateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SesMailService {

    private final SesTemplateUtil templateUtil;
    private final AmazonSimpleEmailService emailService;

    public void sendEmailAuthenticationCode(String to, String purpose, String authenticationCode) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("purpose", purpose);
            data.put("code", authenticationCode);

            SendTemplatedEmailRequest request = templateUtil.
                    createSendTemplatedEmailRequest(to, "SouFEmailVerification", data);

            emailService.sendTemplatedEmail(request);
        } catch (Exception e) {
            throw new RuntimeException("인증번호 전송 실패", e);
        }
    }

    public void announceRecruitResult(String to, String nickname, String recruitTitle) {

        try {
            Map<String, String> data = new HashMap<>();
            data.put("nickname", nickname);
            data.put("recruitTitle", recruitTitle);

            SendTemplatedEmailRequest request = templateUtil.
                    createSendTemplatedEmailRequest(to, "SendRecruitResultTemplate", data);

            emailService.sendTemplatedEmail(request);
        } catch (Exception e) {
            throw new RuntimeException("인증번호 전송 실패", e);
        }
    }
}
