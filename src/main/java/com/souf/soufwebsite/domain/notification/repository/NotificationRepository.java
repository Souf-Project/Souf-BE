package com.souf.soufwebsite.domain.notification.repository;

import com.souf.soufwebsite.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 🔹 목록 조회
    Page<Notification> findByMemberIdOrderByCreatedTimeDesc(Long memberId, Pageable pageable);

    // 🔹 개수 조회
    long countByMemberIdAndReadFalse(Long memberId);
    long countByMemberId(Long memberId);

    // 🔹 단건 읽음 처리 (Row 수 반환)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Notification n
           set n.read = true
         where n.id = :id
           and n.memberId = :memberId
           and n.read = false
        """)
    int markOneRead(@Param("id") Long id, @Param("memberId") Long memberId);

    // 🔹 전체 읽음 처리 (영향받은 개수 반환)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Notification n
           set n.read = true
         where n.memberId = :memberId
           and n.read = false
        """)
    int markAllRead(@Param("memberId") Long memberId);

    // 🔹 유저별 최신 N개만 남기고 나머지 삭제 (MySQL) - 내부 유지용이라 void
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        DELETE FROM notification
         WHERE member_id = :memberId
           AND id NOT IN (
               SELECT id FROM (
                   SELECT id
                     FROM notification
                    WHERE member_id = :memberId
                    ORDER BY created_time DESC
                    LIMIT :keep
               ) t
           )
        """, nativeQuery = true)
    void trimToMax(@Param("memberId") Long memberId, @Param("keep") int keep);

    // 🔹 단건 삭제 (성공/실패 구분 위해 int)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from Notification n
         where n.id = :id
           and n.memberId = :memberId
        """)
    int deleteOne(@Param("id") Long id, @Param("memberId") Long memberId);

    // 🔹 전체 삭제 (몇 개 지웠는지 알 수 있음)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from Notification n
         where n.memberId = :memberId
        """)
    int deleteAllByMemberId(@Param("memberId") Long memberId);
}