package controllers;

import exceptions.EntityNotFoundException;
import exceptions.VoucherDeviationException;
import models.Authorization;
import models.Supplier;
import models.Voucher;
import models.VoucherDetail;
import models.enums.VoucherStatus;
import models.enums.VoucherType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

    public Voucher registerVoucher(UUID supplierId, VoucherType type, LocalDate issueDate,
                                   List<VoucherDetail> details, List<UUID> relatedOrderIds)
            throws EntityNotFoundException, VoucherDeviationException {

        Supplier supplier = SupplierController.getInstance().findById(supplierId);
        Voucher voucher = buildVoucher(supplier, type, issueDate, details, relatedOrderIds);

        boolean isInvoice = type == VoucherType.FACTURA_A
                || type == VoucherType.FACTURA_B
                || type == VoucherType.FACTURA_C;

        if (isInvoice && relatedOrderIds.isEmpty()) {
            throw new VoucherDeviationException(
                    "El comprobante no tiene una orden de compra asociada. Se requiere autorización de supervisor.");
        }

        if (isInvoice && voucher.hasPriceDeviation()) {
            throw new VoucherDeviationException(
                    "Se detectaron diferencias de precio respecto a la orden de compra. Se requiere autorización de supervisor.");
        }

        vouchers.put(voucher.getId(), voucher);
        nextNumber++;
        return voucher;
    }

    public Voucher registerVoucherWithAuthorization(UUID supplierId, VoucherType type, LocalDate issueDate,
                                                    List<VoucherDetail> details, List<UUID> relatedOrderIds,
                                                    Authorization authorization)
            throws EntityNotFoundException {

        Supplier supplier = SupplierController.getInstance().findById(supplierId);
        Voucher voucher = buildVoucher(supplier, type, issueDate, details, relatedOrderIds);
        voucher.setAuthorization(authorization);
        vouchers.put(voucher.getId(), voucher);
        nextNumber++;
        return voucher;
    }

    private Voucher buildVoucher(Supplier supplier, VoucherType type, LocalDate issueDate,
                                 List<VoucherDetail> details, List<UUID> relatedOrderIds)
            throws EntityNotFoundException {

        Voucher voucher = new Voucher(nextNumber, type, issueDate, supplier);
        for (VoucherDetail d : details) voucher.addDetail(d);

        PurchaseOrderController poc = PurchaseOrderController.getInstance();
        for (UUID ocId : relatedOrderIds) {
            voucher.addRelatedOrder(poc.findById(ocId));
        }

        return voucher;
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

    public float getTotalInvoicedBySupplierOnDate(UUID supplierId, LocalDate date) {
        float total = 0f;
        for (Voucher v : findBySupplier(supplierId)) {
            boolean isInvoice = v.getType() == VoucherType.FACTURA_A
                    || v.getType() == VoucherType.FACTURA_B
                    || v.getType() == VoucherType.FACTURA_C;
            if (isInvoice && v.getIssueDate().equals(date)) {
                total += v.getGrossTotal();
            }
        }
        return total;
    }
}
