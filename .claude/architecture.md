# Architecture — MVC with Singleton Controllers

## Layers

```
┌────────────────────────────────────────┐
│ VIEW (Swing - JFrame, JPanel, JDialog) │  ← User interaction, event capture
└──────────────────┬─────────────────────┘
                   ↓
┌────────────────────────────────────────┐
│ CONTROLLER (Singleton)                  │  ← Data storage + CRUD + business logic
└──────────────────┬─────────────────────┘
                   ↓
┌────────────────────────────────────────┐
│ MODEL (Domain entities)                 │  ← Constructor + own methods
└────────────────────────────────────────┘
```

## View rules

- Only Swing components and event handlers.
- No business logic.
- Calls controllers via `XxxController.getInstance()`.
- Never instantiates a model entity to store it — that is the controller's responsibility.
- Displays errors with `JOptionPane.showMessageDialog(...)` in Spanish.
- One frame per main module, dialogs for forms.

## Controller rules

- **Singleton mandatory** — see template below.
- Stores its own entity collection in a `HashMap<UUID, Entity>` (or `HashMap<keyType, Entity>` when the diagram defines a natural key).
- Exposes CRUD: `create`, `update`, `delete`, `findById`, `findAll`, plus domain-specific queries.
- Contains business logic related to its entity.
- For cross-entity operations, calls other controllers via `getInstance()`.

## Model rules

- Entity only: private attributes, constructor, getters/setters, own behavior methods.
- Never persists, never searches other entities, never calls controllers.
- Abstract classes only where polymorphism is justified by the diagram.

## Mandatory Singleton template

Every controller must follow this exact structure:

```java
public class XxxController {

    private static XxxController instance;
    private HashMap<UUID, Xxx> entities;

    private XxxController() {
        this.entities = new HashMap<>();
    }

    public static XxxController getInstance() {
        if (instance == null) {
            instance = new XxxController();
        }
        return instance;
    }

    // CRUD and business methods below
}
```

The three elements that identify the Singleton in the class diagram and in code:
1. `private static XxxController instance;`
2. `private XxxController()`
3. `public static XxxController getInstance()`

## Cross-controller communication

When a controller needs data from another controller, it calls `OtherController.getInstance()` inline within the method, never as a stored attribute. Example:

```java
public float calculateOutstandingDebt(UUID supplierId) {
    var vouchers = VoucherController.getInstance().findUnpaidBySupplier(supplierId);
    var payments = PaymentOrderController.getInstance().findBySupplier(supplierId);
    // ...
}
```

## Project structure

```
src/
├── Main.java
├── views/
│   ├── MainFrame.java
│   └── (one frame per module)
├── controllers/
│   └── (one controller per aggregate root)
└── models/
    ├── (entities)
    └── enums/
        └── (enums)
```