package tech.nejckorasa.logging;

import java.util.UUID;

/**
 * Example of Log Event
 */
public class InsufficientBalanceEvent extends LogEvent {
    private final UUID accountId;
    private final long balance;

    public InsufficientBalanceEvent(UUID accountId, long balance) {
        super();
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