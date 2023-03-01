package com.westernasset.imm.tracing.operator;

import com.westernasset.imm.tracing.model.EnrichedTradeVO;
import com.westernasset.imm.tracing.model.TradeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.MapFunction;

@Slf4j
public class EnrichedTradeMapOperator implements MapFunction<EnrichedTradeVO,TradeVO> {
    @Override
    public TradeVO map(EnrichedTradeVO tradeVO) throws Exception {
//        Span span = Span.current();
//        log.info("TradeMapOperator TRACE_ID={}, SPAN_ID={}, TRADE_ID={}",  span.getSpanContext().getTraceId(),
//                span.getSpanContext().getSpanId(),
//                tradeVO.getTradeId());

        tradeVO.tradeVO.setStatus("PROCESSED_BY_FLINK");
        return tradeVO.tradeVO;
    }
}
