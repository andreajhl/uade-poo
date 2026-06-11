# Requerimientos Técnicos — TPO Farmared

> Leyenda: ✅ Completo · 🔶 Parcial (modelo existe, falta UI o lógica) · ❌ Pendiente

---

## Módulo 1 — Usuarios y Seguridad

| # | Requerimiento | Estado | Notas |
|---|---------------|--------|-------|
| 1.1 | Modelo `User` con username, fullName, `Role` | ✅ | `User`, `Role` |
| 1.2 | Enum `Permission` (AUTHORIZE_PURCHASE_ORDER, …) | ✅ | |
| 1.3 | `UserController` con login/logout, findByPermission | ✅ | |
| 1.4 | `SelectUserDialog` — selección de usuario al iniciar | ✅ | |
| 1.5 | Seed de usuarios por defecto (admin, supervisor, operador) | ✅ | en `UserController` |
| 1.6 | Pantalla de gestión de usuarios (ABM) | ❌ | |

---

## Módulo 2 — Proveedores

| # | Requerimiento | Estado | Notas |
|---|---------------|--------|-------|
| 2.1 | Modelo `Supplier` (CUIT, razón social, nombre fantasía, domicilio, tel, email, IVA, IIBB, fecha inicio) | ✅ | |
| 2.2 | `SupplierController` — create, findAll, findById, delete | ✅ | |
| 2.3 | `SupplierFrame` — listado de proveedores | ✅ | |
| 2.4 | `CreateSupplierDialog` — alta de proveedor | ✅ | |
| 2.5 | `ManageCategoriesDialog` — asociación de rubros al proveedor | ✅ | |
| 2.6 | Editar proveedor (dialog + controller) | ❌ | |
| 2.7 | Eliminar proveedor | ❌ | |
| 2.8 | Modelo `CertificationRetention` (tipo impuesto, fecha emisión, fecha vencimiento) | ✅ | |
| 2.9 | `Supplier.hasValidCertificationFor(TaxType, LocalDate)` | ✅ | |
| 2.10 | `SupplierController.addCertification` | ❌ | método no existe aún |
| 2.11 | `ManageCertificationsDialog` — ver/agregar certificados de no retención | ❌ | |

---

## Módulo 3 — Productos y Servicios

| # | Requerimiento | Estado | Notas |
|---|---------------|--------|-------|
| 3.1 | Modelo `Product` (código, descripción, UDM, TaxType, Category) | ✅ | |
| 3.2 | Modelo `ProductSupplier` (supplierId, precio acordado, rubro) | ✅ | |
| 3.3 | `Product.getPriceForSupplier(UUID)` | ✅ | |
| 3.4 | `ProductController` — create, findAll, findById, delete, setSupplierPrice | ✅ | |
| 3.5 | `ProductFrame` — listado de productos | ✅ | |
| 3.6 | `CreateProductDialog` — alta de producto | ✅ | |
| 3.7 | `SetSupplierPriceDialog` — asignar precio por proveedor desde la UI | ❌ | |
| 3.8 | Editar producto | ❌ | |
| 3.9 | Eliminar producto | ❌ | |

---

## Módulo 4 — Reglas Impositivas (Retenciones)

| # | Requerimiento | Estado | Notas |
|---|---------------|--------|-------|
| 4.1 | Modelo `TaxRule` (TaxType, porcentaje por defecto, mínimo no imponible) | ✅ | |
| 4.2 | Modelo `TaxScale` (desde, hasta, porcentaje) — escalas variables | ✅ | |
| 4.3 | `TaxRule.calculateRetention(float baseAmount)` — lógica con mínimo + escalas | ✅ | |
| 4.4 | `TaxRuleController` — create, update, findByTaxType | ✅ | |
| 4.5 | `CreateTaxRuleDialog` — ABM de reglas impositivas con escalas | ✅ | |
| 4.6 | `TaxRuleFrame` — listado de reglas impositivas | ❌ | tab pendiente en MainFrame |
| 4.7 | Editar regla impositiva desde la UI | ❌ | |

---

## Módulo 5 — Órdenes de Compra

| # | Requerimiento | Estado | Notas |
|---|---------------|--------|-------|
| 5.1 | Modelo `PurchaseOrder` (número, fecha, proveedor, detalles, total, estado, autorización) | ✅ | |
| 5.2 | Modelo `PurchaseOrderDetail` (producto, cantidad, precio acordado, subtotal) | ✅ | |
| 5.3 | Enum `PurchaseOrderStatus` | ✅ | |
| 5.4 | `PurchaseOrderController.createPurchaseOrder` con validación de tope de deuda | ✅ | |
| 5.5 | `PurchaseOrderController.createPurchaseOrderWithAuthorization` | ✅ | |
| 5.6 | Excepción `CreditLimitExceededException` | ✅ | |
| 5.7 | `SupervisorApprovalDialog` — flujo de autorización de supervisor | ✅ | |
| 5.8 | `PurchaseOrderFrame` — listado de OCs | ✅ | |
| 5.9 | `CreatePurchaseOrderDialog` — emisión de OC con ítems | ✅ | |
| 5.10 | Ver detalle de una OC existente | ❌ | |
| 5.11 | `PurchaseOrderController.calculateOutstandingDebt` | ✅ | |

---

## Módulo 6 — Comprobantes (Facturas / NC / ND)

| # | Requerimiento | Estado | Notas |
|---|---------------|--------|-------|
| 6.1 | Jerarquía de modelos: `Voucher` (base) → `Invoice`, `CreditNote`, `DebitNote` | ❌ | `VoucherStatus` enum existe |
| 6.2 | `Voucher` con CUIT, fecha, tipo, comprobantes asociados a OC | ❌ | |
| 6.3 | Validación: productos de factura coinciden con OC | ❌ | |
| 6.4 | Validación: precios de factura coinciden con OC | ❌ | |
| 6.5 | Validación: impuestos e IVA de factura consistentes | ❌ | |
| 6.6 | Autorización supervisor para facturas sin OC previa | ❌ | |
| 6.7 | Autorización supervisor para facturas con diferencia de precios | ❌ | |
| 6.8 | `VoucherController` — create, findBySupplier, findUnpaid | ❌ | |
| 6.9 | `VoucherFrame` — listado de comprobantes | ❌ | |
| 6.10 | `CreateVoucherDialog` — registro de factura/NC/ND | ❌ | |

---

## Módulo 7 — Órdenes de Pago

| # | Requerimiento | Estado | Notas |
|---|---------------|--------|-------|
| 7.1 | Jerarquía: `PaymentMethod` (base) → `Cash`, `BankTransfer`, `OwnCheck`, `ThirdPartyCheck` | ❌ | |
| 7.2 | `Check` con número, fecha emisión, fecha vencimiento, firmante, importe | ❌ | |
| 7.3 | Modelo `PaymentOrder` (proveedor, fecha, comprobantes cancelados, retenciones, medios de pago) | ❌ | |
| 7.4 | Modelo `Retention` (TaxType, monto) | ✅ | modelo existe |
| 7.5 | Aplicar `TaxRule.calculateRetention` al emitir OP | ❌ | |
| 7.6 | Respetar `CertificationRetention` activa → no retener si certificado vigente | ❌ | |
| 7.7 | Cancelación total y parcial de comprobantes | ❌ | |
| 7.8 | `PaymentOrderController` — create, findBySupplier | ❌ | |
| 7.9 | `PaymentOrderFrame` — listado de OPs | ❌ | |
| 7.10 | `CreatePaymentOrderDialog` — emisión de OP con medios de pago y retenciones | ❌ | |

---

## Módulo 8 — Consultas y Reportes

| # | Requerimiento | Estado | Notas |
|---|---------------|--------|-------|
| 8.1 | Total de facturas (monto y cantidad) por día y por proveedor | ❌ | |
| 8.2 | Cuenta corriente detallada de un proveedor | ❌ | |
| 8.3 | Listado de documentos impagos por proveedor | ❌ | |
| 8.4 | Detalle de pagos realizados por proveedor | ❌ | |
| 8.5 | Compulsa de precios de un producto (histórico por rubro/proveedor) | ❌ | |
| 8.6 | Reporte de OCs y OPs emitidas | 🔶 | listados básicos existen en frames |
| 8.7 | Total de deuda vigente segregado por proveedor | 🔶 | `calculateOutstandingDebt` en controller |
| 8.8 | Total retenido por tipo de impuesto | ❌ | |
| 8.9 | Libro IVA Compras (CUIT, razón social, fecha, tipo comprobante, IVA por alícuota, total) | ❌ | |
| 8.10 | `ReportsFrame` — pantalla central de consultas con tabs | ❌ | |

---

## Infraestructura y Arquitectura

| # | Requerimiento | Estado | Notas |
|---|---------------|--------|-------|
| I.1 | Patrón MVC con Singleton en controllers | ✅ | |
| I.2 | Datos en memoria (sin persistencia) | ✅ | HashMaps en controllers |
| I.3 | Java 17+, Swing, sin librerías externas | ✅ | |
| I.4 | `javax.swing` / `java.awt` solo en `views/components/` | ✅ | refactor realizado |
| I.5 | `MainFrame` con tabs por módulo | 🔶 | faltan tabs: Reglas Impositivas, Comprobantes, OPs, Reportes |

---

## Orden de Implementación Sugerida

1. **Módulo 6** — Comprobantes (modelos + controller + UI básica): habilita la cuenta corriente y los pagos
2. **Módulo 7** — Órdenes de Pago (modelos + controller + UI): cierra el ciclo de compra→pago
3. **Módulo 2 pendiente** — Certificados de no retención (dialog + controller)
4. **Módulo 4 pendiente** — `TaxRuleFrame` + edición
5. **Módulo 3 pendiente** — Precios por proveedor (UI), edición/baja de productos
6. **Módulo 8** — Consultas y Reportes (último, depende de todo lo anterior)
7. **I.5** — Completar tabs de `MainFrame` a medida que se agregan módulos
