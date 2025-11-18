package com.souf.soufwebsite.domain.recruit.contract.dto.res.lambda;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LambdaPdfBody(
        String bucket,
        String key,
        String url,
        String sha256,
        @JsonProperty("request_id") String requestId
) {
}
