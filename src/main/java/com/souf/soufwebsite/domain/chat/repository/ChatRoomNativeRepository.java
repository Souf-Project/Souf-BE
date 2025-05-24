package com.souf.soufwebsite.domain.chat.repository;

import com.souf.soufwebsite.domain.chat.dto.ChatRoomSummaryDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
                END AS senderNickname,
                (
                    SELECT content 
                    FROM chat_message cm
                    WHERE cm.chatroom_id = r.chatroom_id
                    ORDER BY cm.created_time DESC
                    LIMIT 1
                ) AS lastMessage,
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
            ORDER BY r.created_time DESC
        """;

        List<Object[]> resultList = em.createNativeQuery(sql)
                .setParameter("userId", userId)
                .getResultList();

        return resultList.stream()
                .map(row -> new ChatRoomSummaryDto(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).intValue()
                ))
                .toList();
    }
}
