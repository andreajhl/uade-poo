package models;

public class PurchaseOrderDetail {

    private Product product;
    private int quantity;
    private float agreedUnitPrice;

    public PurchaseOrderDetail(Product product, int quantity, float agreedUnitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.agreedUnitPrice = agreedUnitPrice;
    }

    public float getSubtotal() {
        return quantity * agreedUnitPrice;
    }

    public Product getProduct() { return product; }

    public int getQuantity() { return quantity; }

    public float getAgreedUnitPrice() { return agreedUnitPrice; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void setAgreedUnitPrice(float agreedUnitPrice) { this.agreedUnitPrice = agreedUnitPrice; }
}
