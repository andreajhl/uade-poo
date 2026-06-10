package controllers;

import exceptions.EntityNotFoundException;
import models.Category;
import models.Supplier;
import models.enums.IVACondition;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SupplierController {

    private static SupplierController instance;
    private HashMap<UUID, Supplier> suppliers;

    private SupplierController() {
        this.suppliers = new HashMap<>();
    }

    public static SupplierController getInstance() {
        if (instance == null) instance = new SupplierController();
        return instance;
    }

    public Supplier create(String cuit, String razonSocial, String fantasyName, String address,
                           String phone, String email, IVACondition ivaCondition,
                           String ingresosBrutos, LocalDate activityStartDate, float creditLimit) {
        Supplier supplier = new Supplier(cuit, razonSocial, fantasyName, address,
                phone, email, ivaCondition, ingresosBrutos, activityStartDate, creditLimit);
        suppliers.put(supplier.getId(), supplier);
        return supplier;
    }

    public Supplier findById(UUID id) throws EntityNotFoundException {
        Supplier supplier = suppliers.get(id);
        if (supplier == null) throw new EntityNotFoundException("Proveedor", id);
        return supplier;
    }

    public List<Supplier> findAll() {
        return new ArrayList<>(suppliers.values());
    }

    public void addCategory(UUID supplierId, Category category) throws EntityNotFoundException {
        findById(supplierId).addCategory(category);
    }

    public void removeCategory(UUID supplierId, Category category) throws EntityNotFoundException {
        findById(supplierId).removeCategory(category);
    }

    public void delete(UUID id) throws EntityNotFoundException {
        if (!suppliers.containsKey(id)) throw new EntityNotFoundException("Proveedor", id);
        suppliers.remove(id);
    }
}
