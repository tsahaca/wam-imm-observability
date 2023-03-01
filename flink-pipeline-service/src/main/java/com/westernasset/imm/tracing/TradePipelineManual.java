package com.westernasset.imm.tracing;

import com.westernasset.imm.tracing.model.EnrichedTradeVO;
import com.westernasset.imm.tracing.model.TradeVO;
import com.westernasset.imm.tracing.operator.EnrichedTradeFilterOperator;
import com.westernasset.imm.tracing.operator.EnrichedTradeMapOperator;
import com.westernasset.imm.tracing.operator.TradeFilterOperator;
import com.westernasset.imm.tracing.operator.TradeMapOperator;
import com.westernasset.imm.tracing.schema.TradeDeserializationSchema;
import com.westernasset.imm.tracing.schema.TradeKafkaHeadersDeserializationSchema;
import com.westernasset.imm.tracing.schema.TradeSerializationSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

@Slf4j
public class TradePipelineManual {

    final static String inputTopic = "orders-topic";
    final static String outputTopic = "trade-output";
    final static String jobTitle = "TradePipeline";

    public static void main(String[] args) throws Exception {
        final String bootstrapServers = args.length > 0 ? args[0] : "kafka:9092";

        // Set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<EnrichedTradeVO> source = KafkaSource.<EnrichedTradeVO>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(inputTopic)
                .setGroupId("flink-otel")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(new TradeKafkaHeadersDeserializationSchema())
                .build();
        /**
        final Tracer tracer = GlobalOpenTelemetry.get().getTracer("io.opentelemetry.kafka-clients-0.11", "1.22.1-alpha");
        Span parentSpan = tracer.spanBuilder("parent").startSpan();
        parentSpan.makeCurrent();

        Span span = Span.current();
        log.info("TradePipelineJob TRACE_ID={}, SPAN_ID={}",  span.getSpanContext().getTraceId(),
                span.getSpanContext().getSpanId());
         */

        FlinkKafkaProducer<TradeVO> producer = new FlinkKafkaProducer<TradeVO>(bootstrapServers, outputTopic, new TradeSerializationSchema());

        DataStream<EnrichedTradeVO> inputTrades = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Trade Source");
        /**
         * Filter
         */
        DataStream<EnrichedTradeVO> fixedIncomeTrades = inputTrades.filter(new EnrichedTradeFilterOperator());

        /**
         * Map to enrich
         */
        DataStream<TradeVO> outputTrades = fixedIncomeTrades.map(new EnrichedTradeMapOperator());



        // Add the sink to so results
        // are written to the outputTopic
        outputTrades.addSink(producer).name("Trade-Sink").uid("Trade-Sink");

        // Execute program
        env.execute(jobTitle);
    }
}