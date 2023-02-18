package com.westernasset.imm.tracing.connector;

import com.westernasset.imm.tracing.model.TradeVO;
import com.westernasset.imm.tracing.schema.TradeDeserializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;



public class Consumers {

    public static FlinkKafkaConsumer<String> createStringConsumerForTopic(String topic, String kafkaAddress, String kafkaGroup) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", kafkaAddress);
        props.setProperty("group.id", kafkaGroup);
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(topic, new SimpleStringSchema(), props);

        return consumer;
    }


    public static FlinkKafkaConsumer<TradeVO> createTradeMessageConsumer(String topic, String kafkaAddress, String kafkaGroup) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", kafkaAddress);
        properties.setProperty("group.id", kafkaGroup);
        FlinkKafkaConsumer<TradeVO> consumer = new FlinkKafkaConsumer<TradeVO>(topic, new TradeDeserializationSchema(), properties);
        return consumer;
    }
}