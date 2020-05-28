package tech.nejckorasa.logging;

import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import tech.nejckorasa.logging.events.InsufficientBalanceEvent;

/**
 * Base class for all Log Events, e.g. {@link InsufficientBalanceEvent}
 */
public abstract class LogEvent {
    private final String name = getClass().getSimpleName();
    private final TraceInfo traceInfo;

    public LogEvent(TraceInfo traceInfo) {
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