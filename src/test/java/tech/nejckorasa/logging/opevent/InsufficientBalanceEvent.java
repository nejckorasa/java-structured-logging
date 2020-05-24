package tech.nejckorasa.logging.opevent;

import tech.nejckorasa.logging.OpEvent;
import tech.nejckorasa.logging.TraceInfo;

import java.util.UUID;

public class InsufficientBalanceEvent extends OpEvent {
    private final UUID accountId;
    private final long balance;

    public InsufficientBalanceEvent(TraceInfo traceInfo, UUID accountId, long balance) {
        super(traceInfo);
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
