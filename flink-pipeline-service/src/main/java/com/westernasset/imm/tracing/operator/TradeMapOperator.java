package com.westernasset.imm.tracing.operator;

import com.westernasset.imm.tracing.model.TradeVO;
import io.opentelemetry.api.trace.Span;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.MapFunction;

@Slf4j
public class TradeMapOperator implements MapFunction<TradeVO,TradeVO> {
    @Override
    public TradeVO map(TradeVO tradeVO) throws Exception {
        Span span = Span.current();
        log.info("TradeMapOperator TRACE_ID={}, SPAN_ID={}, TRADE_ID={}",  span.getSpanContext().getTraceId(),
                span.getSpanContext().getSpanId(),
                tradeVO.getTradeId());

        tradeVO.setStatus("PROCESSED_BY_FLINK");
        return tradeVO;
    }
}
