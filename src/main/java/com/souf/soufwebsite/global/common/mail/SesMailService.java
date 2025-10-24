package com.souf.soufwebsite.global.common.mail;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendTemplatedEmailRequest;
import com.souf.soufwebsite.global.util.SesTemplateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SesMailService {

    private final SesTemplateUtils templateUtil;
    private final AmazonSimpleEmailService emailService;

    public void sendEmailAuthenticationCode(String to, String purpose, String authenticationCode) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("purpose", purpose);
            data.put("code", authenticationCode);

            SendTemplatedEmailRequest request = templateUtil.
                    createSendTemplatedEmailRequest(to, "SouFEmailVerification", data);

            emailService.sendTemplatedEmail(request);
            log.info("인증번호가 성공적으로 전송되었습니다!");
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

    public void sendInquiryResult(String to, String nickname, String inquiryTitle) {

        try {
            Map<String, String> data = new HashMap<>();
            data.put("nickname", nickname);
            data.put("inquiryTitle", inquiryTitle);

            SendTemplatedEmailRequest request = templateUtil
                    .createSendTemplatedEmailRequest(to, "SendInquiryStatusTemplate", data);
            emailService.sendTemplatedEmail(request);
        } catch (Exception e) {
            throw new RuntimeException("문의내용 결과 전송 실패", e);
        }
    }
}
