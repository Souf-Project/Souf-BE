package com.souf.soufwebsite.global.config;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.regions.Region;
import org.apache.http.HttpRequestInterceptor;
import io.github.acm19.aws.interceptor.http.AwsRequestSigningApacheInterceptor;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;


public class AwsSigV4Interceptor {

    public static HttpRequestInterceptor buildWithIamRole(String serviceName, String region, String roleArn) {
        StsClient stsClient = StsClient.builder()
                .region(Region.of(region))
                .build();

        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                .roleArn(roleArn)
                .roleSessionName("opensearch-session-" + System.currentTimeMillis())
                .build();

        AwsCredentialsProvider credentialsProvider = StsAssumeRoleCredentialsProvider.builder()
                .stsClient(stsClient)
                .refreshRequest(assumeRoleRequest)
                .build();

        return new AwsRequestSigningApacheInterceptor(
                serviceName,
                AwsV4HttpSigner.create(),
                credentialsProvider,
                Region.of(region)
        );
    }
}