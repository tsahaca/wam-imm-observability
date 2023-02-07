package com.westernasset.imm.tracing.service;

import com.westernasset.imm.tracing.persistence.entity.Trade;
import com.westernasset.imm.tracing.persistence.repository.TradeRepository;
import com.westernasset.imm.tracing.data.event.TradeWasCreated;
import com.westernasset.imm.tracing.data.event.TradeWasDeleted;
import com.westernasset.imm.tracing.data.event.TradeWasUpdated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class TradePreClearService {

    private final TradeRepository tradeRepository;

    public void consumeTradeWasCreated(TradeWasCreated tradeWasCreated) {
        Long tradeId = tradeWasCreated.getTradeId();
        Optional<Trade> tradeOptional = tradeRepository.findByTradeId(tradeId);
        if (!(tradeOptional).isPresent()) {
            log.info("Trade not existing: ", tradeId);
            Trade trade = Trade.builder()
                    .tradeId(tradeWasCreated.getTradeId())
                    .ticketType(tradeWasCreated.getTicketType())
                    .assetId(tradeWasCreated.getAssetId())
                    .pfNumber(tradeWasCreated.getPfNumber())
                    .parAmount(tradeWasCreated.getParAmount())
                    .build();
            tradeRepository.save(trade);
        }
    }

    public void consumeTradeWasUpdated(TradeWasUpdated tradeWasUpdated) {
        Long tradeId = tradeWasUpdated.getTradeId();
        Optional<Trade> tradeOptional = tradeRepository.findByTradeId(tradeId);
        if (tradeOptional.isPresent()) {
            log.info("Trade updated: ", tradeId);
            tradeOptional.get().setTicketType(tradeWasUpdated.getTicketType());
            tradeOptional.get().setAssetId(tradeWasUpdated.getAssetId());
            tradeOptional.get().setPfNumber(tradeWasUpdated.getPfNumber());
            tradeOptional.get().setParAmount(tradeWasUpdated.getParAmount());
            tradeRepository.save(tradeOptional.get());
        }
    }

    public void consumeTradeWasDeleted(TradeWasDeleted tradeWasDeleted) {
        Long tradeId = tradeWasDeleted.getTradeId();
        Optional<Trade> tradeOptional = tradeRepository.findByTradeId(tradeId);
        if (tradeOptional.isPresent()) {
            log.info("Trade deleted: ", tradeId);
            tradeRepository.delete(tradeOptional.get());
        }
    }
}
