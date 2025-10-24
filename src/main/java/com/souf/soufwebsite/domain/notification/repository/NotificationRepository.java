package com.souf.soufwebsite.domain.notification.repository;

import com.souf.soufwebsite.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByMemberIdOrderByCreatedTimeDesc(Long memberId, Pageable pageable);

    long countByMemberIdAndReadFalse(Long memberId);
    long countByMemberId(Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Notification n set n.read = true where n.memberId = :memberId and n.read = false")
    int markAllRead(@Param("memberId") Long memberId);

    // ✅ 유저별 최신 50개만 남기고 나머지 삭제 (MySQL 기준)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        DELETE FROM notification 
        WHERE member_id = :memberId
          AND id NOT IN (
              SELECT id FROM (
                  SELECT id FROM notification
                  WHERE member_id = :memberId
                  ORDER BY created_time DESC
                  LIMIT :keep
              ) t
          )
        """, nativeQuery = true)
    int trimToMax(@Param("memberId") Long memberId, @Param("keep") int keep);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Notification n where n.id = :id and n.memberId = :memberId")
    int deleteOne(@Param("id") Long id, @Param("memberId") Long memberId);
}