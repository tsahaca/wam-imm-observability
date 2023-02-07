package com.westernasset.imm.tracing.service;

import com.westernasset.imm.tracing.data.TradeVO;
import com.westernasset.imm.tracing.data.exception.NotFoundException;
import com.westernasset.imm.tracing.persistence.entity.Trade;
import com.westernasset.imm.tracing.persistence.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    private final KafkaTemplate<String, TradeVO> kafkaTemplate;

    @Value(value = "${kafka.topic}")
    private String topic;

    public TradeVO saveTrade(TradeVO tradeVO) throws Exception {
       // tradeVO.setTradeId(Strings.isBlank(tradeVO.getTradeId()) ? UUID.randomUUID().toString() : tradeVO.getTradeId());
        Trade trade = Trade.builder()
              //  .tradeId(tradeVO.getTradeId())
                .ticketType(tradeVO.getTicketType())
                .assetId(tradeVO.getAssetId())
                .pfNumber(tradeVO.getPfNumber())
                .parAmount(tradeVO.getParAmount())
                .build();
        tradeRepository.save(trade);
        kafkaTemplate.send(this.topic, tradeVO);
        return tradeVO;
    }

    public TradeVO getTrade(long tradeId) {
        Trade trade =
                tradeRepository.findByTradeId(tradeId).orElseThrow(() ->
                        new NotFoundException("Could not find Trade with tradeId: " + tradeId));

        TradeVO tradeVO = TradeVO.builder()
                .tradeId(tradeId)
                .ticketType(trade.getTicketType())
                .assetId(trade.getAssetId())
                .pfNumber(trade.getPfNumber())
                .parAmount(trade.getParAmount())
                .build();
        return tradeVO;
    }

    public List<TradeVO> getTrades() {
        List<Trade> trades = tradeRepository.findAll();

        List<TradeVO> tradeVOS = trades.stream()
                .map(trade -> TradeVO.builder()
                        .tradeId(trade.getTradeId())
                        .ticketType(trade.getTicketType())
                        .assetId(trade.getAssetId())
                        .pfNumber(trade.getPfNumber())
                        .parAmount(trade.getParAmount())
                        .build())
                .collect(toList());

        return tradeVOS;
    }

    public void updateTrade(long tradeId, TradeVO tradeVO) throws Exception {
        Trade trade =
                tradeRepository.findByTradeId(tradeId).orElseThrow(() ->
                        new NotFoundException("Could not find Trade with tradeId: " + tradeId));
        trade.setTicketType(tradeVO.getTicketType());
        trade.setAssetId(tradeVO.getAssetId());
        trade.setPfNumber(tradeVO.getPfNumber());
        trade.setParAmount(tradeVO.getParAmount());
        tradeRepository.save(trade);
        kafkaTemplate.send(this.topic, tradeVO);
    }

    public void deleteTrade(long tradeId) throws Exception {
        Trade trade =
                tradeRepository.findByTradeId(tradeId).orElseThrow(() ->
                        new NotFoundException("Could not find Trade with tradeId: " + tradeId));
        tradeRepository.delete(trade);
        TradeVO tradeVO = TradeVO.builder().tradeId(tradeId).build();
        kafkaTemplate.send(this.topic, tradeVO);
    }
}
