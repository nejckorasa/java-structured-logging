# java-structured-logging

Explore different simple options for Structured Logging in Java with Logback and logstash-logback-encoder.

See examples in [StructuredLoggingTest.java](/src/test/java/tech/nejckorasa/logging/StructuredLoggingTest.java).

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
    "event": "InsufficientBalanceEvent",
    "accountId": "e99cc00b-f4a5-40c4-b1cb-493a9f52071b",
    "balance": 1000,
    "description": "Account has insufficient balance",
    "traceInfo": {
      "traceId": "traceId",
      "spanId": "spanId"
    }
  }
}
```
Similarly, every other log event will have the same structure. 




