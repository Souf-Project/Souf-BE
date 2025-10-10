//package com.souf.soufwebsite.global.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.DefaultUriBuilderFactory;
//
//import org.apache.hc.client5.http.classic.HttpClient;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
//import org.apache.hc.client5.http.impl.classic.HttpClients;
//import org.apache.hc.client5.http.config.RequestConfig;
//import org.apache.hc.core5.http.HttpRequestInterceptor;
//import org.apache.hc.core5.util.Timeout;
//
//import io.github.acm19.aws.interceptor.http.AwsRequestSigningApacheV5Interceptor;
//import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
//import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
//import software.amazon.awssdk.regions.Region;
//
//
//import java.time.Duration;
//
//@Configuration
//public class OpenSearchRestTemplateConfig {
//
//    @Value("${opensearch.host}")
//    private String host;
//
//    /**
//     * OpenSearch REST 호출용 RestTemplate 빈
//     * - Apache HTTPClient 5.x 사용
//     * - AWS IAM + SigV4 로그인 자동 서명 적용
//     */
//    @Bean
//    public RestTemplate openSearchRestTemplate() {
//        DefaultCredentialsProvider creds = DefaultCredentialsProvider.create();
//
//        // 1. SigV4 서명 인터셉터 (OpenSearch는 "es" 서비스명)
//        HttpRequestInterceptor signerInterceptor = new AwsRequestSigningApacheV5Interceptor(
//                "es",
//                AwsV4HttpSigner.create(),
//                creds,
//                Region.of("ap-northeast-2")
//        );
//
//        // 2. HttpClient 5.x 타임아웃 설정
//        RequestConfig config = RequestConfig.custom()
//                .setConnectTimeout(Timeout.ofSeconds(5))
//                .setResponseTimeout(Timeout.ofSeconds(30))
//                .setConnectionRequestTimeout(Timeout.ofSeconds(2))
//                .build();
//
//        CloseableHttpClient httpClient = HttpClients.custom()
//                .setDefaultRequestConfig(config)
//                .addRequestInterceptorLast(signerInterceptor)
//                .build();
//
//        // 3. HttpComponentsClientHttpRequestFactory 생성 (HTTPClient5.x 호환)
//        HttpComponentsClientHttpRequestFactory factory =
//                new HttpComponentsClientHttpRequestFactory((HttpClient) httpClient);
//
//        factory.setConnectTimeout(Duration.ofSeconds(5));
//        factory.setConnectionRequestTimeout(Duration.ofSeconds(2));
//        // (read timeout는 HC5 RequestConfig에서 관리)
//
//        // 4. RestTemplate에 OpenSearch 호스트 베이스 URL 등록
//        RestTemplate rest = new RestTemplate(factory);
//        rest.setUriTemplateHandler(new DefaultUriBuilderFactory(host));
//
//        return rest;
//    }
//}