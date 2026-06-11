package controllers;

import exceptions.EntityNotFoundException;
import exceptions.InvalidVoucherStatusException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import models.PaymentOrder;
import models.Retention;
import models.Supplier;
import models.TaxRule;
import models.Voucher;
import models.VoucherPayment;
import models.enums.VoucherStatus;
import models.enums.VoucherType;

public class PaymentOrderController {

    private static PaymentOrderController instance;
    private HashMap<UUID, PaymentOrder> paymentOrders;
    private int nextOrderNumber;

    private PaymentOrderController() {
        this.paymentOrders = new HashMap<>();
        this.nextOrderNumber = 1;
    }

    public static PaymentOrderController getInstance() {
        if (instance == null) {
            instance = new PaymentOrderController();
        }
        return instance;
    }

    public PaymentOrder createPaymentOrder(
            UUID supplierId,
            List<VoucherPayment> voucherPayments,
            UUID userId) throws EntityNotFoundException, InvalidVoucherStatusException {

        Supplier supplier = SupplierController.getInstance().findById(supplierId);

        for (VoucherPayment vp : voucherPayments) {
            Voucher voucher = VoucherController.getInstance().findById(vp.getVoucherId());
            validateVoucherForPayment(voucher);
        }

        PaymentOrder order = new PaymentOrder(nextOrderNumber++, supplier, userId);

        for (VoucherPayment vp : voucherPayments) {
            order.addVoucherPayment(vp);
        }

        applyRetentions(order);

        paymentOrders.put(order.getId(), order);

        updateVoucherStatuses(voucherPayments);

        return order;
    }

    private void updateVoucherStatuses(List<VoucherPayment> voucherPayments) {
        for (VoucherPayment vp : voucherPayments) {
            try {
                Voucher voucher = VoucherController.getInstance().findById(vp.getVoucherId());

                voucher.setStatus(
                    vp.getAmount() >= voucher.getGrossTotal()
                        ? VoucherStatus.PAID
                        : VoucherStatus.PARTIALLY_PAID
                );

            } catch (EntityNotFoundException ex) {
                System.err.println("updateVoucherStatuses: voucher not found after validation — " + ex.getMessage());
            }
        }
    }

    private void validateVoucherForPayment(Voucher voucher) throws EntityNotFoundException, InvalidVoucherStatusException {
        if (!isPayableType(voucher.getType())) {
            throw new EntityNotFoundException("Comprobante inválido", voucher.getId());
        }
        if (voucher.getStatus() != VoucherStatus.PENDING) {
            throw new InvalidVoucherStatusException(
                "El comprobante N°" + voucher.getNumber() + " no está en estado pendiente.");
        }
    }

    private boolean isPayableType(VoucherType type) {
        return type == VoucherType.FACTURA_A
                || type == VoucherType.FACTURA_B
                || type == VoucherType.FACTURA_C
                || type == VoucherType.NOTA_DEBITO;
    }

    private void applyRetentions(PaymentOrder order) {
        List<TaxRule> taxRules = TaxRuleController.getInstance().getAllTaxRules();
        float baseAmount = order.getTotalVouchersAmount();
        LocalDate issueDate = order.getIssueDate();

        for (TaxRule rule : taxRules) {
            if (order.getSupplier().hasValidCertificationFor(rule.getTaxType(), issueDate)) continue;
            float retentionAmount = rule.calculateRetention(baseAmount);
            if (retentionAmount > 0) {
                order.addRetention(new Retention(rule.getTaxType(), retentionAmount));
            }
        }
    }

    public List<Voucher> getPendingPayableVouchersBySupplier(UUID supplierId) throws EntityNotFoundException {
        SupplierController.getInstance().findById(supplierId);

        List<Voucher> allVouchers = VoucherController.getInstance().findBySupplier(supplierId);
        List<Voucher> pendingPayable = new ArrayList<>();

        for (Voucher v : allVouchers) {
            if (isPayableType(v.getType()) && v.getStatus() == VoucherStatus.PENDING) {
                pendingPayable.add(v);
            }
        }

        return pendingPayable;
    }

    public PaymentOrder findById(UUID id) throws EntityNotFoundException {
        PaymentOrder order = paymentOrders.get(id);

        if (order == null) {
            throw new EntityNotFoundException("Orden de Pago", id);
        }

        return order;
    }

    public List<PaymentOrder> findAll() {
        return new ArrayList<>(paymentOrders.values());
    }

    public List<PaymentOrder> findBySupplier(UUID supplierId) {
        List<PaymentOrder> result = new ArrayList<>();
        for (PaymentOrder order : paymentOrders.values()) {
            if (order.getSupplier().getId().equals(supplierId)) result.add(order);
        }

        return result;
    }

    public void delete(UUID id) throws EntityNotFoundException {
        if (!paymentOrders.containsKey(id)) {
            throw new EntityNotFoundException("Orden de Pago", id);
        }

        paymentOrders.remove(id);
    }
}