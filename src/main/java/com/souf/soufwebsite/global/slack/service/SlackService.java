package com.souf.soufwebsite.global.slack.service;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.souf.soufwebsite.global.slack.SlackConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class SlackService {

    @Value(value = "${SLACK_TOKEN}")
    private String slackToken;

    public void sendSlackMessage(String message, String channel) {
        String channelAddress = "";

        if(channel.equals("signup")){
            channelAddress = SlackConstant.SIGNUP_CHANNEL;
        } else if(channel.equals("post")){
            channelAddress = SlackConstant.POST_CREATE_CHANNEL;
        } else if(channel.equals("error"))
            channelAddress = SlackConstant.ERROR_CHANNEL;

        try{
            MethodsClient methods = Slack.getInstance().methods(slackToken);

            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channelAddress)
                    .text(message)
                    .build();

            methods.chatPostMessage(request);

            log.info("Slack post message success");
        } catch (SlackApiException | IOException e) {
            log.error(e.getMessage());
        }
    }
}
