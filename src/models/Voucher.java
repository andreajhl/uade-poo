package models;

import java.time.LocalDate;
import java.util.UUID;
import models.enums.VoucherStatus;

public class Voucher {

    private final UUID id;
    private int number;
    private LocalDate issueDate;
    private Supplier supplier;
    private float total;
    private VoucherStatus status;

    public Voucher(int number, LocalDate issueDate, Supplier supplier, float total) {
        this.id = UUID.randomUUID();
        this.number = number;
        this.issueDate = issueDate;
        this.supplier = supplier;
        this.total = total;
        this.status = VoucherStatus.PENDING;
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

    public float getTotal() {
        return total;
    }

    public VoucherStatus getStatus() {
        return status;
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

    public void setTotal(float total) {
        this.total = total;
    }

    public void setStatus(VoucherStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "[" + number + "] " + issueDate + " - $" + total;
    }
}