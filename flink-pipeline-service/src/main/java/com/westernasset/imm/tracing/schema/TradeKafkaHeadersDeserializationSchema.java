package com.westernasset.imm.tracing.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.westernasset.imm.tracing.events.Headers;
import com.westernasset.imm.tracing.events.Metadata;
import com.westernasset.imm.tracing.model.EnrichedTradeVO;
import com.westernasset.imm.tracing.model.TradeVO;
import com.westernasset.imm.tracing.otel.OtelConfig;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import io.smallrye.reactive.messaging.TracingMetadata;
import io.smallrye.reactive.messaging.kafka.tracing.HeaderExtractAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

//import static io.smallrye.reactive.messaging.kafka.KafkaConnector.TRACER;

@Slf4j
public class TradeKafkaHeadersDeserializationSchema
        implements KafkaRecordDeserializationSchema<EnrichedTradeVO> {

    private static final long serialVersionUID = 1L;

    private transient ObjectMapper objectMapper;

    static final String HEADER_TRACE_PARENT = "traceparent";
 //   static final String HEADER_TRACE_STATE = "tracestate";
    static final String HEADER_TRACE_B3 = "b3";

    private static final OpenTelemetry openTelemetry = OtelConfig.initOpenTelemetry2();
    private static final Tracer tracer =
            openTelemetry.getTracer("com.westernasset.imm.tracing.TradePipelineManual");



    /**
     * For performance reasons it's better to create on ObjectMapper in this open method rather than
     * creating a new ObjectMapper for every record.
     */
    @Override
    public void open(DeserializationSchema.InitializationContext context) {
        // JavaTimeModule is needed for Java 8 data time (Instant) support
        objectMapper = JsonMapper.builder().build().registerModule(new JavaTimeModule());
    }

    /**
     * The deserialize method needs access to the information in the Kafka headers of a
     * KafkaConsumerRecord, therefore we have implemented a KafkaRecordDeserializationSchema
     */
    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<EnrichedTradeVO> out)
            throws IOException {
        Span span=addSpanToIncomingTrace(record);

        final TradeVO tradeVO = getTradeVO(record);
        final Metadata metadata = getMetadata(record);
        final Headers headers = getHeaders(record);
        EnrichedTradeVO  enrichedTradeVO = new EnrichedTradeVO(tradeVO, metadata, headers);
        out.collect(enrichedTradeVO);

        openTelemetry.getPropagators().getTextMapPropagator()
                .inject(Context.current(),enrichedTradeVO, setter);
        span.end();

    }

    private TradeVO getTradeVO(ConsumerRecord<byte[], byte[]> record) throws IOException {
        return objectMapper.readValue(record.value(), TradeVO.class);
    }

    /** Extracts the Kafka-provided metadata. */
    private static Metadata getMetadata(ConsumerRecord<byte[], byte[]> record) {
        return new Metadata(
                record.topic(),
                record.partition(),
                record.offset(),
                record.timestamp(),
                String.valueOf(record.timestampType()));
    }

    /** Extracts the user-provided headers. */
    private static Headers getHeaders(ConsumerRecord<byte[], byte[]> record) {
        return new Headers(getStringHeaderValue(record, HEADER_TRACE_B3),
                getStringHeaderValue(record, HEADER_TRACE_PARENT));

    }

    private static String getStringHeaderValue(
            ConsumerRecord<byte[], byte[]> record, String header) {
        //log.info("getStringHeaderValue header.key={}", header);
        String headerValue = "";
        if( null != record.headers().lastHeader(header)){
            headerValue = new String(record.headers().lastHeader(header).value(), StandardCharsets.UTF_8);
        }
        //log.info("getStringHeaderValue header.key={}, header.value={}", header, headerValue);
        //return new String(record.headers().lastHeader(header).value(), StandardCharsets.UTF_8);
        return headerValue;
    }

    @Override
    public TypeInformation<EnrichedTradeVO> getProducedType() {
        return TypeInformation.of(EnrichedTradeVO.class);
    }

    public Span addSpanToIncomingTrace(ConsumerRecord<byte[], byte[]> kafkaRecord) {
            TracingMetadata tracingMetadata =TracingMetadata.empty();
            if (kafkaRecord.headers() != null) {
                // Read tracing headers
//                Context context = openTelemetry.getPropagators().getTextMapPropagator()
//                        .extract(Context.root(), kafkaRecord, getter);
                Context context = openTelemetry.getPropagators().getTextMapPropagator()
                        .extract(Context.root(), kafkaRecord.headers(), HeaderExtractAdapter.GETTER);
                tracingMetadata = TracingMetadata.withPrevious(context);

            }

            final SpanBuilder spanBuilder = tracer.spanBuilder(kafkaRecord.topic() + " receive")
                    .setSpanKind(SpanKind.CONSUMER);

            // Handle possible parent span
            final Context parentSpanContext = tracingMetadata.getPreviousContext();

            if (parentSpanContext != null) {
                spanBuilder.setParent(parentSpanContext);
            } else {
                spanBuilder.setNoParent();
            }

            final Span span = spanBuilder.startSpan();

            // Set Span attributes
            span.setAttribute(SemanticAttributes.MESSAGING_KAFKA_PARTITION, kafkaRecord.partition());
            span.setAttribute("offset", kafkaRecord.offset());
            span.setAttribute(SemanticAttributes.MESSAGING_SYSTEM, "kafka");
            span.setAttribute(SemanticAttributes.MESSAGING_DESTINATION, kafkaRecord.topic());
            span.setAttribute(SemanticAttributes.MESSAGING_DESTINATION_KIND, "topic");

            /**
            final String groupId = metadata .groupMetadata().groupId();
            final String clientId = consumer.groupMetadata().groupInstanceId().orElse("");
            span.setAttribute("messaging.consumer_id", constructConsumerId(groupId, clientId));
            span.setAttribute(SemanticAttributes.MESSAGING_KAFKA_CONSUMER_GROUP, groupId);
            if (!clientId.isEmpty()) {
                span.setAttribute(SemanticAttributes.MESSAGING_KAFKA_CLIENT_ID, clientId);
            }
             */

            span.makeCurrent();

            // Set span onto headers
        /**
            openTelemetry.getPropagators().getTextMapPropagator()
                    .inject(Context.current(),enrichedTradeVO, setter);


            span.end();
         */
        return span;

    }
    // tell open
    private static final TextMapGetter<ConsumerRecord<byte[], byte[]>> getter =
            new TextMapGetter<ConsumerRecord<byte[], byte[]>>() {
                @Override
                public Iterable<String> keys(ConsumerRecord<byte[], byte[]> carrier) {
                    return HeaderExtractAdapter.GETTER.keys(carrier.headers());
                }

                @Override
                public String get(ConsumerRecord<byte[], byte[]> carrier, String key) {

                    String header =  getStringHeaderValue(carrier,key);
                    log.info("TradeKafkaHeadersDeserializationSchema TextMapGetter header.key={}, header.value={}", key, header);

                    if( null != header){
                        return header;
                    } else {
                        return "";
                    }

                }
            };

    // Tell OpenTelemetry to inject the context in the EnrichedTradeVO header
    public static final TextMapSetter<EnrichedTradeVO> setter =
            new TextMapSetter<EnrichedTradeVO>() {
                @Override
                public void set(EnrichedTradeVO carrier, String key, String value) {
                    // Insert the context as Header
                    log.info("TradeKafkaHeadersDeserializationSchema TextMapSetter key={}, value={}", key, value);
                    carrier.headers.traceparent=value;
                }
            };
}
