//package com.souf.soufwebsite.domain.opensearch.service;
////
////import com.fasterxml.jackson.databind.node.ObjectNode;
////import com.souf.soufwebsite.domain.opensearch.dto.SearchResDto;
////import lombok.RequiredArgsConstructor;
////import lombok.extern.slf4j.Slf4j;
////import org.opensearch.client.opensearch.OpenSearchClient;
////import org.opensearch.client.opensearch.core.SearchResponse;
////import org.springframework.stereotype.Service;
////
////import java.io.IOException;
////import java.util.ArrayList;
////import java.util.List;
////
////@Slf4j
////@RequiredArgsConstructor
////@Service
////public class SearchService {
////    private final OpenSearchClient client;
////
////    public List<SearchResDto> searchAll(String keyword, int page, int size) throws IOException {
////        List<SearchResDto> results = new ArrayList<>();
////        int from = page * size;
////        log.info("running searchAll with keyword: {}", keyword);
////        // Recruit 인덱스 검색
////        SearchResponse<ObjectNode> recruitResponse = client.search(s -> s
////                        .index("recruit")
////                        .from(from)
////                        .size(size)
////                        .query(q -> q.multiMatch(m -> m
////                                .query(keyword)
////                                .fields("title", "content")
////                        )),
////                ObjectNode.class
////        );
////        log.info("Recruit search results: {}", recruitResponse.hits().hits().size());
////
////        // Feed 인덱스 검색
////        SearchResponse<ObjectNode> feedResponse = client.search(s -> s
////                        .index("feed")
////                        .from(from)
////                        .size(size)
////                        .query(q -> q.multiMatch(m -> m
////                                .query(keyword)
////                                .fields("topic", "content")
////                        )),
////                ObjectNode.class
////        );
////
////        // Member 인덱스 검색
////        SearchResponse<ObjectNode> memberResponse = client.search(s -> s
////                        .index("member")
////                        .from(from)
////                        .size(size)
////                        .query(q -> q.multiMatch(m -> m
////                                .query(keyword)
////                                .fields("nickname")
////                        )),
////                ObjectNode.class
////        );
////
////        // 결과 합치기
////        results.addAll(processResponse(recruitResponse));
////        results.addAll(processResponse(feedResponse));
////        results.addAll(processResponse(memberResponse));
////
////        return results;
////    }
////
////    private List<SearchResDto> processResponse(SearchResponse<ObjectNode> response) {
////        return response.hits().hits().stream()
////                .map(hit -> {
////                    String indexName = hit.index();
////                    ObjectNode source = hit.source();
////                    return new SearchResDto(indexName, source.toString());
////                })
////                .toList();
////    }
////
////}
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.souf.soufwebsite.domain.opensearch.dto.SearchResDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class SearchService {
//
//    private final RestTemplate openSearchRestTemplate;
//    private final ObjectMapper objectMapper;
//
//    @Value("${opensearch.host}")
//    private String openSearchHost;
//
//    public List<SearchResDto> searchAll(String keyword, int page, int size) {
//        int from = page * size;
//
//        List<SearchResDto> results = new ArrayList<>();
//        results.addAll(searchIndex("recruit", keyword, from, size, "title", "content"));
//        results.addAll(searchIndex("feed", keyword, from, size, "topic", "content"));
//        results.addAll(searchIndex("member", keyword, from, size, "nickname"));
//
//        return results;
//    }
//
//    private List<SearchResDto> searchIndex(String indexName, String keyword, int from, int size, String... fields) {
//        try {
//            // 1. 요청 URL
//            String url = openSearchHost + "/" + indexName + "/_search";
//
//            // 2. 요청 바디 구성 (multi_match query)
//            StringBuilder fieldList = new StringBuilder();
//            for (int i = 0; i < fields.length; i++) {
//                fieldList.append("\"").append(fields[i]).append("\"");
//                if (i < fields.length - 1) fieldList.append(", ");
//            }
//
//            String body = """
//                {
//                  "from": %d,
//                  "size": %d,
//                  "query": {
//                    "multi_match": {
//                      "query": "%s",
//                      "fields": [%s]
//                    }
//                  }
//                }
//                """.formatted(from, size, keyword, fieldList);
//
//            // 3. 요청 보내기
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> entity = new HttpEntity<>(body, headers);
//
//            ResponseEntity<String> response = openSearchRestTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//            JsonNode json = objectMapper.readTree(response.getBody());
//
//            // 4. 결과 파싱
//            List<SearchResDto> hits = new ArrayList<>();
//            Iterator<JsonNode> iterator = json.path("hits").path("hits").elements();
//            while (iterator.hasNext()) {
//                JsonNode hit = iterator.next();
//                JsonNode source = hit.path("_source");
//                hits.add(new SearchResDto(indexName, source.toString()));
//            }
//
//            log.info("✅ {} 검색 결과 {}건", indexName, hits.size());
//            return hits;
//
//        } catch (Exception e) {
//            log.warn("❌ {} 인덱스 검색 중 오류 발생", indexName, e);
//            return List.of();
//        }
//    }
//}