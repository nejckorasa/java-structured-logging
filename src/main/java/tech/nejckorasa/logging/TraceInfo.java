package tech.nejckorasa.logging;


import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;

/**
 * Holds basic tracing data that's included in every Log Event, i.e. every implementation of {@link LogEvent} base class
 */
public class TraceInfo {
    private final String traceId;
    private final String spanId;

    private TraceInfo(String traceId, String spanId) {
        this.traceId = traceId;
        this.spanId = spanId;
    }

    public static TraceInfo of(String traceId, String spanId) {
        return new TraceInfo(traceId, spanId);
    }

    public String getTraceId() {
        return traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public StructuredArgument log() {
        return StructuredArguments.a("traceInfo", this);
    }
}
