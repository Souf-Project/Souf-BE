package com.souf.soufwebsite.domain.opensearch.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.souf.soufwebsite.domain.opensearch.dto.SearchResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchService {
    private final OpenSearchClient client;

    public List<SearchResDto> searchAll(String keyword) throws IOException {
        List<SearchResDto> results = new ArrayList<>();
        log.info("running searchAll with keyword: {}", keyword);
        // Recruit 인덱스 검색
        SearchResponse<ObjectNode> recruitResponse = client.search(s -> s
                        .index("recruit")
                        .query(q -> q.multiMatch(m -> m
                                .query(keyword)
                                .fields("title", "content")
                        ))
                        .size(20),
                ObjectNode.class
        );
        log.info("Recruit search results: {}", recruitResponse.hits().hits().size());

        // Feed 인덱스 검색
        SearchResponse<ObjectNode> feedResponse = client.search(s -> s
                        .index("feed")
                        .query(q -> q.multiMatch(m -> m
                                .query(keyword)
                                .fields("topic", "content")
                        ))
                        .size(20),
                ObjectNode.class
        );

        // Member 인덱스 검색
        SearchResponse<ObjectNode> memberResponse = client.search(s -> s
                        .index("member")
                        .query(q -> q.multiMatch(m -> m
                                .query(keyword)
                                .fields("nickname")
                        ))
                        .size(10),
                ObjectNode.class
        );

        // 결과 합치기
        results.addAll(processResponse(recruitResponse));
        results.addAll(processResponse(feedResponse));
        results.addAll(processResponse(memberResponse));

        return results;
    }

    private List<SearchResDto> processResponse(SearchResponse<ObjectNode> response) {
        return response.hits().hits().stream()
                .map(hit -> {
                    String indexName = hit.index();
                    ObjectNode source = hit.source();
                    return new SearchResDto(indexName, source.toString());
                })
                .toList();
    }

}