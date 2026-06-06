package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Product {

    private UUID id;
    private String code;
    private String description;
    private String unitOfMeasure;
    private float ivaRate;
    private Category category;
    private List<ProductSupplierPrice> supplierPrices;

    public Product(String code, String description, String unitOfMeasure,
                   float ivaRate, Category category) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.unitOfMeasure = unitOfMeasure;
        this.ivaRate = ivaRate;
        this.category = category;
        this.supplierPrices = new ArrayList<>();
    }

    public void addSupplierPrice(ProductSupplierPrice price) {
        supplierPrices.removeIf(p -> p.getSupplierId().equals(price.getSupplierId()));
        supplierPrices.add(price);
    }

    public float getPriceForSupplier(UUID supplierId) {
        for (ProductSupplierPrice p : supplierPrices) {
            if (p.getSupplierId().equals(supplierId)) {
                return p.getUnitPrice();
            }
        }
        return 0f;
    }

    public UUID getId() { return id; }

    public String getCode() { return code; }

    public String getDescription() { return description; }

    public String getUnitOfMeasure() { return unitOfMeasure; }

    public float getIvaRate() { return ivaRate; }

    public Category getCategory() { return category; }

    public List<ProductSupplierPrice> getSupplierPrices() { return supplierPrices; }

    public void setCode(String code) { this.code = code; }

    public void setDescription(String description) { this.description = description; }

    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public void setIvaRate(float ivaRate) { this.ivaRate = ivaRate; }

    public void setCategory(Category category) { this.category = category; }

    @Override
    public String toString() { return "[" + code + "] " + description; }
}
