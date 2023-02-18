package com.westernasset.imm.tracing;

import com.westernasset.imm.tracing.model.TradeVO;
import com.westernasset.imm.tracing.operator.TradeFilterOperator;
import com.westernasset.imm.tracing.operator.TradeMapOperator;
import com.westernasset.imm.tracing.schema.TradeDeserializationSchema;
import com.westernasset.imm.tracing.schema.TradeSerializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

public class TradePipelineJob {

    final static String inputTopic = "orders-topic";
    final static String outputTopic = "trade-output";
    final static String jobTitle = "TradePipeline";

    public static void main(String[] args) throws Exception {
        final String bootstrapServers = args.length > 0 ? args[0] : "kafka:9092";

        // Set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<TradeVO> source = KafkaSource.<TradeVO>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(inputTopic)
                .setGroupId("trade-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new TradeDeserializationSchema())
                .build();

        FlinkKafkaProducer<TradeVO> producer = new FlinkKafkaProducer<TradeVO>(bootstrapServers, outputTopic, new TradeSerializationSchema());

        DataStream<TradeVO> inputTrades = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Trade Source");
        /**
         * Filter
         */
        DataStream<TradeVO> fixedIncomeTrades = inputTrades.filter(new TradeFilterOperator());

        /**
         * Map to enrich
         */
        DataStream<TradeVO> outputTrades = fixedIncomeTrades.map(new TradeMapOperator());



        // Add the sink to so results
        // are written to the outputTopic
        outputTrades.addSink(producer);

        // Execute program
        env.execute(jobTitle);
    }
}