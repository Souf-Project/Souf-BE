package com.souf.soufwebsite.domain.inquiry.repository;

import com.souf.soufwebsite.domain.inquiry.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, InquiryCustomRepository {


    @Query(
            value = """
                SELECT inq
                FROM Inquiry inq
                JOIN FETCH inq.member m
                WHERE (:email is null or m.email = :email)
                ORDER BY inq.createdTime DESC
            """,
            countQuery = """
                    SELECT COUNT(inq)
                    FROM Inquiry inq
                    JOIN inq.member m
                    WHERE (:email is null or m.email = :email)
                    ORDER BY inq.createdTime DESC
                    """
    )
    Page<Inquiry> findByMember(@Param("email") String email, Pageable pageable);

}
