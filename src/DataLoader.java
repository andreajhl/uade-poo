import controllers.ProductController;
import controllers.PurchaseOrderController;
import controllers.SupplierController;
import controllers.UserController;
import controllers.VoucherController;
import models.Product;
import models.ProductSupplier;
import models.PurchaseOrder;
import models.PurchaseOrderDetail;
import models.Supplier;
import models.User;
import models.VoucherDetail;
import models.enums.Category;
import models.enums.IVACondition;
import models.enums.TaxType;
import models.enums.UnitOfMeasure;
import models.enums.VoucherType;

import java.time.LocalDate;
import java.util.List;

public class DataLoader {

    public static void load() {
        try {
            User user = UserController.getInstance().findAll().get(0);

            Product guantes = ProductController.getInstance().create(
                    "P001", "Guantes de Látex", UnitOfMeasure.BOX, TaxType.IVA, Category.INSUMOS_DESCARTABLES);
            Product ibuprofeno = ProductController.getInstance().create(
                    "P002", "Ibuprofeno 400mg", UnitOfMeasure.BOX, TaxType.IVA, Category.MEDICAMENTOS);
            Product alcohol = ProductController.getInstance().create(
                    "P003", "Alcohol 96°", UnitOfMeasure.LITER, TaxType.IVA, Category.MEDICAMENTOS);

            Supplier distmed = SupplierController.getInstance().create(
                    "20-12345678-3", "Distribuidora Médica SA", "DistMed",
                    "Av. Corrientes 1234", "01145678901", "distmed@gmail.com",
                    IVACondition.RESPONSABLE_INSCRIPTO, "123456789",
                    LocalDate.of(2020, 1, 15), 500000f);
            SupplierController.getInstance().addCategory(distmed.getId(), Category.INSUMOS_DESCARTABLES);
            SupplierController.getInstance().addCategory(distmed.getId(), Category.MEDICAMENTOS);

            Supplier farmainsumos = SupplierController.getInstance().create(
                    "30-87654321-5", "Farmainsumos SRL", "Farmainsumos",
                    "Av. Santa Fe 567", "01145671234", "farmainsumos@gmail.com",
                    IVACondition.RESPONSABLE_INSCRIPTO, "987654321",
                    LocalDate.of(2018, 6, 15), 300000f);
            SupplierController.getInstance().addCategory(farmainsumos.getId(), Category.MEDICAMENTOS);
            SupplierController.getInstance().addCategory(farmainsumos.getId(), Category.LIMPIEZA_E_HIGIENE);

            guantes.addSupplierPrice(new ProductSupplier(distmed.getId(), 150f, Category.INSUMOS_DESCARTABLES));
            ibuprofeno.addSupplierPrice(new ProductSupplier(distmed.getId(), 450f, Category.MEDICAMENTOS));
            ibuprofeno.addSupplierPrice(new ProductSupplier(farmainsumos.getId(), 430f, Category.MEDICAMENTOS));
            alcohol.addSupplierPrice(new ProductSupplier(farmainsumos.getId(), 80f, Category.MEDICAMENTOS));

            PurchaseOrder oc1 = PurchaseOrderController.getInstance().createPurchaseOrder(
                    distmed.getId(),
                    List.of(
                            new PurchaseOrderDetail(guantes, 100, 150f),
                            new PurchaseOrderDetail(ibuprofeno, 50, 450f)
                    ),
                    user.getId());

            PurchaseOrderController.getInstance().createPurchaseOrder(
                    farmainsumos.getId(),
                    List.of(
                            new PurchaseOrderDetail(ibuprofeno, 30, 430f),
                            new PurchaseOrderDetail(alcohol, 20, 80f)
                    ),
                    user.getId());

            var factura = VoucherController.getInstance().registerInvoice(
                    distmed.getId(),
                    VoucherType.FACTURA_A,
                    LocalDate.of(2026, 5, 10),
                    List.of(
                            new VoucherDetail(guantes, 100, 150f),
                            new VoucherDetail(ibuprofeno, 50, 450f)
                    ),
                    oc1.getId());

            VoucherController.getInstance().registerDebitNote(
                    distmed.getId(),
                    LocalDate.of(2026, 5, 15),
                    List.of(
                            new VoucherDetail(guantes, 5, 150f)
                    ),
                    factura.getId());

        } catch (Exception e) {
            System.err.println("DataLoader: error al precargar datos — " + e.getMessage());
        }
    }
}
