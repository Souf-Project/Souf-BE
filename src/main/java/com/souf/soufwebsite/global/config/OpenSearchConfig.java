package com.souf.soufwebsite.global.config;

import jakarta.annotation.PostConstruct;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
//import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import software.amazon.awssdk.services.opensearch.OpenSearchClient;
//import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.regions.Region;

import java.net.URI;

@Configuration
public class OpenSearchConfig {
    @Value("${opensearch.host}")
    private String host;

    @Value("${opensearch.username}")
    private  String username;

    @Value("${opensearch.password}")
    private String password;

    @PostConstruct
    public void debugCredentials() {
        System.out.println("🔑 OpenSearch 연결 정보");
        System.out.println("➡️ host = " + host);
//        System.out.println("➡️ username = " + username);
//        System.out.println("➡️ password = " + password);
    }

//    @Bean  // 마스터 계정 사용 방식
//    public OpenSearchClient openSearchClient() {
//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
//
//        RestClient restClient = RestClient.builder(HttpHost.create(host))
//                .setHttpClientConfigCallback(httpClientBuilder ->
//                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
//                .setRequestConfigCallback(requestConfigBuilder ->
//                        requestConfigBuilder
//                                .setConnectTimeout(5000)    // 연결 타임아웃
//                                .setSocketTimeout(30000))   // 소켓 타임아웃
//                .build();
//
//        return new OpenSearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
//    }


    @Value("${aws.region}")
    private String region;

    @Bean
    public OpenSearchClient openSearchClient() {
        AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();

        return OpenSearchClient.builder()
                .endpointOverride(URI.create(host))
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}