package models;

import java.util.UUID;

public class VoucherPayment {

    private final UUID voucherId;
    private float amount;

    public VoucherPayment(UUID voucherId, float amount) {
        this.voucherId = voucherId;
        this.amount = amount;
    }

    public UUID getVoucherId() {
        return voucherId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "VoucherPayment{" +
                "voucherId=" + voucherId +
                ", amount=" + amount +
                '}';
    }
}