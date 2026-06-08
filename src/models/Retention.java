package models;

import models.enums.TaxType;

import java.util.UUID;

public class Retention {

    private UUID id;
    private TaxType taxType;
    private float amount;

    public Retention(TaxType taxType, float amount) {
        this.id = UUID.randomUUID();
        this.taxType = taxType;
        this.amount = amount;
    }

    public UUID getId() { return id; }

    public TaxType getTaxType() { return taxType; }

    public float getAmount() { return amount; }
}
