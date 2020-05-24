package tech.nejckorasa.logging;

import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;

public abstract class OpEvent {
    private final String name = getClass().getSimpleName();
    private final TraceInfo traceInfo;

    public OpEvent(TraceInfo traceInfo) {
        this.traceInfo = traceInfo;
    }

    public abstract String getDescription();

    public String getName() {
        return name;
    }

    public TraceInfo getTraceInfo() {
        return traceInfo;
    }

    public StructuredArgument log() {
        return StructuredArguments.keyValue("event", this);
    }
}