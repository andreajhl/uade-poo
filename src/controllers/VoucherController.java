package controllers;

import exceptions.EntityNotFoundException;
import models.Supplier;
import models.Voucher;
import models.enums.VoucherStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class VoucherController {

    private static VoucherController instance;
    private HashMap<UUID, Voucher> vouchers;
    private int nextVoucherNumber;

    private VoucherController() {
        this.vouchers = new HashMap<>();
        this.nextVoucherNumber = 1;
    }

    public static VoucherController getInstance() {
        if (instance == null) {
            instance = new VoucherController();
        }
        return instance;
    }

    public Voucher create(Supplier supplier, float total) {
        LocalDate issueDate = LocalDate.now();
        Voucher voucher = new Voucher(nextVoucherNumber++, issueDate, supplier, total);
        vouchers.put(voucher.getId(), voucher);
        return voucher;
    }

    public Voucher findById(UUID id) throws EntityNotFoundException {
        Voucher voucher = vouchers.get(id);
        if (voucher == null) {
            throw new EntityNotFoundException("Comprobante", id);
        }
        return voucher;
    }

    public List<Voucher> findAll() {
        return new ArrayList<>(vouchers.values());
    }

    public List<Voucher> findBySupplier(UUID supplierId) {
        List<Voucher> result = new ArrayList<>();
        for (Voucher v : vouchers.values()) {
            if (v.getSupplier().getId().equals(supplierId)) {
                result.add(v);
            }
        }
        return result;
    }

    public List<Voucher> findByStatus(VoucherStatus status) {
        List<Voucher> result = new ArrayList<>();
        for (Voucher v : vouchers.values()) {
            if (v.getStatus() == status) {
                result.add(v);
            }
        }
        return result;
    }

    public List<Voucher> findPendingBySupplier(UUID supplierId) {
        List<Voucher> result = new ArrayList<>();
        for (Voucher v : vouchers.values()) {
            if (v.getSupplier().getId().equals(supplierId) && v.getStatus() == VoucherStatus.PENDING) {
                result.add(v);
            }
        }
        return result;
    }

    public void updateStatus(UUID id, VoucherStatus newStatus) throws EntityNotFoundException {
        Voucher voucher = findById(id);
        voucher.setStatus(newStatus);
    }

    public void delete(UUID id) throws EntityNotFoundException {
        if (!vouchers.containsKey(id)) {
            throw new EntityNotFoundException("Comprobante", id);
        }
        vouchers.remove(id);
    }
}