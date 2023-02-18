package com.westernasset.imm.tracing.schema;

import com.westernasset.imm.tracing.model.TradeVO;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;


public class TradeKafkaDeserializationSchema implements KafkaDeserializationSchema<TradeVO>{

    @Override
    public boolean isEndOfStream(TradeVO tradeVO) {
        return false;
    }

    @Override
    public TradeVO deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) throws Exception {
        return null;
    }

    @Override
    public TypeInformation<TradeVO> getProducedType() {
        return null;
    }
}
