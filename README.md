# Farmared — Sistema de Gestión de Compras

Trabajo Práctico Obligatorio — Programación Orientada a Objetos · UADE

## Descripción

Sistema de escritorio para la gestión de compras y proveedores de la cadena de farmacias **Farmared**. Permite administrar proveedores, productos, órdenes de compra, comprobantes fiscales (facturas, notas de débito y crédito) y órdenes de pago, con generación de reportes operativos.

## Tecnología

- **Java 17**
- **Swing** (`javax.swing`) — interfaz gráfica de escritorio
- Sin dependencias externas ni frameworks
- Datos en memoria (sin persistencia)

## Arquitectura

El sistema sigue el patrón **MVC** con controladores Singleton:

```
src/
├── controllers/     Lógica de negocio (Singleton por módulo)
├── models/          Entidades de dominio y enums
├── views/           Vistas Swing organizadas por módulo
│   └── components/  Componentes reutilizables (AppTable, FormPanel, etc.)
├── exceptions/      Excepciones de dominio
├── DataLoader.java  Precarga de datos de ejemplo
└── Main.java        Punto de entrada
```

## Módulos

| Módulo | Funcionalidad |
|---|---|
| **Proveedores** | Alta, edición, baja y gestión de certificaciones |
| **Productos** | Alta, edición, baja; precio acordado por proveedor |
| **Órdenes de Compra** | Creación con ítems, control de límite de crédito, detalle |
| **Comprobantes** | Registro de Facturas A/B/C, Notas de Débito y Notas de Crédito; validación contra OC |
| **Órdenes de Pago** | Emisión con comprobantes, retenciones impositivas y medios de pago |
| **Reportes** | Deuda pendiente, comprobantes impagos, libro IVA, compulsas de precios, retenciones, resumen de OPs |
| **Usuarios** | Login con roles y permisos; autorización de supervisor |
| **Reglas impositivas** | Administración de escalas de retención por tipo de impuesto |

## Cómo ejecutar

### Requisitos

- JDK 17 o superior

### Desde IntelliJ IDEA

1. Abrir el proyecto (raíz del repositorio).
2. Configurar el SDK a Java 17.
3. Marcar `src/` como *Sources Root*.
4. Ejecutar `Main.java`.

### Desde línea de comandos

```bash
# Compilar
javac -d out/production/uade-progra-II $(find src -name "*.java")

# Ejecutar
java -cp out/production/uade-progra-II Main
```

## Inicio de sesión

Al iniciar se muestra un selector de usuario. Usuarios precargados:

| Usuario | Nombre | Rol |
|---|---|---|
| `admin` | Administrador | Supervisor |
| `maria` | María González | Supervisor |
| `juan` | Juan Pérez | Operador |

## Datos de ejemplo

`DataLoader` precarga automáticamente al iniciar:
- 2 proveedores (Distribuidora Médica SA, Farmainsumos SRL)
- 3 productos (Guantes de Látex, Ibuprofeno 400mg, Alcohol 96°)
- 2 órdenes de compra
- 1 factura con nota de débito asociada
