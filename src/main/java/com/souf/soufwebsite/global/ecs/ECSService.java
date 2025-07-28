package com.souf.soufwebsite.global.ecs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

@Service
@RequiredArgsConstructor
public class ECSService {

    private final EcsClient ecsClient;

    @Value("${cloud.aws.ecs.cluster}")
    private String clusterName;

    @Value("${cloud.aws.ecs.task-definition}")
    private String taskDefinition;

    @Value("${cloud.aws.ecs.subnet}")
    private String subnet;

    @Value("${cloud.aws.ecs.security-group}")
    private String securityGroup;

    public void triggerThumbnailJob(String videoUrl, String prefix){
        ecsClient.runTask(r -> r
                .cluster(clusterName)
                .taskDefinition(taskDefinition)
                .launchType(LaunchType.FARGATE)
                .overrides(TaskOverride.builder()
                        .containerOverrides(ContainerOverride.builder()
                                .name("ffmpeg-container-souf")
                                .command(prefix, videoUrl)
                                .build())
                        .build())
                .networkConfiguration(NetworkConfiguration.builder()
                        .awsvpcConfiguration(AwsVpcConfiguration.builder()
                                .subnets(subnet)
                                .assignPublicIp(AssignPublicIp.ENABLED)
                                .securityGroups(securityGroup)
                                .build())
                        .build())
        );
    }
}
