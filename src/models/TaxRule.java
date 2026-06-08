package models;

import models.enums.TaxType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaxRule {

    private UUID id;
    private TaxType taxType;
    private float defaultPercentage;
    private float minimumTaxableAmount;
    private List<TaxScale> scales;
    private List<Retention> retentions;

    public TaxRule(TaxType taxType, float defaultPercentage, float minimumTaxableAmount) {
        this.id = UUID.randomUUID();
        this.taxType = taxType;
        this.defaultPercentage = defaultPercentage;
        this.minimumTaxableAmount = minimumTaxableAmount;
        this.scales = new ArrayList<>();
        this.retentions = new ArrayList<>();
    }

    public float calculateRetention(float baseAmount) {
        if (baseAmount < minimumTaxableAmount) return 0f;
        for (TaxScale scale : scales) {
            if (scale.appliesTo(baseAmount)) {
                return baseAmount * scale.getPercentage() / 100f;
            }
        }
        return baseAmount * defaultPercentage / 100f;
    }

    public void addScale(TaxScale scale) {
        scales.add(scale);
    }

    public void addRetention(Retention retention) {
        retentions.add(retention);
    }

    public UUID getId() { return id; }

    public TaxType getTaxType() { return taxType; }

    public float getDefaultPercentage() { return defaultPercentage; }

    public float getMinimumTaxableAmount() { return minimumTaxableAmount; }

    public List<TaxScale> getScales() { return scales; }

    public List<Retention> getRetentions() { return retentions; }

    public void setDefaultPercentage(float defaultPercentage) { this.defaultPercentage = defaultPercentage; }

    public void setMinimumTaxableAmount(float minimumTaxableAmount) { this.minimumTaxableAmount = minimumTaxableAmount; }
}
