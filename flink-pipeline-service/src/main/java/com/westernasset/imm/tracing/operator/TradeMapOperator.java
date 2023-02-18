package com.westernasset.imm.tracing.operator;

import com.westernasset.imm.tracing.model.TradeVO;
import org.apache.flink.api.common.functions.MapFunction;

public class TradeMapOperator implements MapFunction<TradeVO,TradeVO> {
    @Override
    public TradeVO map(TradeVO tradeVO) throws Exception {
        tradeVO.setStatus("PROCESSED_BY_FLINK");
        return tradeVO;
    }
}
