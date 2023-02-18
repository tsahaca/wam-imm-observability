package com.westernasset.imm.tracing.persistence.repository;

import com.westernasset.imm.tracing.persistence.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface TradeRepository extends JpaRepository<Trade, Long> {
    Optional<Trade> findByTradeId(Long tradeId);
}
