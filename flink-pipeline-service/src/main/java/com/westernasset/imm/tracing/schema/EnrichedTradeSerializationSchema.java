package com.westernasset.imm.tracing.schema;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.westernasset.imm.tracing.model.EnrichedTradeVO;
import com.westernasset.imm.tracing.otel.OtelConfig;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.smallrye.reactive.messaging.TracingMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class EnrichedTradeSerializationSchema implements KafkaSerializationSchema<EnrichedTradeVO> {
    private String topic;
    static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(DeserializationFeature. FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final OpenTelemetry openTelemetry = OtelConfig.initOpenTelemetry2();
    private static final Tracer tracer =
            openTelemetry.getTracer("com.westernasset.imm.tracing.operator.EnrichedTradeSerializationSchema");

    public EnrichedTradeSerializationSchema(String topic){
        this.topic=topic;
    }

    public Span addSpanToIncomingTrace(EnrichedTradeVO tradeVO) {
        TracingMetadata tracingMetadata =TracingMetadata.empty();
        if (tradeVO.headers.traceparent != null) {
            // Read tracing headers
            Context context = openTelemetry.getPropagators().getTextMapPropagator()
                    .extract(Context.root(), tradeVO, getter);

            tracingMetadata = TracingMetadata.withPrevious(context);

        }

        final SpanBuilder spanBuilder = tracer.spanBuilder( "Trade Sink")
                .setSpanKind(SpanKind.INTERNAL);
        //  .setSpanKind(SpanKind.CONSUMER);

        // Handle possible parent span
        final Context parentSpanContext = tracingMetadata.getPreviousContext();

        if (parentSpanContext != null) {
            spanBuilder.setParent(parentSpanContext);
        } else {
            spanBuilder.setNoParent();
        }

        final Span span = spanBuilder.startSpan();

        // Set Span attributes
        span.setAttribute("FLINK_OPERATOR", "EnrichedTradeMapOperator");

        span.makeCurrent();

        // Set span onto headers
        /**
        openTelemetry.getPropagators().getTextMapPropagator()
                .inject(Context.current(), producerRecord, setter);

        span.end();
        */
        return span;

    }

    private static final TextMapGetter<EnrichedTradeVO> getter =
            new TextMapGetter<EnrichedTradeVO>() {
                @Override
                public Iterable<String> keys(EnrichedTradeVO carrier) {
                    Set<String> headerKeys = new HashSet<String>();
                    headerKeys.add("traceparent");
                    return headerKeys;
                }
                @Override
                public String get(EnrichedTradeVO carrier, String key) {

                    String header =  "";
                    if("traceparent".equalsIgnoreCase(key)){
                        header = carrier.headers.traceparent;
                    }
                    log.info("EnrichedTradeMapOperator TextMapGetter header.key={}, header.value={}", key, header);
                    return header;
                }
            };

    public static final TextMapSetter<ProducerRecord<byte[], byte[]>> setter =
            new TextMapSetter<ProducerRecord<byte[], byte[]>>() {
                @Override
                public void set(ProducerRecord<byte[], byte[]> carrier, String key, String value) {
                    // Insert the context as Header
                    log.info("EnrichedTradeMapOperator TextMapSetter key={}, value={}", key, value);
                    carrier.headers().add(new Header() {
                        @Override
                        public String key() {
                            return key;
                        }

                        @Override
                        public byte[] value() {
                            return value.getBytes(StandardCharsets.UTF_8);
                        }
                    });

                }
            };



    @Override
    public ProducerRecord<byte[], byte[]> serialize(EnrichedTradeVO enrichedTradeVO, @Nullable Long aLong) {
        Span span = addSpanToIncomingTrace(enrichedTradeVO);

        if (objectMapper == null) {
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        }
        try {
            String json = objectMapper.writeValueAsString(enrichedTradeVO.tradeVO);
            ProducerRecord<byte[], byte[]> producerRecord = new ProducerRecord<byte[], byte[]>(topic, json.getBytes(StandardCharsets.UTF_8));

            openTelemetry.getPropagators().getTextMapPropagator()
                    .inject(Context.current(), producerRecord, setter);
            span.end();

            return producerRecord;

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Failed to parse JSON", e);
        }

        return null;
    }
}
