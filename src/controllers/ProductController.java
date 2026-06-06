package controllers;

import exceptions.EntityNotFoundException;
import models.Category;
import models.Product;
import models.ProductSupplierPrice;

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

    public Product create(String code, String description, String unitOfMeasure,
                          float ivaRate, Category category) {
        Product product = new Product(code, description, unitOfMeasure, ivaRate, category);

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

    public void setSupplierPrice(UUID productId, UUID supplierId, float price) throws EntityNotFoundException {
        findById(productId).addSupplierPrice(new ProductSupplierPrice(supplierId, price));
    }

    public void delete(UUID id) throws EntityNotFoundException {
        if (!products.containsKey(id)) throw new EntityNotFoundException("Producto", id);
        products.remove(id);
    }
}
