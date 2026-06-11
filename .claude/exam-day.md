# Exam Day Procedure

This document defines the procedure when the user provides the class diagram and sequence diagrams at the start of an exam session.

## Step 1 — Receive diagrams

The user will provide:
- A **class diagram** (image, PDF, or Mermaid code).
- One or more **sequence diagrams** (same formats).

Ask for both if only one is provided.

## Step 2 — Analyze and extract context

Read the diagrams carefully and produce a structured analysis containing:

### A) Inventory of classes
- List all classes grouped by layer (View, Controller, Model, Enum, DTO).
- For each controller, list its attributes (especially storage collections) and methods.
- For each model, list its attributes and own methods.
- Identify inheritance hierarchies.
- Identify Singleton controllers (look for `instance`, `getInstance`, private constructor).

### B) Relationships
- Composition (which controller owns which entities).
- Associations (which controller uses which other controllers).
- Multiplicities relevant to behavior (1..*, 0..*, 0..1).

### C) Use cases from sequence diagrams
- For each sequence diagram, describe the use case in one paragraph.
- List the participating classes and methods in order.
- Identify which controller orchestrates the use case.

### D) Functional requirements
- Extract the list of features that need to be implemented based on the diagrams.

## Step 3 — Present the analysis

Show the analysis to the user in a structured format. **Stop and wait for confirmation** that the analysis is correct before proceeding.

## Step 4 — Propose feature order

Based on the analysis, propose an implementation order that respects dependencies. Example:

```
Suggested implementation order:
1. Enums and base models
2. User and authentication module
3. Suppliers module
4. Products module
5. Vouchers module
6. Purchase orders module
7. Payment orders module
8. Reports module
9. MainFrame integration
```

Wait for user approval or adjustment.

## Step 5 — Proceed with workflow

Once the feature order is approved, follow `workflow.md` strictly for each feature: plan → approval → item by item → commit.

## Reminders

- All rules from `architecture.md` and `code-style.md` apply.
- Singleton template is mandatory for every controller in the diagram.
- If the diagram contradicts the project rules (e.g., proposes a non-Singleton controller), flag it and ask the user how to resolve before implementing.