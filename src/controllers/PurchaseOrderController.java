package controllers;

import exceptions.CreditLimitExceededException;
import exceptions.EntityNotFoundException;
import models.PurchaseOrder;
import models.PurchaseOrderDetail;
import models.Supplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PurchaseOrderController {

    private static PurchaseOrderController instance;
    private HashMap<UUID, PurchaseOrder> purchaseOrders;
    private int nextOrderNumber;

    private PurchaseOrderController() {
        this.purchaseOrders = new HashMap<>();
        this.nextOrderNumber = 1;
    }

    public static PurchaseOrderController getInstance() {
        if (instance == null)  instance = new PurchaseOrderController();
        return instance;
    }

    public PurchaseOrder createPurchaseOrder(UUID supplierId, List<PurchaseOrderDetail> details, UUID userId)
            throws EntityNotFoundException, CreditLimitExceededException {
        Supplier supplier = SupplierController.getInstance().findById(supplierId);

        PurchaseOrder order = new PurchaseOrder(nextOrderNumber++, supplier, userId);
        for (PurchaseOrderDetail detail : details) order.addDetail(detail);

        validateCreditLimit(supplier, order);

        purchaseOrders.put(order.getId(), order);

        return order;
    }

    public void validateCreditLimit(Supplier supplier, PurchaseOrder order)
            throws CreditLimitExceededException {
        float currentDebt = calculateOutstandingDebt(supplier.getId());

        if (currentDebt + order.getTotal() > supplier.getCreditLimit()) {
            throw new CreditLimitExceededException(currentDebt, order.getTotal(), supplier.getCreditLimit());
        }
    }

    public float calculateOutstandingDebt(UUID supplierId) {
        float debt = 0f;
        for (PurchaseOrder order : purchaseOrders.values()) {
            if (order.getSupplier().getId().equals(supplierId)) debt += order.getTotal();
        }

        return debt;
    }

    public List<PurchaseOrder> findBySupplier(UUID supplierId) {
        List<PurchaseOrder> result = new ArrayList<>();
        for (PurchaseOrder order : purchaseOrders.values()) {
            if (order.getSupplier().getId().equals(supplierId)) result.add(order);
        }

        return result;
    }

    public PurchaseOrder findById(UUID id) throws EntityNotFoundException {
        PurchaseOrder order = purchaseOrders.get(id);
        if (order == null) throw new EntityNotFoundException("Orden de Compra", id);
        return order;
    }

    public List<PurchaseOrder> findAll() {
        return new ArrayList<>(purchaseOrders.values());
    }
}
