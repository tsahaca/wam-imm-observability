package com.westernasset.imm.tracing.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.westernasset.imm.tracing.model.TradeVO;
import io.opentelemetry.api.trace.Span;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import java.io.IOException;

@Slf4j
public class TradeDeserializationSchema implements DeserializationSchema<TradeVO> {
    static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public TradeVO deserialize(byte[] bytes) throws IOException {
        Span span = Span.current();
        log.info("TradeDeserializationSchema TRACE_ID={}, SPAN_ID={}",  span.getSpanContext().getTraceId(),
                span.getSpanContext().getSpanId());
        return objectMapper.readValue(bytes, TradeVO.class);
    }

    @Override
    public boolean isEndOfStream(TradeVO inputMessage) {
        return false;
    }

    @Override
    public TypeInformation<TradeVO> getProducedType() {
        return TypeInformation.of(TradeVO.class);
    }
}
