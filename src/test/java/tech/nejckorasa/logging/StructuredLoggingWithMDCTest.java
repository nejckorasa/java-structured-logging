package tech.nejckorasa.logging;

import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.UUID;

import static java.util.UUID.randomUUID;

/**
 * Example of logging with {@link LogEvent} and setting trace info through {@link MDC}
 */
public class StructuredLoggingWithMDCTest {

    private static final EventLogger eventLog = EventLogger.forClass(StructuredLoggingWithMDCTest.class);

    private static final TraceInfo traceInfo = TraceInfo.of("someTraceId", "someSpanId");
    private static final UUID accountId = randomUUID();

    @BeforeEach
    public void setTraceInfoToMDC() {
        MDC.put("traceId", traceInfo.getTraceId());
        MDC.put("spanId", traceInfo.getSpanId());
    }

    /*
     * {
     *   "@timestamp": "2020-05-24T16:11:41.278+01:00",
     *   "message": "Account has insufficient balance",
     *   "logger_name": "logging-test",
     *   "level": "ERROR",
     *   "traceId": "someTraceId",                                      <-- Separate trace info
     *   "spanId": "someSpanId",
     *   "event": {                                                     <-- Log Event
     *     "name": "InsufficientBalanceEvent",
     *     "accountId": "e99cc00b-f4a5-40c4-b1cb-493a9f52071b",
     *     "balance": 1000,
     *     "description": "Account has insufficient balance",
     *     }
     *   }
     * }
     */
    @Test
    public void traceInfoIsSeparate() {
        eventLog.error(new InsufficientBalanceEvent(accountId, 10_00));
    }
}


/**
 * Base class for all Log Events, e.g. {@link InsufficientBalanceEvent}
 */
abstract class LogEvent {
    private final String name = getClass().getSimpleName();

    public abstract String getDescription();

    public String getName() {
        return name;
    }

    public StructuredArgument log() {
        return StructuredArguments.keyValue("event", this);
    }
}

/**
 * Example of Log Event
 */
class InsufficientBalanceEvent extends LogEvent {
    private final UUID accountId;
    private final long balance;

    public InsufficientBalanceEvent(UUID accountId, long balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    @Override
    public String getDescription() {
        return "Account has insufficient balance";
    }

    public UUID getAccountId() {
        return accountId;
    }

    public long getBalance() {
        return balance;
    }
}