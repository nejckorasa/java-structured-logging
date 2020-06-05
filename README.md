[![Build Status](https://travis-ci.org/nejckorasa/java-structured-logging.svg?branch=master)](https://travis-ci.org/nejckorasa/java-structured-logging)

# java-structured-logging

Explore different options for Structured Logging in Java with structured fields and log events, using Logback and logstash-logback-encoder.

See examples in [StructuredLoggingTest.java](/src/test/java/tech/nejckorasa/logging/StructuredLoggingTest.java).

## Structured Fields

The simplest way to add structure to logs is by adding structured fields, for example:

**Unstructured**  
   
```java
log.info("Account {} has insufficient balance", accountId);
``` 
```json
{
  "@timestamp": "2020-05-24T16:05:13.913+01:00",
  "message": "Account b9b3e3da-9a3f-4454-ae25-dc9154263bf6 has insufficient balance",
  "logger_name": "logging-test",
  "level": "INFO"
}
```

**Structured** 

```java
log.info("Account has insufficient balance", kv("accountId", accountId));
``` 
```json
 {
   "@timestamp": "2020-05-24T16:05:13.913+01:00",
   "message": "Account has insufficient balance",
   "accountId": "b9b3e3da-9a3f-4454-ae25-dc9154263bf6",
   "logger_name": "logging-test",
   "level": "INFO"
 }
```

## Log Events

Another way to avoid unstructured text data and adopt consistent structure in logs is to always log **events**.

Define Log Events as simple POJOs by extending base [LogEvent](/src/main/java/tech/nejckorasa/logging/LogEvent.java), for example [InsufficientBalanceEvent](/src/main/java/tech/nejckorasa/logging/InsufficientBalanceEvent.java):

```java
// Define Log Event
class InsufficientBalanceEvent extends LogEvent {
    private UUID accountId;

    public InsufficientBalanceEvent(UUID accountId) {
        this.accountId = accountId;
    }

    @Override
    public String getDescription() { return "Account has insufficient balance"; }
}
```
It might be suitable to separate shared data (e.g. distributed tracing information) from log events and generically apply them to the logs through [MDC](http://www.slf4j.org/api/org/slf4j/MDC.html):

```java
MDC.put("traceId", traceId);
MDC.put("spanId", spanId);
```

```java
// Logging event
eventLog.error(new InsufficientBalanceEvent(accountId));
```

Logging above event will result in:

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
    "description": "Account has insufficient balance",
    "accountId": "e99cc00b-f4a5-40c4-b1cb-493a9f52071b"
  }
}
```
Similarly, every other log event will have a consistent structure.

> Wrapper for Logger to ease logging events: [EventLogger.java](src/main/java/tech/nejckorasa/logging/EventLogger.java)
>
> Examples of logging trace info without MDC: [StructuredLoggingWithoutMDCTest.java](src/test/java/tech/nejckorasa/logging/StructuredLoggingWithoutMDCTest.java)


## Why Structured Logging?

- Producing logs in JSON format eases storing these logs in tools like Splunk, ELK stack, and **allows indexing on particular fields**.

- **Enables log correlation** by storing tracing data which is very valuable during the development process and for troubleshooting production problems. 

- **Provides consistency in log structure** which reduces cognitive overhead of figuring out what happened when searching through the logs. 

## Why Log Events?

- **Enforces structure** and consistency by always logging objects.
 
- Using class names in `event.name` provides **consistent naming** for log events, and it makes it easy to find the events in the codebase.

- **Supports log events schema evolution**. Since you can search for occurrences of a particular log event by event name (class name), changing the schema of one log event (by adding/removing fields) won't affect the search.
