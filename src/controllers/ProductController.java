package controllers;

import exceptions.EntityNotFoundException;
import models.Category;
import models.Product;
import models.ProductSupplier;
import models.enums.TaxType;
import models.enums.UnitOfMeasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ProductController {

    private static ProductController instance;
    private HashMap<UUID, Product> products;

    private ProductController() {
        this.products = new HashMap<>();
    }

    public static ProductController getInstance() {
        if (instance == null) instance = new ProductController();
        return instance;
    }

    public Product create(String code, String description, UnitOfMeasure unitOfMeasure,
                          TaxType taxType, Category category) {
        Product product = new Product(code, description, unitOfMeasure, taxType, category);
        products.put(product.getId(), product);
        return product;
    }

    public Product findById(UUID id) throws EntityNotFoundException {
        Product product = products.get(id);
        if (product == null) throw new EntityNotFoundException("Producto", id);
        return product;
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public void setSupplierPrice(UUID productId, UUID supplierId, float price, Category category)
            throws EntityNotFoundException {
        findById(productId).addSupplierPrice(new ProductSupplier(supplierId, price, category));
    }

    public void delete(UUID id) throws EntityNotFoundException {
        if (!products.containsKey(id)) throw new EntityNotFoundException("Producto", id);
        products.remove(id);
    }
}
