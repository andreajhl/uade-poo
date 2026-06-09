package models;

import models.enums.TaxType;

public class VoucherDetail {

    private final Product product;
    private final int quantity;
    private final float unitPrice;
    private final float netAmount;
    private final float vatAmount;

    public VoucherDetail(Product product, int quantity, float unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.netAmount = quantity * unitPrice;
        this.vatAmount = netAmount * ivaRateFor(product.getTaxType()) / 100f;
    }

    private static float ivaRateFor(TaxType taxType) {
        return switch (taxType) {
            case IVA -> 21f;
            case INGRESOS_BRUTOS -> 10.5f;
            case GANANCIAS -> 0f;
        };
    }

    public static float ivaRate(TaxType taxType) {
        return ivaRateFor(taxType);
    }

    public float getGrossAmount() {
        return netAmount + vatAmount;
    }

    public Product getProduct() { return product; }

    public int getQuantity() { return quantity; }

    public float getUnitPrice() { return unitPrice; }

    public float getNetAmount() { return netAmount; }

    public float getVatAmount() { return vatAmount; }
}
