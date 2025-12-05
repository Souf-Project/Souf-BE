package com.souf.soufwebsite.domain.recruit.contract.dto.res.lambda;

import java.util.Map;

public record LambdaWrapperRes(
        int statusCode,
        Map<String, String> headers,
        String body
) {
}
