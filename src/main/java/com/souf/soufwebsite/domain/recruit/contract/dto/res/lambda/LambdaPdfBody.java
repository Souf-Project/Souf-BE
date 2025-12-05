package com.souf.soufwebsite.domain.recruit.contract.dto.res.lambda;

public record LambdaPdfBody(
        Boolean ok,
        String local,
        String bucket,
        String key,
        String sha256
) {
}
