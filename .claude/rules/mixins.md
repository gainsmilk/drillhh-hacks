---
paths: ["src/main/java/dev/alxx/keepsprint/mixins/**/*.java"]
---

# Mixin Conventions

- One mixin per module. File name matches target class: `EntityPlayerSPMixin`, `FastPlaceMixin` etc.
- Register every new mixin in `src/main/resources/keepsprint.mixins.json`
- Compatibility level stays `JAVA_8` - do not raise without testing weave reloader
- Always null-guard `KeepSprintMod.CONFIG` first (early return if null)
- Early-return on `!enabled` flag next, before any game state access
- Use `@Shadow` for field access on target class, do not cast through reflection
- `@Inject` at `HEAD` for pre-logic reads, `TAIL` for post-physics mutations
- Do NOT call `setSprinting`, `setPosition`, or other state mutations inside a `HEAD` inject that vanilla will overwrite later in the same method - use `TAIL` or re-assert every tick
- Every mixin has a javadoc header: what it does, when it triggers, which config flag gates it
- When adding a new module: update Config (field + load + save), add mixin, register in json, add GUI card + slider, update CHANGELOG
