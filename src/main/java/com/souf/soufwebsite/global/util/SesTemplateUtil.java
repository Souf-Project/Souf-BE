package com.souf.soufwebsite.global.util;

import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.SendTemplatedEmailRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SesTemplateUtil {

    @Value("${spring.mail.username}")
    private String mail;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public SendTemplatedEmailRequest createSendTemplatedEmailRequest(String recipient, String templateName,
                                                                     Map<String, String> templateData) throws Exception {
        String jsonData = objectMapper.writeValueAsString(templateData);

        return new SendTemplatedEmailRequest()
                .withSource(mail)
                .withTemplate(templateName)
                .withTemplateData(jsonData)
                .withDestination(new Destination().withToAddresses(recipient));
    }
}
