package com.westernasset.imm.tracing.model;

import com.westernasset.imm.tracing.events.Headers;
import com.westernasset.imm.tracing.events.Metadata;

public class EnrichedTradeVO {
    public TradeVO tradeVO;
    public Metadata metadata; // added by Kafka Broker
    public Headers headers; // added by OpenTelemetry Library

    /** A Flink POJO must have a no-args default constructor */
    public EnrichedTradeVO() {}

    public EnrichedTradeVO(TradeVO tradeVO, Metadata metadata, Headers headers) {
        this.tradeVO = tradeVO;
        this.metadata = metadata;
        this.headers = headers;
    }

    @Override
    public String toString() {
        return String.format("EnrichedTradeVO{%n\t%s,%n\t%s,%n\t%s}", tradeVO, metadata, headers);
    }
}
