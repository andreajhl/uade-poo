package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PurchaseOrder {

    private UUID id;
    private int number;
    private LocalDate issueDate;
    private Supplier supplier;
    private List<PurchaseOrderDetail> details;
    private UUID userId;
    private float total;
    private Authorization authorization;

    public PurchaseOrder(int number, Supplier supplier, UUID userId) {
        this.id = UUID.randomUUID();
        this.number = number;
        this.issueDate = LocalDate.now();
        this.supplier = supplier;
        this.userId = userId;
        this.details = new ArrayList<>();
        this.total = 0f;

    }

    public void addDetail(PurchaseOrderDetail detail) {
        details.add(detail);
        calculateTotal();
    }

    public void calculateTotal() {
        total = 0f;
        for (PurchaseOrderDetail d : details) {
            total += d.getSubtotal();
        }
    }

    public boolean requiresAuthorization(float currentDebt) {
        return currentDebt + total > supplier.getCreditLimit();
    }

    public boolean isAuthorized() {
        return authorization != null && authorization.isValid();
    }

    public UUID getId() { return id; }

    public int getNumber() { return number; }

    public LocalDate getIssueDate() { return issueDate; }

    public Supplier getSupplier() { return supplier; }

    public List<PurchaseOrderDetail> getDetails() { return details; }

    public UUID getUserId() { return userId; }

    public float getTotal() { return total; }

    public Authorization getAuthorization() { return authorization; }

    public void setAuthorization(Authorization authorization) { this.authorization = authorization; }

    @Override
    public String toString() {
        return "OC #" + number + " — " + supplier.getRazonSocial() + " (" + issueDate + ")";
    }
}
