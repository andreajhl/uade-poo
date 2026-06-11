# Development Workflow

Every feature follows this strict cycle. Do not skip steps.

## Step 1 — Receive requirement

The user passes a feature requirement, for example:
> "Implement the suppliers module."

## Step 2 — Generate plan

Present a numbered plan with small, concrete items. Each item must be implementable and verifiable on its own. Example format:

```
PLAN: Implement suppliers module

[ ] 1. Create enum IVACondition
[ ] 2. Create Category model
[ ] 3. Create CertificationRetention model
[ ] 4. Create Supplier model with constructor, getters/setters, own methods
[ ] 5. Create SupplierController with Singleton skeleton
[ ] 6. Implement basic CRUD in SupplierController
[ ] 7. Implement category management in SupplierController
[ ] 8. Implement certification management in SupplierController
[ ] 9. Create SupplierFrame with JTable listing
[ ] 10. Create CreateSupplierDialog
[ ] 11. Wire form to controller
[ ] 12. Implement edit from table
[ ] 13. Implement delete with confirmation
[ ] 14. Wire into MainFrame
[ ] 15. Manual smoke test
```

**Stop here.** Wait for user approval of the plan before continuing.

## Step 3 — Iterate item by item

For each approved item:

1. Implement **only that item**.
2. Show the full code of the file(s) changed.
3. Briefly explain what was done in 1–2 lines.
4. **Stop and wait** for user feedback.
5. If changes are requested, apply them and show again.
6. When the user says "ok" / "next" / "siguiente", mark the item as `[x]` and move to the next one.

**Never skip ahead.** Never implement multiple items in one response unless the user explicitly requests it.

## Step 4 — Commits

After each completed item (when the user approves), suggest a commit message in conventional format:

```
feat(suppliers): add Supplier model with constructor and getters
feat(suppliers): implement SupplierController singleton with basic CRUD
fix(suppliers): correct CUIT validation regex
refactor(suppliers): extract category management to private method
```

Types: `feat`, `fix`, `refactor`, `style`, `docs`, `chore`.

Scope: lowercase module name.

The user decides when to actually run the commit. Do not run `git commit` automatically.

## Step 5 — Feature closure

When all items are completed, present a brief summary:
- Files created/modified.
- Public API exposed by the new controllers.
- Pending items if any.

Then wait for the next requirement.

## Hard rules

1. **Never generate code without an approved plan first.**
2. **Never skip items in the plan.**
3. **Never group items into a single response unless explicitly requested.**
4. **Never introduce libraries, frameworks, or break the rules in `architecture.md` or `code-style.md`.**
5. **If a requirement conflicts with the rules, flag it and propose an alternative before implementing.**