package com.souf.soufwebsite.domain.application.dto.req;

import jakarta.validation.constraints.NotNull;

public record ApplicationDecisionReqDto(
        @NotNull ApplicationDecision decision
) {}
