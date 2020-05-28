package tech.nejckorasa.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for {@link Logger} to log {@link LogEvent}s
 */
public class EventLogger {
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
        log.debug(logEvent.getName(), logEvent.log());
    }

    public void info(LogEvent logEvent) {
        log.info(logEvent.getName(), logEvent.log());
    }

    public void warn(LogEvent logEvent) {
        log.warn(logEvent.getName(), logEvent.log());
    }

    public void error(LogEvent logEvent) {
        log.error(logEvent.getName(), logEvent.log());
    }
}
