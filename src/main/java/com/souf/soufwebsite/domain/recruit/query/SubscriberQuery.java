package com.souf.soufwebsite.domain.recruit.query;

import java.util.List;

public interface SubscriberQuery {
    List<Long> findSubscriberIdsByFirstSecond(Long firstCategoryId, Long secondCategoryId);
}