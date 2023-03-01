package com.westernasset.imm.tracing.operator;

import com.westernasset.imm.tracing.model.EnrichedTradeVO;
import com.westernasset.imm.tracing.model.TradeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FilterFunction;

@Slf4j
public class EnrichedTradeFilterOperator implements FilterFunction<EnrichedTradeVO> {
    @Override
    public boolean filter(EnrichedTradeVO tradeVO) throws Exception {
        /**
        final Tracer tracer = GlobalOpenTelemetry.get().getTracer("app-name", "1.0.0");
        tracer.

        OpenTelemetrySdk sdk = AutoConfiguredOpenTelemetrySdk.initialize()
                .getOpenTelemetrySdk();

        SdkTracerProvider sdkTracerProvider = sdk.getSdkTracerProvider();


        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();

        Tracer tracer = openTelemetry.getTracer("instrumentation-library-name", "1.0.0");

        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdk.getSdkTracerProvider())
                .setMeterProvider(sdk.getSdkMeterProvider())
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();

        Tracer tracer = openTelemetry.getTracerProvider().get();

        Span span = Span.current();
        span.setAttribute("pf_number", tradeVO.getPfNumber());
        log.info("TradeFilterOperator TRACE_ID={}, SPAN_ID={}, TRADE_ID={}",  span.getSpanContext().getTraceId(),
                span.getSpanContext().getSpanId(),
                tradeVO.getTradeId());
         */
        return "FIXED".equalsIgnoreCase(tradeVO.tradeVO.getTicketType());
    }
}
