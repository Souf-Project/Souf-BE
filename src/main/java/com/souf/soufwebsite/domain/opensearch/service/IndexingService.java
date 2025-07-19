package com.souf.soufwebsite.domain.opensearch.service;

import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class IndexingService {
    private final OpenSearchClient client;

    public <T> void indexDocument(String indexName, String id, T document) throws IOException {
        client.index(i -> i
                .index(indexName)
                .id(id)
                .document(document)
        );
    }

    public void deleteDocument(String indexName, String id) throws IOException {
        client.delete(d -> d.index(indexName).id(id));
    }
}