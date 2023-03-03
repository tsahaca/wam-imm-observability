package com.westernasset.imm.tracing.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import java.util.concurrent.TimeUnit;

/**
 * All SDK management takes place here, away from the instrumentation code, which should only access
 * the OpenTelemetry APIs.
 */
public class OtelConfig {
    private static final String SERVICE_NAME = "FLINK_TRADE_PIPELINE_JOB";

    /**
     * Initializes the OpenTelemetry SDK with a logging span exporter and the W3C Trace Context
     * propagator.
     *
     * @return A ready-to-use {@link OpenTelemetry} instance.
     */
    public static OpenTelemetry initOpenTelemetry2() {
        //Create Resource
        AttributesBuilder attrBuilders = Attributes.builder()
                .put(ResourceAttributes.SERVICE_NAME, SERVICE_NAME);

        Resource serviceResource = Resource
                .create(attrBuilders.build());
        //Create Span Exporter
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://otel-collector:4317")
                .build();

        SdkTracerProvider sdkTracerProvider =
                SdkTracerProvider.builder()
                        .addSpanProcessor(BatchSpanProcessor.builder(spanExporter)
                                .setScheduleDelay(100, TimeUnit.MILLISECONDS).build())
                        .setResource(serviceResource)
                        .build();

        OpenTelemetrySdk sdk =
                OpenTelemetrySdk.builder()
                        .setTracerProvider(sdkTracerProvider)
                        .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                        .build();

        Runtime.getRuntime().addShutdownHook(new Thread(sdkTracerProvider::close));
        return sdk;
    }


    /**
     * Initializes the OpenTelemetry SDK with a logging span exporter and the W3C Trace Context
     * propagator.
     *
     * @return A ready-to-use {@link OpenTelemetry} instance.
     */
    public static OpenTelemetry initOpenTelemetry() {

        //Create Resource
        AttributesBuilder attrBuilders = Attributes.builder()
                .put(ResourceAttributes.SERVICE_NAME, SERVICE_NAME);

        Resource serviceResource = Resource
                .create(attrBuilders.build());
        //Create Span Exporter
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://otel-collector:4317")
                .build();

        //Create SdkTracerProvider
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter)
                        .setScheduleDelay(100, TimeUnit.MILLISECONDS).build())
                .setResource(serviceResource)
                .build();

        //This Instance can be used to get tracer if it is not configured as global
        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();

        Runtime.getRuntime().addShutdownHook(new Thread(sdkTracerProvider::close));

        return openTelemetry;
    }


}
