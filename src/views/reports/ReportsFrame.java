package views.reports;

import views.components.AppFrame;
import views.components.AppTabs;

public class ReportsFrame extends AppFrame {

    private final InvoicesByDateReport invoicesByDate  = new InvoicesByDateReport();
    private final UnpaidVouchersReport unpaidVouchers  = new UnpaidVouchersReport();
    private final PriceComparisonReport priceComparison = new PriceComparisonReport();
    private final OutstandingDebtReport outstandingDebt = new OutstandingDebtReport();
    private final IVABookReport ivaBook                = new IVABookReport();
    private final PaymentOrdersSummaryReport paymentOrders = new PaymentOrdersSummaryReport();
    private final RetentionsDetailReport retentionsDetail = new RetentionsDetailReport();

    public ReportsFrame() {
        AppTabs tabs = new AppTabs();
        tabs.addTab("Facturas por Día/Proveedor", invoicesByDate);
        tabs.addTab("Documentos Impagos",         unpaidVouchers);
        tabs.addTab("Compulsa de Precios",         priceComparison);
        tabs.addTab("Deuda Vigente",               outstandingDebt);
        tabs.addTab("Libro IVA Compras",           ivaBook);
        tabs.addTab("Órdenes de Pago",             paymentOrders);
        tabs.addTab("Detalle de Retenciones",      retentionsDetail);
        addCenter(tabs);
    }

    @Override
    public void refresh() {
        invoicesByDate.refresh();
        unpaidVouchers.refresh();
        priceComparison.refresh();
        outstandingDebt.refresh();
        paymentOrders.refresh();
    }
}