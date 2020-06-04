package tech.nejckorasa.logging;

import java.util.UUID;

/**
 * Example of Log Event
 */
public class InsufficientBalanceEvent extends LogEvent {
    // All relevant fields for this event
    private final UUID accountId;

    public InsufficientBalanceEvent(UUID accountId) {
        super();
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