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

        PaymentOrder order = new PaymentOrder(nextOrderNumber++, supplier, userId);

        for (VoucherPayment vp : voucherPayments) {
            order.addVoucherPayment(vp);
        }

        applyRetentions(order);

        paymentOrders.put(order.getId(), order);

        return order;
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

    public List<Voucher> getPendingVouchersBySupplier(UUID supplierId) throws EntityNotFoundException {
        SupplierController.getInstance().findById(supplierId);

        return VoucherController.getInstance().findPendingBySupplier(supplierId);
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