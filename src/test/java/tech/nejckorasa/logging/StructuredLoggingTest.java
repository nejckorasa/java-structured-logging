package tech.nejckorasa.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * Example of logging with {@link LogEvent} and setting trace info through {@link MDC}
 */
@SuppressWarnings("PlaceholderCountMatchesArgumentCount")
public class StructuredLoggingTest {

    private static final Logger log = LoggerFactory.getLogger("logging-test");
    private final EventLogger eventLog = EventLogger.from(log);

    private static final UUID accountId = randomUUID();

    @BeforeEach
    public void setTraceInfoToMDC() {
        MDC.put("traceId", "someTraceId");
        MDC.put("spanId", "someSpanId");
    }

    /*
     * {
     *   "@timestamp": "2020-05-24T16:05:13.913+01:00",
     *   "message": "Account b9b3e3da-9a3f-4454-ae25-dc9154263bf6 has insufficient balance",
     *   "logger_name": "logging-test",
     *   "level": "INFO",
     *   "traceId": "someTraceId",                                      <-- Trace info
     *   "spanId": "someSpanId"
     * }
     */
    @Test
    public void unstructuredHenceBad() {
        log.info("Account {} has insufficient balance", accountId);
    }

    /*
     * {
     *   "@timestamp": "2020-05-24T16:05:13.913+01:00",
     *   "message": "Account has insufficient balance",
     *   "accountId": "b9b3e3da-9a3f-4454-ae25-dc9154263bf6",           <-- Structured fields for account and balance
     *   "logger_name": "logging-test",
     *   "level": "INFO",
     *   "traceId": "someTraceId",
     *   "spanId": "someSpanId"
     * }
     */
    @Test
    public void withStructuredFields() {
        log.info("Account has insufficient balance", kv("accountId", accountId));
    }

    /*
     * {
     *   "@timestamp": "2020-05-24T16:09:43.136+01:00",
     *   "message": "Account has insufficient balance",
     *   "logger_name": "logging-test",
     *   "level": "ERROR",
     *   "traceId": "someTraceId",                                      <-- Trace info
     *   "spanId": "someSpanId",
     *   "event": {                                                     <-- Log Event
     *     "name": "InsufficientBalanceEvent",
     *     "description": "Account has insufficient balance",
     *     "accountId": "b9b3e3da-9a3f-4454-ae25-dc9154263bf6"
     *   }
     * }
     */
    @Test
    public void withLogEvents() {
        log.error("Account has insufficient balance", new InsufficientBalanceEvent(accountId).log());
    }

    /*
     * {
     *   "@timestamp": "2020-05-24T16:11:41.278+01:00",
     *   "message": "Account has insufficient balance",
     *   "logger_name": "logging-test",
     *   "level": "ERROR",
     *   "traceId": "someTraceId",                                      <-- Trace info
     *   "spanId": "someSpanId",
     *   "event": {                                                     <-- Log Event
     *     "name": "InsufficientBalanceEvent",
     *     "description": "Account has insufficient balance",
     *     "accountId": "e99cc00b-f4a5-40c4-b1cb-493a9f52071b"
     *   }
     * }
     */
    @Test
    public void withLogEventsAndEventLogger() {
        eventLog.error(new InsufficientBalanceEvent(accountId));
    }
}
