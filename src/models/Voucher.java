package models;

import models.enums.VoucherStatus;
import models.enums.VoucherType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Voucher {

    private final UUID id;
    private final int number;
    private final VoucherType type;
    private final LocalDate issueDate;
    private final Supplier supplier;
    private final List<VoucherDetail> details;
    private final List<PurchaseOrder> relatedOrders;
    private VoucherStatus status;
    private Authorization authorization;

    public Voucher(int number, VoucherType type, LocalDate issueDate, Supplier supplier) {
        this.id = UUID.randomUUID();
        this.number = number;
        this.type = type;
        this.issueDate = issueDate;
        this.supplier = supplier;
        this.details = new ArrayList<>();
        this.relatedOrders = new ArrayList<>();
        this.status = VoucherStatus.PENDING;
    }

    public void addDetail(VoucherDetail detail) {
        details.add(detail);
    }

    public void addRelatedOrder(PurchaseOrder order) {
        relatedOrders.add(order);
    }

    public float getNetTotal() {
        float total = 0f;
        for (VoucherDetail d : details) total += d.getNetAmount();
        return total;
    }

    public float getVatTotal() {
        float total = 0f;
        for (VoucherDetail d : details) total += d.getVatAmount();
        return total;
    }

    public float getGrossTotal() {
        return getNetTotal() + getVatTotal();
    }

    public boolean hasPriceDeviation() {
        for (PurchaseOrder oc : relatedOrders) {
            for (VoucherDetail vd : details) {
                for (PurchaseOrderDetail ocd : oc.getDetails()) {
                    if (ocd.getProduct().getId().equals(vd.getProduct().getId())) {
                        if (Math.abs(ocd.getAgreedUnitPrice() - vd.getUnitPrice()) > 0.001f) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public UUID getId() { return id; }

    public int getNumber() { return number; }

    public VoucherType getType() { return type; }

    public LocalDate getIssueDate() { return issueDate; }

    public Supplier getSupplier() { return supplier; }

    public List<VoucherDetail> getDetails() { return details; }

    public List<PurchaseOrder> getRelatedOrders() { return relatedOrders; }

    public VoucherStatus getStatus() { return status; }

    public void setStatus(VoucherStatus status) { this.status = status; }

    public Authorization getAuthorization() { return authorization; }

    public void setAuthorization(Authorization authorization) { this.authorization = authorization; }

    @Override
    public String toString() {
        return type.name() + " N°" + number + " — " + supplier.getRazonSocial();
    }
}
