package tech.nejckorasa.logging;

import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * Example of logging with {@link LogEvent} without setting trace info through {@link MDC}
 */
@SuppressWarnings("PlaceholderCountMatchesArgumentCount")
public class StructuredLoggingWithoutMDCTest {

    private static final Logger log = LoggerFactory.getLogger("logging-test");
    private static final EventLogger eventLog = EventLogger.from(log);

    private static final TraceInfo traceInfo = TraceInfo.of("someTraceId", "someSpanId");
    private static final UUID accountId = randomUUID();

    /*
     * {
     *   "@timestamp": "2020-05-24T16:05:13.913+01:00",
     *   "message": "Account b9b3e3da-9a3f-4454-ae25-dc9154263bf6 has insufficient balance",
     *   "logger_name": "logging-test",
     *   "level": "INFO"
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
     *   "accountId": "b9b3e3da-9a3f-4454-ae25-dc9154263bf6",           <-- Structured field for accountId
     *   "logger_name": "logging-test",
     *   "level": "INFO"
     * }
     */
    @Test
    public void withStructuredFields() {
        log.info("Account has insufficient balance", kv("accountId", accountId));
    }

    /*
     * {
     *   "@timestamp": "2020-05-24T16:09:12.227+01:00",
     *   "message": "Account has insufficient balance",
     *   "accountId": "b9b3e3da-9a3f-4454-ae25-dc9154263bf6",           <-- Structured field for accountId
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
    public void withStructuredFieldsAndTraceInfo() {
        log.info("Account has insufficient balance", kv("accountId", accountId), traceInfo.log());
    }

    /*
     * {
     *   "@timestamp": "2020-05-24T16:09:43.136+01:00",
     *   "message": "Account has insufficient balance",
     *   "logger_name": "logging-test",
     *   "level": "ERROR",
     *   "event": {                                                     <-- Log Event with trace info
     *     "name": "InsufficientBalanceEvent",
     *     "description": "Account has insufficient balance",
     *     "accountId": "b9b3e3da-9a3f-4454-ae25-dc9154263bf6",
     *     "traceInfo": {
     *       "traceId": "someTraceId",
     *       "spanId": "someSpanId"
     *     }
     *   }
     * }
     */
    @Test
    public void withLogEvents() {
        log.error("Account has insufficient balance", new InsufficientBalanceEvent(traceInfo, accountId).log());
    }

    /*
     * {
     *   "@timestamp": "2020-05-24T16:11:41.278+01:00",
     *   "message": "Account has insufficient balance",
     *   "logger_name": "logging-test",
     *   "level": "ERROR",
     *   "event": {                                                     <-- Log Event with trace info
     *     "name": "InsufficientBalanceEvent",
     *     "description": "Account has insufficient balance",
     *     "accountId": "e99cc00b-f4a5-40c4-b1cb-493a9f52071b",
     *     "traceInfo": {
     *       "traceId": "someTraceId",
     *       "spanId": "someSpanId"
     *     }
     *   }
     * }
     */
    @Test
    public void withLogEventsAndEventLogger() {
        eventLog.error(new InsufficientBalanceEvent(traceInfo, accountId));
    }


    /**
     * Example of Log Event
     */
    private static class InsufficientBalanceEvent extends LogEvent {
        private final UUID accountId;

        public InsufficientBalanceEvent(TraceInfo traceInfo, UUID accountId) {
            super(traceInfo);
            this.accountId = accountId;
        }

        @Override
        public String getDescription() {
            return "Account has insufficient balance";
        }

        public UUID getAccountId() {
            return accountId;
        }
    }

    /**
     * Base class for all Log Events, e.g. {@link tech.nejckorasa.logging.InsufficientBalanceEvent}
     */
    private static abstract class LogEvent {
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

    /**
     * Holds basic tracing data that's included in every Log Event, i.e. every implementation of {@link LogEvent} base class
     */
    private static class TraceInfo {
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

    /**
     * Wrapper for {@link Logger} to log {@link LogEvent}s
     */
    private static class EventLogger {
        private final Logger log;

        public static EventLogger from(Logger log) {
            return new EventLogger(log);
        }

        public static EventLogger forClass(Class<?> clazz) {
            return new EventLogger(LoggerFactory.getLogger(clazz.getName()));
        }

        private EventLogger(Logger log) {
            this.log = log;
        }

        public void trace(LogEvent logEvent) {
            log.trace(logEvent.getDescription(), logEvent.log());
        }

        public void debug(LogEvent logEvent) {
            log.debug(logEvent.getDescription(), logEvent.log());
        }

        public void info(LogEvent logEvent) {
            log.info(logEvent.getDescription(), logEvent.log());
        }

        public void warn(LogEvent logEvent) {
            log.warn(logEvent.getDescription(), logEvent.log());
        }

        public void error(LogEvent logEvent) {
            log.error(logEvent.getDescription(), logEvent.log());
        }
    }
}
