package tech.nejckorasa.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.nejckorasa.logging.opevent.InsufficientBalanceEvent;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static net.logstash.logback.argument.StructuredArguments.f;

@SuppressWarnings("PlaceholderCountMatchesArgumentCount")
public class StructuredLoggingTest {

    private static final Logger log = LoggerFactory.getLogger("logging-test");

    private static final TraceInfo traceInfo = TraceInfo.of("someTraceId", "someSpanId");
    private static final UUID accountId = randomUUID();

    private final OpEvents opEvents = new OpEvents();

    /*
     * {
     *   "@timestamp": "2020-05-24T16:05:13.913+01:00",
     *   "message": "Something happened",
     *   "logger_name": "logging-test",
     *   "level": "INFO",
     *   "traceId": "someTraceId",                                      <-- Trace info
     *   "spanId": "someSpanId"
     * }
     */
    @Test
    public void simplestWithOnlyTraceInfo() {
        log.info("Something happened", f(traceInfo));
    }

    /*
     * {
     *   "@timestamp": "2020-05-24T16:09:12.227+01:00",
     *   "message": "Something happened",
     *   "logger_name": "logging-test",
     *   "level": "INFO",
     *   "traceInfo": [                                                 <-- Trace info
     *     {
     *       "traceId": "someTraceId",
     *       "spanId": "someSpanId"
     *     }
     *   ]
     * }
     */
    @Test
    public void simplestWithOnlyTraceInfoAsKeyValue() {
        log.info("Something happened", traceInfo.log());
    }

    /*
     * {
     *   "@timestamp": "2020-05-24T16:09:43.136+01:00",
     *   "message": "Account has insufficient balance",
     *   "logger_name": "logging-test",
     *   "level": "ERROR",
     *   "event": {                                                     <-- Operational event with trace info
     *     "name": "InsufficientBalanceEvent",
     *     "accountId": "b9b3e3da-9a3f-4454-ae25-dc9154263bf6",
     *     "balance": 1000,
     *     "description": "Account has insufficient balance",
     *     "traceInfo": {
     *       "traceId": "someTraceId",
     *       "spanId": "someSpanId"
     *     }
     *   }
     * }
     */
    @Test
    public void moreStructuredWithOperationalEvents() {
        log.error("Account has insufficient balance", new InsufficientBalanceEvent(traceInfo, accountId, 10_00).log());
    }

    /*
     * {
     *   "@timestamp": "2020-05-24T16:11:41.278+01:00",
     *   "message": "Account has insufficient balance",
     *   "logger_name": "operational-events",
     *   "level": "ERROR",
     *   "event": {                                                     <-- Operational event with trace info
     *     "event": "InsufficientBalanceEvent",
     *     "accountId": "e99cc00b-f4a5-40c4-b1cb-493a9f52071b",
     *     "balance": 1000,
     *     "description": "Account has insufficient balance",
     *     "traceInfo": {
     *       "traceId": "someTraceId",
     *       "spanId": "someSpanId"
     *     }
     *   }
     * }
     */
    @Test
    public void withOperationalEventsAndLoggerProxy() {
        opEvents.error(new InsufficientBalanceEvent(traceInfo, accountId, 10_00));
    }
}
