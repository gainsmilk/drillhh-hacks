---
paths: ["src/main/java/**/*.java"]
---

# Java Conventions

- 4-space indent, LF endings, UTF-8
- Import order: java.*, javax.*, external libs, net.minecraft.*, org.spongepowered.*, local (blank line separators)
- Classes/methods with non-obvious behavior get javadoc headers. Single-sentence headers preferred, expand only when needed.
- Section separators inside longer classes: `// --- Section Name ---` with proper capitalization. Use when a file has multiple logical groupings (e.g. fields / init / drawing / input / helpers).
- No `TODO` or `FIXME` committed - open an issue or fix it
- No `System.out.println` outside of `KeepSprintMod.init()` - noisy on heavy tick rates
- Null-guard every `KeepSprintMod.CONFIG` access in mixins - config is nil until init returns
- Mixins: `@Inject` preferred over `@Overwrite`. Use `@At("HEAD")` or `@At("TAIL")` with a clear reason.
- MCP names only in mixin targets (`onLivingUpdate`, `runTick`, `getBlockReachDistance`) - Weave remaps at load
- Config fields public for direct access, readers null-check on load (see `Config.load()`)
- Long dashes "—" banned. Use "-" or commas.
