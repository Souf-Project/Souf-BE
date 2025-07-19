package com.souf.soufwebsite.domain.opensearch.doc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedDoc {
    private String id;
    private String topic;
    private String content;
}