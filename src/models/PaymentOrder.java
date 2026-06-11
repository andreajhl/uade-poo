package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentOrder {

    private final UUID id;
    private int number;
    private LocalDate issueDate;
    private Supplier supplier;
    private List<VoucherPayment> voucherPayments;
    private float totalVouchersAmount;
    private List<Retention> retentions;
    private float totalRetained;
    private float netAmount;
    private UUID issuedBy;
    private List<PaymentItem> paymentItems;

    public PaymentOrder(int number, Supplier supplier, UUID issuedBy) {
        this.id = UUID.randomUUID();
        this.number = number;
        this.issueDate = LocalDate.now();
        this.supplier = supplier;
        this.issuedBy = issuedBy;
        this.voucherPayments = new ArrayList<>();
        this.retentions = new ArrayList<>();
        this.paymentItems = new ArrayList<>();
        this.totalVouchersAmount = 0f;
        this.totalRetained = 0f;
        this.netAmount = 0f;
    }

    public void addVoucherPayment(VoucherPayment voucherPayment) {
        voucherPayments.add(voucherPayment);
        calculateTotals();
    }

    public void addPaymentItem(PaymentItem item) {
        paymentItems.add(item);
    }

    public List<PaymentItem> getPaymentItems() {
        return paymentItems;
    }

    public void addRetention(Retention retention) {
        retentions.add(retention);
        calculateTotals();
    }

    private void calculateTotals() {
        totalVouchersAmount = 0f;
        for (VoucherPayment vp : voucherPayments) {
            totalVouchersAmount += vp.getAmount();
        }

        totalRetained = 0f;
        for (Retention r : retentions) {
            totalRetained += r.getAmount();
        }

        netAmount = totalVouchersAmount - totalRetained;
    }

    public UUID getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public List<VoucherPayment> getVoucherPayments() {
        return voucherPayments;
    }

    public float getTotalVouchersAmount() {
        return totalVouchersAmount;
    }

    public List<Retention> getRetentions() {
        return retentions;
    }

    public float getTotalRetained() {
        return totalRetained;
    }

    public float getNetAmount() {
        return netAmount;
    }

    public UUID getIssuedBy() {
        return issuedBy;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public String toString() {
        return "[" + number + "] " + issueDate + " - " + supplier.getRazonSocial() + " - $" + netAmount;
    }
}