# keep-sprint-mod

**the tren of bridging, compiled for personal edification.**

minecraft 1.8.9 weave mod. five client-side modules behind one in-game GUI. singleplayer testing, local research, vibes.

## what it is

small java mod that reimplements a handful of behaviors from a well-known paid client whose jar we decompiled purely for reference. nothing novel. just the classic movement/interaction set, gated behind toggles, with a GUI that isn't hideous.

## features

- **KeepSprint** - forces sprint on while you're moving + fed. no more dropping sprint every time you strafe. default on.
- **OmniSprint** - small velocity bump on pure strafe or pure backwards to claw back what vanilla steals from you. `1.00x-1.15x`. default off.
- **FastPlace** - zeroes `rightClickDelayTimer` so holding RMB places one block per tick. god-tier for bridging practice.
- **BlockReach** - pushes client-side interaction distance past vanilla's 4.5. clamped `4.5-7.0`. past ~6 most anticheats will have opinions.
- **Timer** - straight multiplier on `Minecraft.timer.timerSpeed`. `0.50x-1.50x`. vanilla is 1.0.
- **GUI** - press RIGHT_ARROW in game. dark panel, toggle cards, sliders. save commits to disk, cancel doesn't.

## install

1. get [weave loader](https://github.com/Weave-MC/Weave-Loader) via [weave manager](https://github.com/Weave-MC/Weave-Manager)
2. `make deploy` (builds + yeets the jar into `~/.weave/mods/`)
3. launch lunar or vanilla 1.8.9 through weave manager

or manually:

```bash
./gradlew build
cp build/libs/trenbolone-bridgonate-*.jar ~/.weave/mods/
```

## config

lives at `~/.weave/mods/keepsprint-config.properties`. auto-created on first run. the GUI is the canonical editor, the file is just what it writes out.

```
keepSprintEnabled=true
omniSprintEnabled=false
omniSprintMultiplier=1.0
fastPlaceEnabled=false
blockReachEnabled=false
blockReachDistance=6.0
timerEnabled=false
timerMultiplier=1.0
```

legacy `tellyMomentumEnabled` / `tellyMomentumMultiplier` keys still read if the new ones aren't there. back-compat preserved for anyone who had the old build.

## keybind

- `RIGHT_ARROW` (LWJGL 205) - opens the GUI. that's it. one keybind.

## build

needs a JDK 8+ on PATH. gradle wrapper handles everything else.

```
make build     # jar -> build/libs/
make deploy    # build + copy to ~/.weave/mods/
make clean     # gradle clean
make help      # list targets
```

output: `build/libs/trenbolone-bridgonate-0.1.0.jar`.

## target

- minecraft 1.8.9, MCP mappings
- weave loader 1.1.0
- mixin 0.8.5, compatibility level `JAVA_8`
- lunar client (via weave manager) or plain 1.8.9

## notes

java 8 is load-bearing. do not bump the toolchain, 1.8.9's classloader will not hear it. same energy for `compatibilityLevel: JAVA_8` in the mixin config.

reference implementation was a decompiled paid client jar. what's here is a reimplementation from the observed behaviors, not copy-pasted bytecode.

## license

[MIT](LICENSE)
