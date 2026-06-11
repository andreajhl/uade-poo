# Project Context

This is a Java + Swing academic project. All implementation must strictly follow the architecture, conventions, and workflow defined in the documents below. Read them in order before generating any code or plan.

## Required reading (in order)

1. **`.claude/architecture.md`** — MVC architecture, Singleton pattern for controllers, layer responsibilities.
2. **`.claude/code-style.md`** — Naming conventions, formatting rules, language, comments policy, data structures.
3. **`.claude/workflow.md`** — How to receive requirements, create plans, iterate item-by-item, and commit.
4. **`.claude/exam-day.md`** — Procedure when the user provides class and sequence diagrams.

## Hard constraints (non-negotiable)

- **Language**: Java 17+
- **UI**: Swing only (`javax.swing`)
- **No external libraries** — only JDK standard.
- **No frameworks** (no Spring, no Hibernate, no Maven, no Gradle).
- **No persistence** — data lives in memory inside controllers.
- **No tests** — manual testing only.
- **Code in English** — class names, methods, variables, enums. User-facing messages in Spanish.
- **No comments in code** — code must be self-explanatory through naming.

## First message protocol

When the user starts a new session:
1. Confirm you have read all four documents in `.claude/`.
2. Wait for the user to either:
   - Provide diagrams (class diagram, sequence diagrams) → follow `exam-day.md`.
   - Pass a specific feature requirement → follow `workflow.md`.
3. **Do not generate any code or plan without explicit instruction.**