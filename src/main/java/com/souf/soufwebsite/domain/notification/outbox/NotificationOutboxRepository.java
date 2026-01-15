package com.souf.soufwebsite.domain.notification.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationOutboxRepository extends JpaRepository<NotificationOutbox, Long> {

    @Query(
            value = """
              select *
              from notification_outbox
              where status = 'PENDING'
              and next_retry_at <= :now
              order by id
              for update skip locked
             """, nativeQuery = true
    )
    List<NotificationOutbox> findDue(@Param("now") LocalDateTime now);
}