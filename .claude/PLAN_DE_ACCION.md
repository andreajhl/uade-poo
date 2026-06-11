# Plan de Acción — TPO Farmared (End-to-End)

> Última actualización: 2026-06-09  
> Leyenda: ✅ Completo · 🔶 Parcial (modelo/lógica existe, falta UI) · ❌ Pendiente

---

## Estado actual del proyecto

El ciclo básico de compra está operativo:
alta de proveedores → alta de productos → emisión de OC con control de tope de deuda y autorización de supervisor.

**Pendiente para completar el TP:**
- Comprobantes (Facturas/NC/ND) con validaciones contra la OC
- Órdenes de Pago con retenciones y medios de pago
- Certificados de no retención en proveedores
- Asignación de precio por proveedor desde la UI
- Consultas y reportes (cuenta corriente, libro IVA, etc.)
- Pantallas menores faltantes en módulos existentes

---

## Feature 1 — Gestión de Proveedores

| Tarea | Estado |
|-------|--------|
| Alta de proveedor (todos los campos del TP) | ✅ |
| Listado de proveedores | ✅ |
| Asociar/quitar rubros al proveedor | ✅ |
| Modelo `CertificationRetention` (tipo, fechas) y `hasValidCertificationFor()` | ✅ |
| `SupplierController.addCertification()` | ❌ |
| `ManageCertificationsDialog` — ver/agregar certificados de no retención | ❌ |
| Editar proveedor — `EditSupplierDialog` + `SupplierController.update()` | ❌ |
| Eliminar proveedor desde la toolbar | ❌ |

---

## Feature 2 — Catálogo de Productos

| Tarea | Estado |
|-------|--------|
| Alta de producto (código, descripción, UDM, TaxType, rubro) | ✅ |
| Listado de productos | ✅ |
| Modelo `ProductSupplier` y `getPriceForSupplier()` | ✅ |
| `ProductController.setSupplierPrice()` | ✅ |
| `SetSupplierPriceDialog` — asignar precio por proveedor desde la UI | ❌ |
| Editar producto | ❌ |
| Eliminar producto | ❌ |

---

## Feature 3 — Reglas Impositivas

| Tarea | Estado |
|-------|--------|
| Alta de regla impositiva con escalas | ✅ |
| `TaxRule.calculateRetention(baseAmount)` con mínimo no imponible y escalas | ✅ |
| `TaxRuleController` | ✅ |
| `TaxRuleFrame` — listado de reglas (tab en `MainFrame`) | ❌ |
| Editar regla impositiva | ❌ |

---

## Feature 4 — Órdenes de Compra

| Tarea | Estado |
|-------|--------|
| `PurchaseOrder`, `PurchaseOrderDetail`, `PurchaseOrderStatus` | ✅ |
| Emisión de OC con ítems y cálculo de total | ✅ |
| Validación de tope de deuda (`CreditLimitExceededException`) | ✅ |
| Autorización de supervisor (`SupervisorApprovalDialog`) | ✅ |
| `calculateOutstandingDebt(supplierId)` | ✅ |
| Listado de OCs | ✅ |
| Ver detalle de una OC existente | ❌ |

---

## Feature 5 — Comprobantes: Facturas, NC y ND *(nuevo)*

### Modelos a crear

| Clase | Descripción |
|-------|-------------|
| `Voucher` | id, número, tipo (`FACTURA_A/B/C`, `NOTA_CREDITO`, `NOTA_DEBITO`), fecha, `Supplier`, lista de `VoucherDetail`, lista de `PurchaseOrder` relacionadas, `VoucherStatus`, `Authorization` opcional |
| `VoucherDetail` | `Product`, cantidad, precio unitario, subtotal neto, IVA aplicado |

> `VoucherStatus` enum ya existe. Herencia `Invoice`/`CreditNote`/`DebitNote` es **opcional** — con un campo tipo enum en `Voucher` alcanza.

### Lógica de negocio

| Método | Descripción |
|--------|-------------|
| `Voucher.validateAgainstPurchaseOrder(PurchaseOrder oc)` | Verifica que productos, cantidades y precios coincidan |
| `Voucher.hasPriceDeviation(PurchaseOrder oc)` | Retorna true si hay diferencia de precios |
| Factura sin OC previa → requiere autorización supervisor | |
| Factura con diferencia de precios → requiere autorización supervisor | |

### Controller: `VoucherController`

| Método | Descripción |
|--------|-------------|
| `registerVoucher(supplierId, type, details, relatedOCIds, userId)` | Crea el comprobante; internamente valida OC y precios; si hay desvío → `VoucherDeviationException` |
| `registerVoucherWithAuthorization(...)` | Versión con `Authorization` para desvíos |
| `findAll()` | |
| `findBySupplier(UUID supplierId)` | |
| `findUnpaid(UUID supplierId)` | Status != PAID — necesario para crear OP |
| `getTotalByDayAndSupplier()` | Para reporte 8.1 |
| `getIVABook()` | Para Libro IVA Compras |

### Views

| Clase | Descripción |
|-------|-------------|
| `VoucherFrame extends AppFrame` | Listado de comprobantes con tabla; botón "Nuevo Comprobante" |
| `CreateVoucherDialog extends AppDialog` | Seleccionar proveedor y tipo, asociar OCs (opcional), agregar ítems con precio, mostrar desvíos, pedir autorización si corresponde |

### Excepciones

- `VoucherDeviationException` (diferencia de precios o sin OC)

---

## Feature 6 — Órdenes de Pago *(nuevo)*

### Modelos a crear

| Clase | Descripción |
|-------|-------------|
| `PaymentMethod` *(abstracta)* | importe |
| `CashPayment extends PaymentMethod` | |
| `BankTransferPayment extends PaymentMethod` | referencia/CBU |
| `OwnCheckPayment extends PaymentMethod` | número, fecha emisión, fecha vencimiento, firmante |
| `ThirdPartyCheckPayment extends PaymentMethod` | igual que `OwnCheck` |
| `PaymentOrderDetail` | `Voucher` cancelado, importe aplicado (puede ser parcial) |
| `PaymentOrder` | id, número, fecha, `Supplier`, lista de `PaymentOrderDetail`, lista de `Retention` (calculadas), lista de `PaymentMethod`, total bruto, total retenciones, total neto |

> `Retention` model ya existe (TaxType, amount).

### Lógica de negocio

| Regla | Descripción |
|-------|-------------|
| Calcular retenciones al emitir OP | Para cada `TaxType` activo: si el proveedor **no** tiene `CertificationRetention` vigente → `TaxRule.calculateRetention(baseAmount)` |
| Cancelación total | `VoucherStatus → PAID` |
| Cancelación parcial | `VoucherStatus → PARTIALLY_PAID`; registrar importe parcial en `PaymentOrderDetail` |
| Una OP puede usar múltiples medios de pago | La suma de medios debe cubrir el total neto |

### Controller: `PaymentOrderController`

| Método | Descripción |
|--------|-------------|
| `createPaymentOrder(supplierId, List<PaymentOrderDetail>, List<PaymentMethod>, userId)` | Calcula retenciones, valida suma de medios de pago = total neto |
| `findAll()` | |
| `findBySupplier(UUID supplierId)` | |
| `getTotalRetainedByTaxType()` | Para reporte 8.8 |

### Views

| Clase | Descripción |
|-------|-------------|
| `PaymentOrderFrame extends AppFrame` | Listado de OPs |
| `CreatePaymentOrderDialog extends AppDialog` | Seleccionar proveedor → ver comprobantes impagos → ingresar importes a cancelar → mostrar retenciones calculadas (indicar si hay certificado activo) → agregar medios de pago hasta cubrir total neto → confirmar |

---

## Feature 7 — Consultas y Reportes *(nuevo)*

Depende de Feature 5 y Feature 6 completos.

### Lógica a agregar en controllers existentes

| Método | Controller |
|--------|-----------|
| `getOutstandingDebtBySupplier()` → `Map<Supplier, Float>` | `PurchaseOrderController` / `VoucherController` |
| `getCurrentAccountBySupplier(UUID)` → lista ordenada de movimientos | `VoucherController` + `PaymentOrderController` |
| `getPaymentsBySupplier(UUID)` | `PaymentOrderController` |
| `getPriceHistoryByProduct(UUID)` → precios por proveedor/rubro | `ProductController` |
| `getIVABook()` → filas con CUIT, razón social, fecha, tipo, desglose IVA | `VoucherController` |

### View: `ReportsFrame extends AppFrame`

Panel con tabs o combo para seleccionar consulta:

| Consulta | Descripción |
|----------|-------------|
| Facturas por día/proveedor | Tabla con filtro de proveedor y rango de fechas |
| Cuenta corriente | Combo de proveedor → tabla con saldo acumulado (OCs → Facturas → Pagos) |
| Documentos impagos | Combo de proveedor → facturas con saldo pendiente |
| Pagos realizados | Combo de proveedor → OPs con detalle |
| Compulsa de precios | Combo de producto → tabla de precios por proveedor/rubro |
| Deuda vigente | Tabla por proveedor con total de deuda |
| Retenciones por impuesto | Tabla agrupada por `TaxType` con total retenido |
| Libro IVA Compras | CUIT, razón social, fecha, tipo, base 21%, IVA 21%, base 10.5%, IVA 10.5%, exento, total |

---

## Tabs finales en `MainFrame`

| # | Tab | Estado |
|---|-----|--------|
| 1 | Proveedores | ✅ |
| 2 | Productos | ✅ |
| 3 | Órdenes de Compra | ✅ |
| 4 | Reglas Impositivas | ❌ `TaxRuleFrame` |
| 5 | Comprobantes | ❌ `VoucherFrame` |
| 6 | Órdenes de Pago | ❌ `PaymentOrderFrame` |
| 7 | Consultas | ❌ `ReportsFrame` |

---

## Orden de implementación recomendado

```
Feature 5 — Comprobantes
    ↓ (habilita cuenta corriente y pagos)
Feature 6 — Órdenes de Pago
    ↓ (cierra el ciclo)
Feature 1 faltante — Certificados de no retención  ←  (necesario para Feature 6)
Feature 2 faltante — Precios por proveedor UI       ←  (necesario para Feature 5)
Feature 3 faltante — TaxRuleFrame + edición
Feature 7 — Reportes  ←  (último, depende de todo)
Edición/baja de entidades (Features 1, 2, 3)
```

---

## Componentes de UI disponibles para reutilizar

| Componente | Usar para |
|-----------|-----------|
| `AppDialog` | Base de todos los dialogs nuevos |
| `AppFrame` | Base de todos los frames nuevos |
| `AppTextField` / `PlaceholderTextField` | Campos de texto |
| `AppComboBox<T>` | Selección de proveedor, tipo, etc. |
| `AppTable` | Todas las tablas |
| `AppList<T>` | Listas con scroll |
| `AppButton` | Variantes `primary` y `danger` |
| `ButtonBar` | Barra de botones |
| `FormPanel` | Formularios etiqueta+campo |
| `SectionPanel` | Panel con título y `addCenter/North/South` |
| `BorderPanel` | Layout sin título |
| `AppScrollPane` | Scroll wrapper |
| `Alerts` | Mensajes de validación/éxito/error/confirmación |
