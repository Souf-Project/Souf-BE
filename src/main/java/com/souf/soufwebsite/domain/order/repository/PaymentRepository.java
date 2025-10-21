package com.souf.soufwebsite.domain.order.repository;

import com.souf.soufwebsite.domain.order.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
