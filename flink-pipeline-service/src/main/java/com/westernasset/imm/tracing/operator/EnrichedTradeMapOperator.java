package com.westernasset.imm.tracing.operator;

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
import org.apache.flink.api.common.functions.MapFunction;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class EnrichedTradeMapOperator implements MapFunction<EnrichedTradeVO,EnrichedTradeVO> {
    private static final OpenTelemetry openTelemetry = OtelConfig.initOpenTelemetry2();
    private static final Tracer tracer =
            openTelemetry.getTracer("com.westernasset.imm.tracing.operator.EnrichedTradeMapOperator");

    @Override
    public EnrichedTradeVO map(EnrichedTradeVO tradeVO) throws Exception {
        Span span = addSpanToIncomingTrace(tradeVO);
        span.setAttribute("pf_number", tradeVO.tradeVO.getPfNumber());

        tradeVO.tradeVO.setStatus("PROCESSED_BY_FLINK");
        span.end();
        return tradeVO;
    }

    public Span addSpanToIncomingTrace(EnrichedTradeVO tradeVO) {
        TracingMetadata tracingMetadata =TracingMetadata.empty();
        if (tradeVO.headers.traceparent != null) {
            // Read tracing headers
            Context context = openTelemetry.getPropagators().getTextMapPropagator()
                    .extract(Context.root(), tradeVO, getter);

            tracingMetadata = TracingMetadata.withPrevious(context);

        }

        final SpanBuilder spanBuilder = tracer.spanBuilder( "Trade Map")
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
        openTelemetry.getPropagators().getTextMapPropagator()
                .inject(Context.current(), tradeVO, setter);

       // span.end();
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

    public static final TextMapSetter<EnrichedTradeVO> setter =
            new TextMapSetter<EnrichedTradeVO>() {
                @Override
                public void set(EnrichedTradeVO carrier, String key, String value) {
                    // Insert the context as Header
                    log.info("EnrichedTradeMapOperator TextMapSetter key={}, value={}", key, value);
                    carrier.headers.traceparent=value;
                }
            };
}
