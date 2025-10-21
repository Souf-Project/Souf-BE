package com.souf.soufwebsite.domain.order.repository;

import com.souf.soufwebsite.domain.order.entity.Payout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutRepository extends JpaRepository<Payout, Long> {

}
