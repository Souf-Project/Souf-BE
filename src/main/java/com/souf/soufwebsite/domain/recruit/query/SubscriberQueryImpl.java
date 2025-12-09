package com.souf.soufwebsite.domain.recruit.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriberQueryImpl implements SubscriberQuery {
    @PersistenceContext
    private final EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<Long> findSubscriberIdsByFirst(Long firstCategoryId) {
        String sql = """
        SELECT mcm.member_member_id
        FROM member_category_mapping mcm
        WHERE mcm.first_category_first_category_id = ?1
        """;

        List<Number> rows = em.createNativeQuery(sql)
                .setParameter(1, firstCategoryId)
                .getResultList();

        return rows.stream()
                .map(Number::longValue)
                .distinct()
                .toList();
    }
}