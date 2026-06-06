package models;

import java.util.UUID;

public class ProductSupplierPrice {

    private UUID supplierId;
    private float unitPrice;

    public ProductSupplierPrice(UUID supplierId, float unitPrice) {
        this.supplierId = supplierId;
        this.unitPrice = unitPrice;
    }

    public UUID getSupplierId() { return supplierId; }

    public float getUnitPrice() { return unitPrice; }

    public void setUnitPrice(float unitPrice) { this.unitPrice = unitPrice; }
}
