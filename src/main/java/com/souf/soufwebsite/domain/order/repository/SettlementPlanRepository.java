package com.souf.soufwebsite.domain.order.repository;

import com.souf.soufwebsite.domain.order.entity.SettlementPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementPlanRepository extends JpaRepository<SettlementPlan, Long> {
}
