package controllers;

import exceptions.EntityNotFoundException;
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
            UUID userId) throws EntityNotFoundException {

        Supplier supplier = SupplierController.getInstance().findById(supplierId);

        // Validar que todos los vouchers sean de tipo permitido y estén PENDING
        for (VoucherPayment vp : voucherPayments) {
            Voucher voucher = VoucherController.getInstance().findById(vp.getVoucherId());
            validateVoucherForPayment(voucher);
        }

        // Validar certificaciones de retención
        validateRetentionCertifications(supplier);

        PaymentOrder order = new PaymentOrder(nextOrderNumber++, supplier, userId);

        for (VoucherPayment vp : voucherPayments) {
            order.addVoucherPayment(vp);
        }

        applyRetentions(order);

        paymentOrders.put(order.getId(), order);

        return order;
    }

    private void validateVoucherForPayment(Voucher voucher) throws EntityNotFoundException {
        // Solo se pueden pagar Facturas (A, B, C) y Notas de Débito
        boolean isPayableType = voucher.getType() == VoucherType.FACTURA_A
                || voucher.getType() == VoucherType.FACTURA_B
                || voucher.getType() == VoucherType.FACTURA_C
                || voucher.getType() == VoucherType.NOTA_DEBITO;

        if (!isPayableType) {
            throw new EntityNotFoundException(
                "Comprobante inválido", voucher.getId()
            );
        }

        // Validar que esté PENDING
        if (voucher.getStatus() != VoucherStatus.PENDING) {
            throw new EntityNotFoundException(
                "Comprobante no pendiente", voucher.getId()
            );
        }
    }

    private void validateRetentionCertifications(Supplier supplier) {
        // Advertencia: Si el proveedor no tiene certificaciones vigentes
        // En versiones futuras, esto podría ser una excepción
        // Por ahora es solo informativo
        int activeCertifications = 0;
        if (supplier.getCertifications() != null) {
            activeCertifications = (int) supplier.getCertifications().stream()
                    .filter(c -> c.isValid(java.time.LocalDate.now()))
                    .count();
        }

        if (activeCertifications == 0) {
            // Log o advertencia - en versiones futuras mostrar en UI
            System.out.println("⚠️ Advertencia: Proveedor sin certificaciones de retención vigentes");
        }
    }

    private void applyRetentions(PaymentOrder order) {
        List<TaxRule> taxRules = TaxRuleController.getInstance().getAllTaxRules();

        float baseAmount = order.getTotalVouchersAmount();

        for (TaxRule rule : taxRules) {
            float retentionAmount = rule.calculateRetention(baseAmount);
            if (retentionAmount > 0) {
                Retention retention = new Retention(rule.getTaxType(), retentionAmount);
                order.addRetention(retention);
            }
        }
    }

    public List<Voucher> getPendingPayableVouchersBySupplier(UUID supplierId) throws EntityNotFoundException {
        SupplierController.getInstance().findById(supplierId);

        List<Voucher> allVouchers = VoucherController.getInstance().findBySupplier(supplierId);
        List<Voucher> pendingPayable = new ArrayList<>();

        for (Voucher v : allVouchers) {
            // Solo Facturas A/B/C y Notas de Débito con estado PENDING
            boolean isPayableType = v.getType() == VoucherType.FACTURA_A
                    || v.getType() == VoucherType.FACTURA_B
                    || v.getType() == VoucherType.FACTURA_C
                    || v.getType() == VoucherType.NOTA_DEBITO;

            if (isPayableType && v.getStatus() == VoucherStatus.PENDING) {
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
            if (order.getSupplier().getId().equals(supplierId)) {
                result.add(order);
            }
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