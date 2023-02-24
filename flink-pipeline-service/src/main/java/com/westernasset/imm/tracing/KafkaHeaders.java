package com.westernasset.imm.tracing;

import com.westernasset.imm.tracing.events.EnrichedEvent;
import com.westernasset.imm.tracing.events.KafkaHeadersEventDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;

import java.util.function.Consumer;

public class KafkaHeaders {

    static final String TOPIC = "input";

    public static void main(String[] args) throws Exception {
        runJob();
    }

    static void runJob() throws Exception {
        KafkaSource<EnrichedEvent> source =
                KafkaSource.<EnrichedEvent>builder()
                        .setBootstrapServers("kafka:9092")
                        .setTopics(TOPIC)
                        .setStartingOffsets(OffsetsInitializer.earliest())
                        .setDeserializer(new KafkaHeadersEventDeserializationSchema())
                        .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        defineWorkflow(env, source, workflow -> workflow.addSink(new PrintSinkFunction<>()));
        env.execute();
    }

    static void defineWorkflow(
            StreamExecutionEnvironment env,
            Source<EnrichedEvent, ?, ?> source,
            Consumer<DataStream<EnrichedEvent>> sinkApplier) {
        final DataStreamSource<EnrichedEvent> kafka =
                env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka");

        // additional workflow steps go here

        sinkApplier.accept(kafka);
    }
}
