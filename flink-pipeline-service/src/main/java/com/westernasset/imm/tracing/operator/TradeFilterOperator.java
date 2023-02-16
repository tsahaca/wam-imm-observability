package com.westernasset.imm.tracing.operator;

import com.westernasset.imm.tracing.model.TradeVO;
import org.apache.flink.api.common.functions.FilterFunction;

public class TradeFilterOperator implements FilterFunction<TradeVO> {
    @Override
    public boolean filter(TradeVO tradeVO) throws Exception {
        return "FIXED".equalsIgnoreCase(tradeVO.getTicketType());
    }
}
