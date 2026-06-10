package controllers;

import exceptions.EntityNotFoundException;
import exceptions.VoucherDeviationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import models.Authorization;
import models.Supplier;
import models.Voucher;
import models.VoucherDetail;
import models.enums.VoucherStatus;
import models.enums.VoucherType;

public class VoucherController {

    private static VoucherController instance;
    private final HashMap<UUID, Voucher> vouchers;
    private int nextNumber;

    private VoucherController() {
        vouchers = new HashMap<>();
        nextNumber = 1;
    }

    public static VoucherController getInstance() {
        if (instance == null) instance = new VoucherController();
        return instance;
    }

    public Voucher registerDebitNote(UUID supplierId, LocalDate issueDate,
                                     List<VoucherDetail> details, UUID relatedOrderId)
            throws EntityNotFoundException {

        Supplier supplier = SupplierController.getInstance().findById(supplierId);
        Voucher voucher = new Voucher(nextNumber, VoucherType.NOTA_DEBITO, issueDate, supplier);

        for (VoucherDetail d : details) voucher.addDetail(d);

        voucher.addRelatedOrder(PurchaseOrderController.getInstance().findById(relatedOrderId));
        vouchers.put(voucher.getId(), voucher);

        nextNumber++;
        return voucher;
    }

    public Voucher registerInvoice(UUID supplierId, VoucherType type, LocalDate issueDate,
                                    List<VoucherDetail> details, UUID relatedDebitNoteId)
            throws EntityNotFoundException, VoucherDeviationException {

        Supplier supplier = SupplierController.getInstance().findById(supplierId);
        Voucher debitNote = findById(relatedDebitNoteId);

        Voucher voucher = new Voucher(nextNumber, type, issueDate, supplier);
        for (VoucherDetail d : details) voucher.addDetail(d);
        voucher.setRelatedDebitNote(debitNote);
        for (var oc : debitNote.getRelatedOrders()) voucher.addRelatedOrder(oc);

        if (voucher.hasPriceDeviation()) {
            throw new VoucherDeviationException(
                    "Se detectaron diferencias de precio respecto a la nota de débito. Se requiere autorización.");
        }

        vouchers.put(voucher.getId(), voucher);
        nextNumber++;
        return voucher;
    }

    public Voucher registerInvoiceWithAuthorization(UUID supplierId, VoucherType type, LocalDate issueDate,
                                                     List<VoucherDetail> details, UUID relatedDebitNoteId,
                                                     Authorization authorization)
            throws EntityNotFoundException {

        Supplier supplier = SupplierController.getInstance().findById(supplierId);
        Voucher debitNote = findById(relatedDebitNoteId);

        Voucher voucher = new Voucher(nextNumber, type, issueDate, supplier);
        for (VoucherDetail d : details) voucher.addDetail(d);
        voucher.setRelatedDebitNote(debitNote);
        for (var oc : debitNote.getRelatedOrders()) voucher.addRelatedOrder(oc);
        voucher.setAuthorization(authorization);

        vouchers.put(voucher.getId(), voucher);
        nextNumber++;
        return voucher;
    }

    public List<Voucher> findDebitNotesBySupplier(UUID supplierId) {
        List<Voucher> result = new ArrayList<>();
        for (Voucher v : findBySupplier(supplierId)) {
            if (v.getType() == VoucherType.NOTA_DEBITO) result.add(v);
        }
        return result;
    }

    public List<Voucher> findAll() {
        return new ArrayList<>(vouchers.values());
    }

    public List<Voucher> findBySupplier(UUID supplierId) {
        List<Voucher> result = new ArrayList<>();
        for (Voucher v : vouchers.values()) {
            if (v.getSupplier().getId().equals(supplierId)) result.add(v);
        }
        return result;
    }

    public List<Voucher> findUnpaid(UUID supplierId) {
        List<Voucher> result = new ArrayList<>();
        for (Voucher v : findBySupplier(supplierId)) {
            if (v.getStatus() == VoucherStatus.PENDING
                    || v.getStatus() == VoucherStatus.PARTIALLY_PAID) {
                result.add(v);
            }
        }
        return result;
    }

    public Voucher findById(UUID id) throws EntityNotFoundException {
        Voucher v = vouchers.get(id);
        if (v == null) throw new EntityNotFoundException("Voucher", id);
        return v;
    }

    public List<Voucher> findInvoices(UUID supplierId, LocalDate from, LocalDate to) {
        List<Voucher> result = new ArrayList<>();
        for (Voucher v : vouchers.values()) {
            if (!isInvoiceType(v.getType())) continue;
            if (supplierId != null && !v.getSupplier().getId().equals(supplierId)) continue;
            if (from != null && v.getIssueDate().isBefore(from)) continue;
            if (to != null && v.getIssueDate().isAfter(to)) continue;
            result.add(v);
        }
        result.sort((a, b) -> a.getIssueDate().compareTo(b.getIssueDate()));
        return result;
    }

    public float getTotalInvoicedBySupplierOnDate(UUID supplierId, LocalDate date) {
        float total = 0f;
        for (Voucher v : findBySupplier(supplierId)) {
            if (isInvoiceType(v.getType()) && v.getIssueDate().equals(date))
                total += v.getGrossTotal();
        }
        return total;
    }

    private boolean isInvoiceType(VoucherType type) {
        return type == VoucherType.FACTURA_A
                || type == VoucherType.FACTURA_B
                || type == VoucherType.FACTURA_C;
    }
    public List<Voucher> findPendingBySupplier(UUID supplierId) {
    List<Voucher> result = new ArrayList<>();
    for (Voucher v : findBySupplier(supplierId)) {
        if (v.getStatus() == VoucherStatus.PENDING) {
            result.add(v);
        }
    }
    return result;
}
}
