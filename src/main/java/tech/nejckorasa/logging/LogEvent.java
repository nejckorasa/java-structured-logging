package tech.nejckorasa.logging;

import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;

/**
 * Base class for all Log Events, e.g. {@link InsufficientBalanceEvent}
 */
public abstract class LogEvent {
    private final String name = getClass().getSimpleName();

    public abstract String getDescription();

    public String getName() {
        return name;
    }

    public StructuredArgument log() {
        return StructuredArguments.keyValue("event", this);
    }
}