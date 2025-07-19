package com.souf.soufwebsite.global.config;

import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
@RequiredArgsConstructor
public class OpenSearchInitializer implements ApplicationRunner {

    private final OpenSearchClient client;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createRecruitIndex();
        createFeedIndex();
        createMemberIndex();
    }

    private String getDefaultSettings() {
        return """
    {
      "number_of_shards": 3,
      "number_of_replicas": 1,
      "analysis": {
        "tokenizer": {
          "edge_ngram_tokenizer": {
            "type": "ngram",
            "min_gram": 2,
            "max_gram": 4,
            "token_chars": ["letter", "digit"]
          }
        },
        "analyzer": {
          "edge_ngram_analyzer": {
            "type": "custom",
            "tokenizer": "edge_ngram_tokenizer"
          }
        }
      },
      "index.max_ngram_diff": 8
    }
    """;
    }

    private String getMappings(String... fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"properties\": {\n");
        for (int i = 0; i < fields.length; i++) {
            sb.append("    \"").append(fields[i]).append("\": {\n");
            sb.append("      \"type\": \"text\",\n");
            sb.append("      \"analyzer\": \"edge_ngram_analyzer\"\n");
            sb.append("    }");
            if (i < fields.length - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  }\n}");
        return sb.toString();
    }


    private void createRecruitIndex() throws Exception {
        if (!client.indices().exists(e -> e.index("recruit")).value()) {

            // --- JSON 문자열로 직접 정의 (가장 안전한 방법) ---
            String settingsJson = getDefaultSettings();

            String mappingsJson = getMappings("title", "content");

            // --- InputStream으로 전달 ---
            client.indices().create(c -> c
                    .index("recruit")
                    .settings(s -> s.withJson(new ByteArrayInputStream(settingsJson.getBytes())))
                    .mappings(m -> m.withJson(new ByteArrayInputStream(mappingsJson.getBytes())))
            );

            System.out.println("✅ 인덱스 생성 완료: recruit");
        }
    }

    private void createFeedIndex() throws Exception {
        if (!client.indices().exists(e -> e.index("feed")).value()) {
            String settingsJson = getDefaultSettings();

            String mappingsJson = getMappings("topic", "content");

            client.indices().create(c -> c
                    .index("feed")
                    .settings(s -> s.withJson(new ByteArrayInputStream(settingsJson.getBytes())))
                    .mappings(m -> m.withJson(new ByteArrayInputStream(mappingsJson.getBytes())))
            );

            System.out.println("✅ 인덱스 생성 완료: feed");
        }
    }

    private void createMemberIndex() throws Exception {
        if (!client.indices().exists(e -> e.index("member")).value()) {
            String settingsJson = getDefaultSettings();

            String mappingsJson = getMappings("nickname");

            client.indices().create(c -> c
                    .index("member")
                    .settings(s -> s.withJson(new ByteArrayInputStream(settingsJson.getBytes())))
                    .mappings(m -> m.withJson(new ByteArrayInputStream(mappingsJson.getBytes())))
            );

            System.out.println("✅ 인덱스 생성 완료: member");
        }
    }

}