# keep-sprint-mod

Minecraft 1.8.9 Weave Loader mod. Five client-side movement/interaction modules behind a custom in-game GUI: KeepSprint, OmniSprint, FastPlace, BlockReach, Timer.

This file is the canonical project rules for BOTH Claude Code and OpenAI Codex CLI. Read it before making any changes.

## Critical Rules

- ALWAYS keep `dev.alxx.keepsprint` package layout. Never rename packages or the entrypoint class (`KeepSprintMod`).
- ALWAYS gate mixin logic on `KeepSprintMod.CONFIG != null` and the per-feature enable flag. Mixins fire before init in edge cases.
- ALWAYS preserve back-compat config keys (`tellyMomentumEnabled`, `tellyMomentumMultiplier`) in `Config.load()`.
- ALWAYS run `make build` before any commit.
- NEVER add co-author tags to commits.
- NEVER commit without explicit ask.
- NEVER commit directly to main - use feature branches (`feat/<name>`, `fix/<name>`).

## Architecture

Single-module Gradle project. Java 8 toolchain (Minecraft 1.8.9 constraint). Weave Loader 1.1.0 with MCP mappings.

- Entrypoint: `dev.alxx.keepsprint.KeepSprintMod` implements `ModInitializer`. Loads `Config`, subscribes `KeybindHandler` to Weave's `EventBus`
- Config: `dev.alxx.keepsprint.config.Config` - properties file at `~/.weave/mods/keepsprint-config.properties`. Auto-creates with defaults on first run
- Keybind: `KeybindHandler` listens for LWJGL keycode 205 (RIGHT ARROW) and opens `TrenboloneGui` when no other screen is open
- GUI: `dev.alxx.keepsprint.gui.TrenboloneGui` extends `GuiScreen`. Working copy of config, committed on Save. Three sliders (omni multiplier, reach distance, timer multiplier), five toggle cards
- Mixins (in `dev.alxx.keepsprint.mixins`, registered via `keepsprint.mixins.json`):
  - `EntityPlayerSPMixin.keepSprint` - HEAD inject on `onLivingUpdate`, force `setSprinting(true)` while moving + fed
  - `OmniSprintMixin.applyOmniSprint` - TAIL inject, multiplies `motionX/Z` on pure strafe or pure backwards. Heavily gated (onGround, not water/lava/ladder/sneak/falling, fed)
  - `FastPlaceMixin.fastPlace` - HEAD inject on `Minecraft.runTick`, zeros `rightClickDelayTimer`
  - `BlockReachMixin.customReach` - HEAD inject cancellable on `PlayerControllerMP.getBlockReachDistance`, returns config value
  - `TimerMixin.applyTimer` - HEAD inject on `runTick`, writes `Minecraft.timer.timerSpeed` (vanilla 1.0)

## Stack Decisions (Locked)

- **Java 8** toolchain - Minecraft 1.8.9 ABI requires it. Do not bump.
- **Weave Loader 1.1.0** + `net.weavemc.api` for events and mod lifecycle
- **SpongePowered Mixin 0.8.5** with `compatibilityLevel: JAVA_8` in `keepsprint.mixins.json`
- **MCP mappings** via `weave { configure { mcpMappings() } }` - all `net.minecraft.*` references use MCP names
- **Properties file** for config (no JSON deps, no GSON, no extras) - keeps the jar tiny

## Java Conventions

- 4-space indent, LF line endings, UTF-8
- Javadoc-style class headers: one-sentence purpose, then context/gotchas
- Mixin methods are `private`, return `void`, take `CallbackInfo`/`CallbackInfoReturnable<T>`
- Mixin classes are `abstract`, use `(EntityPlayerSP)(Object) this` cast pattern
- `@Shadow` fields stay public to mirror MCP visibility - don't redeclare
- Constants UPPER_SNAKE_CASE, fields lowerCamelCase
- Logging via `System.out.println` / `System.err.println` with `[TrenboloneBridgonate]` prefix - no logger framework
- No external runtime deps beyond Weave + Mixin (compileOnly)

## Implementation Pitfalls

- Mixin `compatibilityLevel` MUST stay `JAVA_8`. Higher values silently break under MC 1.8.9
- `keepsprint.mixins.json` `package` field MUST match the actual mixins package - typos fail at runtime with confusing classloader errors
- `Minecraft.timer` is `@Shadow public` - shadowing it as private breaks the inject silently
- `EntityPlayerSP.movementInput` can be null during world transitions - always nil-check
- `Config.load()` reads back-compat keys (`tellyMomentumEnabled` etc) when new keys are absent. Don't drop the fallback path
- Slider clamps in `TrenboloneGui` are stepped (0.01 omni/timer, 0.1 reach) - keep `clampOmni`/`clampReach`/`clampTimer` in sync with config bounds
- GUI keybind is hardcoded to LWJGL keycode 205 (RIGHT ARROW). If you change it, update `KeybindHandler.KEY_RIGHT_ARROW` and the README
- `cir.setReturnValue` on `BlockReachMixin` cancels the original method - never combine with non-cancellable injects
- Building with a different Java toolchain than 8 will compile but produce a jar that fails to load under MC 1.8.9's classloader

## Commands

```
make build     # gradle build (output: build/libs/trenbolone-bridgonate-<version>.jar)
make deploy    # build + copy jar to ~/.weave/mods/
make install   # alias for deploy
make clean     # gradle clean
make help      # list targets
```

## Branch Discipline

- Feature branches: `feat/<name>`
- Bug fixes: `fix/<name>`
- Work on branch, merge to main after local verification
- Never force push to main
- Tag releases after merge: `git tag v0.X.Y && git push origin v0.X.Y`

## Commit Style

Conventional commits: `feat|fix|refactor|docs|infra|test|chore(scope): description`

- Subject line imperative mood, under 72 chars
- Body explains the "why" for non-trivial changes
- Scopes mirror modules: `keepsprint`, `omnisprint`, `fastplace`, `blockreach`, `timer`, `gui`, `config`, `build`, `test`
- NEVER add co-author tags (`Co-Authored-By:`) to any commit

## Compact Instructions

Always preserve: current task, file paths being edited, mixin compat level, MCP method names being targeted, config back-compat keys, active branch name.

## Do NOT

- Use long dashes - use "-" or commas
- Add AI slop ("furthermore", "it's worth noting", "comprehensive")
- Bump Java toolchain past 8 (MC 1.8.9 won't load)
- Bump mixin `compatibilityLevel` past `JAVA_8`
- Add runtime dependencies beyond what's in `build.gradle.kts`
- Commit `build/`, `.gradle/`, `.idea/`, or any built jars
- Switch from MCP to Yarn/Mojang mappings - the codebase is fully MCP-named
- Skip the `KeepSprintMod.CONFIG == null` guard in mixins
