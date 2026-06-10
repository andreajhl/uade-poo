package models;

import models.enums.Category;

import java.util.UUID;

public class ProductSupplier {

    private UUID supplierId;
    private float agreedUnitPrice;
    private Category category;

    public ProductSupplier(UUID supplierId, float agreedUnitPrice, Category category) {
        this.supplierId = supplierId;
        this.agreedUnitPrice = agreedUnitPrice;
        this.category = category;
    }

    public UUID getSupplierId() { return supplierId; }

    public float getAgreedUnitPrice() { return agreedUnitPrice; }

    public Category getCategory() { return category; }

    public void setAgreedUnitPrice(float agreedUnitPrice) { this.agreedUnitPrice = agreedUnitPrice; }
}
