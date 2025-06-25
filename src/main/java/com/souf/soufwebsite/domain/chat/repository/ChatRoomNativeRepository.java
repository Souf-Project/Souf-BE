package com.souf.soufwebsite.domain.chat.repository;

import com.souf.soufwebsite.domain.chat.dto.ChatRoomSummaryDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomNativeRepository {

    @PersistenceContext
    private final EntityManager em;

    public List<ChatRoomSummaryDto> getChatRoomSummaries(Long userId) {
        String sql = """
            SELECT
                r.chatroom_id AS roomId,
                CASE
                    WHEN r.sender_id = :userId THEN m2.nickname
                    ELSE m1.nickname
                END AS opponentNickname,

                CASE
                    WHEN r.sender_id = :userId THEN m2.profile_image_url
                    ELSE m1.profile_image_url
                END AS opponentProfileImageUrl,

                (
                    SELECT cm.content
                    FROM chat_message cm
                    WHERE cm.chatroom_id = r.chatroom_id
                    ORDER BY cm.created_time DESC
                    LIMIT 1
                ) AS lastMessage,

                (
                    SELECT cm.created_time
                    FROM chat_message cm
                    WHERE cm.chatroom_id = r.chatroom_id
                    ORDER BY cm.created_time DESC
                    LIMIT 1
                ) AS lastMessageTime,

                (
                    SELECT COUNT(*)
                    FROM chat_message cm2
                    WHERE cm2.chatroom_id = r.chatroom_id
                      AND cm2.sender_id != :userId
                      AND cm2.is_read = false
                ) AS unreadCount

            FROM chat_room r
            JOIN member m1 ON r.sender_id = m1.member_id
            JOIN member m2 ON r.receiver_id = m2.member_id
            WHERE r.sender_id = :userId OR r.receiver_id = :userId
            ORDER BY lastMessageTime DESC
        """;

        List<Object[]> resultList = em.createNativeQuery(sql)
                .setParameter("userId", userId)
                .getResultList();

        return resultList.stream()
                .map(row -> {
                    String lastMessage = row[3] != null ? (String) row[3] : "대화를 시작해보세요";
                    LocalDateTime lastMessageTime = null;
                    if (row[4] != null) {
                        lastMessageTime = ((Timestamp) row[4]).toLocalDateTime();
                    }

                    return new ChatRoomSummaryDto(
                            ((Number) row[0]).longValue(),     // roomId
                            (String) row[1],                   // opponentNickname
                            (String) row[2],                   // opponentProfileImageUrl
                            (String) row[3],                   // lastMessage
                            lastMessageTime,                   // lastMessageTime (nullable)
                            ((Number) row[5]).intValue()       // unreadCount
                    );
                })
                .toList();
    }
}
