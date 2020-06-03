# java-structured-logging

Explore different simple options for Structured Logging in Java with Logback and logstash-logback-encoder.

See examples in [StructuredLoggingTest.java](/src/test/java/tech/nejckorasa/logging/StructuredLoggingTest.java) and [StructuredLoggingWithMDCTest.java](src/test/java/tech/nejckorasa/logging/StructuredLoggingWithMDCTest.java).

## Log Events

The idea is to avoid using unstructured text data and adopt Consistent Structure in Logs. Easy way to achieve this is to always log **Events**.

Define Log Events as simple POJOs by extending base [LogEvent](/src/main/java/tech/nejckorasa/logging/LogEvent.java), for example:

```java
// Define Log Event
class InsufficientBalanceEvent extends LogEvent {
    private UUID accountId;
    private long balance;

    public InsufficientBalanceEvent(TraceInfo traceInfo, UUID accountId, long balance) {
        super(traceInfo);
        this.accountId = accountId;
        this.balance = balance;
    }

    @Override
    public String getDescription() {
        return "Account has insufficient balance";
    }
}
```

```java
// Logging event
eventLog.error(new InsufficientBalanceEvent(TraceInfo.of(traceId. spanId), accountId, 10_00));
```
InsufficientBalanceEvent is defined [here](/src/main/java/tech/nejckorasa/logging/events/InsufficientBalanceEvent.java). Logging above event will result in:

```json
{
  "@timestamp": "2020-05-24T16:11:41.278+01:00",
  "message": "Account has insufficient balance",
  "logger_name": "logging-test",
  "level": "ERROR",
  "event": {
    "name": "InsufficientBalanceEvent",
    "accountId": "e99cc00b-f4a5-40c4-b1cb-493a9f52071b",
    "balance": 1000,
    "description": "Account has insufficient balance",
    "traceInfo": {
      "traceId": "someTraceId",
      "spanId": "someSpanId"
    }
  }
}
```
Similarly, every other log event will have the same structure.

### Setting common fields through MDC

It might be more suitable to separate shared data (e.g. `traceInfo`) from `LogEvents` and apply them to the logs through [MDC](http://www.slf4j.org/api/org/slf4j/MDC.html):

```java
MDC.put("traceId", traceId);
MDC.put("spanId", spanId);
```

```java
// Logging event (without TraceInfo)
eventLog.error(new InsufficientBalanceEvent(accountId, 10_00));
```
That would result in a following log structure:

```json
{
  "@timestamp": "2020-05-24T16:11:41.278+01:00",
  "message": "Account has insufficient balance",
  "logger_name": "logging-test",
  "level": "ERROR",
  "traceId": "someTraceId",
  "spanId": "someSpanId",
  "event": {
    "name": "InsufficientBalanceEvent",
    "accountId": "e99cc00b-f4a5-40c4-b1cb-493a9f52071b",
    "balance": 1000,
    "description": "Account has insufficient balance"
  }
}
```

Examples can be found in [StructuredLoggingWithMDCTest.java](src/test/java/tech/nejckorasa/logging/StructuredLoggingWithMDCTest.java)


## Why log events?

- Producing logs in JSON format eases storing these logs in tools like Splunk, ELK stack and **allows indexing on particular fields**.

- **Enables log correlation** by storing tracing data which is very valuable during the development process and for troubleshooting production problems. 

- **Provides consistency in log structure** which reduces cognitive overhead of figuring out what happened when searching through the logs. 

- Using class names in `event.name` provides **consistent naming** for log events, and it makes it easy to find the events in the codebase.

- **Supports log events schema evolution**. Since you can search for occurrences of a particular log event by event name (class name), changing the schema of one log event (by adding/removing fields) won't affect the search.
