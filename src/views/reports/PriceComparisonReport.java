package views.reports;

import controllers.ProductController;
import controllers.SupplierController;
import exceptions.EntityNotFoundException;
import models.Product;
import models.ProductSupplier;
import models.Supplier;
import views.components.AppComboBox;
import views.components.AppFrame;
import views.components.AppTable;
import views.components.ButtonBar;
import views.components.FormPanel;
import views.components.SectionPanel;

import java.util.List;

public class PriceComparisonReport extends AppFrame {

    private AppComboBox<Product> cmbProduct;
    private AppTable table;

    public PriceComparisonReport() {
        initFilters();
        initTable();
    }

    private void initFilters() {
        cmbProduct = new AppComboBox<>();
        for (Product p : ProductController.getInstance().findAll()) cmbProduct.addItem(p);

        FormPanel form = new FormPanel("Filtros");
        form.addRow("Producto:", cmbProduct);
        form.addFullRow(ButtonBar.primary("Consultar", this::query));

        addNorth(form);
    }

    private void initTable() {
        table = new AppTable(new String[]{"Proveedor", "CUIT", "Rubro", "Precio Acordado"});

        SectionPanel section = new SectionPanel("Compulsa de Precios por Proveedor");
        section.addCenter(table);

        addCenter(section);
    }

    @Override
    public void refresh() {
        Product selected = cmbProduct.getSelected();
        cmbProduct.removeAllItems();

        for (Product p : ProductController.getInstance().findAll()) cmbProduct.addItem(p);

        if (selected != null) cmbProduct.setSelectedItem(selected);
    }

    private void query() {
        Product product = cmbProduct.getSelected();
        if (product == null) return;

        List<ProductSupplier> prices = product.getSupplierPrices();

        table.clearRows();
        for (ProductSupplier ps : prices) {
            String supplierName = ps.getSupplierId().toString();
            String cuit = "—";

            try {
                Supplier s = SupplierController.getInstance().findById(ps.getSupplierId());
                supplierName = s.getRazonSocial();
                cuit = s.getCuit();
            } catch (EntityNotFoundException ignored) {}

            table.addRow(new Object[]{
                supplierName,
                cuit,
                ps.getCategory().name(),
                String.format("$ %.2f", ps.getAgreedUnitPrice())
            });
        }
    }
}
