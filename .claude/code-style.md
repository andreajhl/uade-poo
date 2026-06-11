# Code Style

## Language

- **Code (identifiers, classes, methods, variables)**: English.
- **User-facing strings (Swing labels, error messages, menus)**: Spanish.
- **Enums**: English identifiers, English values.

## Naming

| Element | Convention | Example |
|---|---|---|
| Class | PascalCase | `SupplierController` |
| Method | camelCase | `findByCuit` |
| Variable | camelCase | `currentUser` |
| Constant | UPPER_SNAKE_CASE | `MAX_CREDIT_LIMIT` |
| Package | lowercase | `controllers` |
| Enum type | PascalCase | `IVACondition` |
| Enum value | UPPER_SNAKE_CASE | `RESPONSABLE_INSCRIPTO` |
| Frame | `XxxFrame` | `SupplierFrame` |
| Dialog | `XxxDialog` | `CreateSupplierDialog` |

## Comments policy

- **No code comments.** Names and structure must convey intent.
- Allowed exceptions: legal headers if explicitly required by the assignment.
- Javadoc only on public controller methods when the intent cannot be expressed by the signature alone, and only when explicitly requested.

## Formatting rules

- 4 spaces for indentation, no tabs.
- Opening brace on the same line.
- One blank line between methods.
- No blank lines inside method bodies unless separating logical blocks.
- Imports grouped: `java.*` first, blank line, then `javax.*`, blank line, then project imports.
- Maximum line length: 120 characters.
- One class per file, file name matches class name.

## Data structures

| Need | Use | Do not use |
|---|---|---|
| Entity storage with ID lookup | `HashMap<UUID, Entity>` | `ArrayList<Entity>` |
| Ordered iteration list | `ArrayList<T>` | `LinkedList`, `Stack`, `Queue` |
| Set without duplicates | `HashSet<T>` | manual checks on lists |
| Fixed configuration | Constructor initialization | external files |

Use the simplest structure that solves the problem. Avoid `Stack`, `Queue`, `LinkedList`, `TreeMap`, complex Streams, generics beyond parametric collections, reflection.

## Loops

Prefer classic `for` and `for-each` loops over Streams API. Streams allowed only when they shorten the code without adding complexity (e.g., a single `map` + `collect`).

## Visibility

- Fields: `private` always.
- Methods: `public` for API/CRUD, `private` for helpers.

## IDs

- `java.util.UUID` for entity identifiers unless the diagram dictates otherwise (e.g., `int` ID for human-readable records).
- Generate with `UUID.randomUUID()` on entity construction.

## Errors

- Custom exceptions per business rule (`CreditLimitExceededException`, `EntityNotFoundException`, `InvalidPaymentException`).
- Controllers throw, Views catch and display with `JOptionPane`.

## Swing conventions

- Use `BorderLayout`, `BoxLayout`, `GridBagLayout`. Avoid null layouts.
- One `JFrame` per module, `JDialog` for forms.
- Tables with `JTable` + `DefaultTableModel`.
- Validate input in the View before sending to Controller.
- `JOptionPane` for confirmations and messages.