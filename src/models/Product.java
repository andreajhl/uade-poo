package models;

import models.enums.TaxType;
import models.enums.UnitOfMeasure;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Product {

    private UUID id;
    private String code;
    private String description;
    private UnitOfMeasure unitOfMeasure;
    private TaxType taxType;
    private Category category;
    private List<ProductSupplier> supplierPrices;

    public Product(String code, String description, UnitOfMeasure unitOfMeasure,
                   TaxType taxType, Category category) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.unitOfMeasure = unitOfMeasure;
        this.taxType = taxType;
        this.category = category;
        this.supplierPrices = new ArrayList<>();
    }

    public void addSupplierPrice(ProductSupplier price) {
        supplierPrices.removeIf(p -> p.getSupplierId().equals(price.getSupplierId()));
        supplierPrices.add(price);
    }

    public float getPriceForSupplier(UUID supplierId) {
        for (ProductSupplier p : supplierPrices) {
            if (p.getSupplierId().equals(supplierId)) {
                return p.getAgreedUnitPrice();
            }
        }
        return 0f;
    }

    public UUID getId() { return id; }

    public String getCode() { return code; }

    public String getDescription() { return description; }

    public UnitOfMeasure getUnitOfMeasure() { return unitOfMeasure; }

    public TaxType getTaxType() { return taxType; }

    public Category getCategory() { return category; }

    public List<ProductSupplier> getSupplierPrices() { return supplierPrices; }

    public void setCode(String code) { this.code = code; }

    public void setDescription(String description) { this.description = description; }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public void setTaxType(TaxType taxType) { this.taxType = taxType; }

    public void setCategory(Category category) { this.category = category; }

    @Override
    public String toString() { return "[" + code + "] " + description; }
}
