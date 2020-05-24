package tech.nejckorasa.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpEvents {
    private static final Logger log = LoggerFactory.getLogger("operational-events");

    public void trace(OpEvent opEvent) {
        log.trace(opEvent.getDescription(), opEvent.log());
    }

    public void debug(OpEvent opEvent) {
        log.debug(opEvent.getName(), opEvent.log());
    }

    public void info(OpEvent opEvent) {
        log.info(opEvent.getName(), opEvent.log());
    }

    public void warn(OpEvent opEvent) {
        log.warn(opEvent.getName(), opEvent.log());
    }

    public void error(OpEvent opEvent) {
        log.error(opEvent.getName(), opEvent.log());
    }
}
