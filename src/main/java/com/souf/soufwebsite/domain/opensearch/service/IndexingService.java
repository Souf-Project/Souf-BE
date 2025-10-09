//package com.souf.soufwebsite.domain.opensearch.service;
//
////import lombok.RequiredArgsConstructor;
////import org.opensearch.client.opensearch.OpenSearchClient;
////import org.springframework.stereotype.Service;
////
////import java.io.IOException;
////
////@RequiredArgsConstructor
////@Service
////public class IndexingService {
////    private final OpenSearchClient client;
////
////    public <T> void indexDocument(String indexName, String id, T document) throws IOException {
////        client.index(i -> i
////                .index(indexName)
////                .id(id)
////                .document(document)
////        );
////    }
////
////    public void deleteDocument(String indexName, String id) throws IOException {
////        client.delete(d -> d.index(indexName).id(id));
////    }
////}
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class IndexingService {
//
//    private final RestTemplate openSearchRestTemplate;
//
//    @Value("${opensearch.host}")
//    private String host;
//
//    /**
//     * 문서를 인덱싱합니다.
//     */
//    public <T> void indexDocument(String indexName, String id, T document) {
//        String url = host + "/" + indexName + "/_doc/" + id;
//
//        try {
//            HttpHeaders headers = getJsonHeaders();
//            HttpEntity<T> request = new HttpEntity<>(document, headers);
//
//            openSearchRestTemplate.put(url, request);
//            log.info("✅ 문서 인덱싱 성공: " + url);
//        } catch (Exception e) {
//            log.error("문서 인덱싱 실패: " + url, e);
//        }
//    }
//
//    /**
//     * 문서를 삭제합니다.
//     */
//    public void deleteDocument(String indexName, String id) {
//        String url = host + "/" + indexName + "/_doc/" + id;
//
//        try {
//            openSearchRestTemplate.delete(url);
//            log.info("🗑️ 문서 삭제 성공: " + url);
//        } catch (Exception e) {
//            log.error("문서 삭제 실패: " + url, e);
//        }
//    }
//
//    private HttpHeaders getJsonHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        return headers;
//    }
//}