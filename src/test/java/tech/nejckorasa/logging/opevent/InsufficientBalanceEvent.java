package tech.nejckorasa.logging.opevent;

import tech.nejckorasa.logging.OpEvent;
import tech.nejckorasa.logging.TraceInfo;

import java.util.UUID;

public class InsufficientBalanceEvent extends OpEvent {
    private final UUID accountId;
    private final long adjustmentAmount;

    public InsufficientBalanceEvent(TraceInfo traceInfo, UUID accountId, long adjustmentAmount) {
        super(traceInfo);
        this.accountId = accountId;
        this.adjustmentAmount = adjustmentAmount;
    }

    @Override
    public String getDescription() {
        return "Account has insufficient balance";
    }

    public UUID getAccountId() {
        return accountId;
    }

    public long getAdjustmentAmount() {
        return adjustmentAmount;
    }
}
