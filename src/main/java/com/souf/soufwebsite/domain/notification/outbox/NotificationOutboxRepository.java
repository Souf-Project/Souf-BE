package com.souf.soufwebsite.domain.notification.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationOutboxRepository extends JpaRepository<NotificationOutbox, Long> {

    @Query("""
        select o from NotificationOutbox o
        where o.status = 'PENDING'
          and o.nextRetryAt <= :now
        order by o.id asc
    """)
    List<NotificationOutbox> findDue(@Param("now") LocalDateTime now);
}