package com.westernasset.imm.tracing.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.westernasset.imm.tracing.model.TradeVO;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class TradeDeserializationSchema implements DeserializationSchema<TradeVO> {

    static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public TradeVO deserialize(byte[] bytes) throws IOException {

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