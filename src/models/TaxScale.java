package models;

import java.util.UUID;

public class TaxScale {

    private UUID id;
    private float fromAmount;
    private float toAmount;
    private float percentage;

    public TaxScale(float fromAmount, float toAmount, float percentage) {
        this.id = UUID.randomUUID();
        this.fromAmount = fromAmount;
        this.toAmount = toAmount;
        this.percentage = percentage;
    }

    public boolean appliesTo(float amount) {
        return amount >= fromAmount && amount <= toAmount;
    }

    public UUID getId() { return id; }

    public float getFromAmount() { return fromAmount; }

    public float getToAmount() { return toAmount; }

    public float getPercentage() { return percentage; }

    public void setFromAmount(float fromAmount) { this.fromAmount = fromAmount; }

    public void setToAmount(float toAmount) { this.toAmount = toAmount; }

    public void setPercentage(float percentage) { this.percentage = percentage; }
}
