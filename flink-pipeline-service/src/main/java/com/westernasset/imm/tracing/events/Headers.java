package com.westernasset.imm.tracing.events;

import java.util.Objects;

/**
 * A collection of custom created Headers sent with a Kafka record that Flink recognizes as a valid
 * POJO
 */
public class Headers {

    /** A Flink POJO must have public fields, or getters and setters */
    public String b3;

    public String traceparent;

    /** A Flink POJO must have a no-args default constructor */
    public Headers() {}

    public Headers(final String b3, final String traceparent) {
        this.b3 = b3;
        this.traceparent = traceparent;
    }

    public Headers(final String b3) {
        this.b3 = b3;
    }

    /** Used for printing during development */
    @Override
    public String toString() {
        return "Headers{" + "b3=" + b3 + ", traceparent='" + traceparent + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Headers headers = (Headers) o;
        return b3.equals(headers.b3) && traceparent.equals(headers.traceparent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(traceparent, traceparent);
    }
}
