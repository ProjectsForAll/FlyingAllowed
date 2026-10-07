# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A Bukkit/Spigot/Folia plugin that automatically toggles a player's flight on and
off as they move, based on where they are: which world, whose land claim, and
which WorldGuard region. It reads the state of many third-party claim plugins
rather than owning claims itself.

Built on **BukkitOfUtils (BOU)** — see the `bou` skill, and BOU's source at
`C:\Users\nitra\OneDrive\Development\Java\Plugins\BukkitOfUtils`. BOU is a
separate server plugin (a hard `depend` in `plugin.yml`), consumed `compileOnly`
and never shaded.

## Build

```powershell
.\gradlew.bat build          # full build; shaded jar -> target\libs\FlyingAllowed-<version>.jar
.\gradlew.bat shadowJar      # jar only
```

There is no unit-test suite — `test` is always NO-SOURCE. The only automated
check is `testFlightState` (part of `check`, so `build` runs it): a plain
`main()` under `src/regressionTest` that drives `PlayerMoveData` with
`Proxy`-faked `Player`/`World` objects. The fakes throw on any method they don't
stub, and plugin config is never initialized, so a code path it exercises must
not touch config or unstubbed Bukkit calls. It covers the cache/auto-enable
logic only; verify everything else by building and, when behavior matters, by
loading the jar on a server.

`layout.buildDirectory` is redirected to `target/` (not Gradle's default
`build/`), and `target/` is gitignored.

### Toolchain constraints

The build is sensitive to versions in a way that is easy to break:

- **Java 11 source/target**, but the build itself runs on **JDK 25**. Lombok is
  pinned to a version new enough for that JDK (`lombok { version = ... }` in
  `build.gradle`); the freefair plugin's bundled default lags current JDKs and
  fails with `ExceptionInInitializerError` / `NoSuchFieldException: TypeTag ::
  UNKNOWN`. Bump the pin when the JDK moves.
- Gradle 9.x + `com.gradleup.shadow` (the `com.github.johnrengelman.shadow`
  plugin is dead and will not run on Gradle 9).
- `java.disableAutoTargetJvm()` is required: several soft-depend plugin APIs
  ship higher bytecode than this plugin's Java 11 target, and resolution rejects
  them without it even though they are compile-only.

Write Java 11 — no records, no arrow-`switch` expressions.

The build uses `sourceCompatibility`/`targetCompatibility` rather than
`options.release`, so there is **no bootclasspath restriction**: code compiled on
JDK 25 can call an API that did not exist in Java 11, emit valid major-55
bytecode, and then `NoSuchMethodError` at runtime on a real server. The compiler
will not catch this for you. After a toolchain change, confirm the target
survived:

```bash
unzip -p target/libs/FlyingAllowed-<version>.jar host/plas/flyingallowed/FlyingAllowed.class | od -An -tu1 -j6 -N2
# 0 55 = Java 11 (correct); 69 would be Java 25 and will not load
```

### Dependencies

Version lists live in **`dependencies.gradle`**, not `build.gradle`, grouped as
`ANNO` / `COMP_ONLY` / `IMPL` / `SHADOW` / `FILES` / `OTHER_PLUGINS`. Every
claim-plugin API is `compileOnly` in `OTHER_PLUGINS` — nothing is shaded, so
`minimize()` has nothing to strip.

An API with no reachable Maven artifact (currently FabledSkyBlock) is instead
hand-stubbed under `src/stubs/java` — a separate `stubs` source set on the main
`compileOnly` classpath, never packaged. Stubs must mirror the real signatures
exactly; see `src/stubs/README.md`.

`plugin.yml` is templated: `name`, `version`, and `main` are expanded from
`gradle.properties` at `processResources` time. Bump the version there, not in
the YAML. `plugin.main = default` derives the main class as
`<group>.<name-lowercased>.<name>`.

## Architecture

### The decision flow

Everything hangs off player movement. `FlightWorlds` (a `Listener`) handles
`PlayerMoveEvent`, `PlayerTeleportEvent`, and `PlayerPortalEvent`, wraps each
into a `PlayerMoveData`, and calls `checkPermission()` on it. `PlayerMoveData`
is the real decision engine — it holds both the from/to locations and all the
policy logic.

Order of checks in `PlayerMoveData.checkPermission()`:

1. **World gate** — `isWorldDisabled()` against `WorldConfig`, which is a
   list plus a `whitelist` flag (so the list means either "disabled here" or
   "only enabled here"), plus a `full-disable` flag that force-lands the player
   instead of merely skipping.
2. **Cache** — if `MoveDataCache` already has a verdict for this player, stop.
3. **Gamemode** — creative and spectator are left alone.
4. **Claim/region query** — `CompatManager.getFlyingAllowed(this)` fans out to
   every enabled hook.
5. **Apply** — `checkFlyAndIsHandled(...)` toggles flight and sends the
   matching message; falling through to a plain per-world permission check
   (`flyingallowed.allow.<world>`).

### The two-enum result

A hook does not return a boolean. It returns a `SingleSet<FlightAbility,
FlightExtent>`: **what** the answer is and **who** said so.

- `FlightAbility` — `ABLE_TO_FLY`, `UNABLE_TO_FLY`, `NO_CLAIM` (in the wild,
  outside any claim), plus `NO_API` / `ISSUE` / `NONE` for hook failures.
- `FlightExtent` — which plugin produced it (`LANDS`, `WORLDGUARD`, …). It
  exists mainly so the right message is sent: `WORLDGUARD` gets the
  "region" wording, everything else gets the "claim" wording.

When several hooks answer, `CompatManager.checkAndSet` merges them with a fixed
precedence: **`ABLE_TO_FLY` > `UNABLE_TO_FLY` > `NO_CLAIM`**. Any one plugin
permitting flight wins over another forbidding it.

### Adding a claim-plugin hook

Each integration is a trio under `compat/plugins/<name>/`, and all three are
required:

- `<X>Holder extends FlyingHolder<TheirApiType>` — the real work. Its
  constructor passes an identifier, a `Function<Void, Api>` supplier, and the
  `FlightExtent`; it implements `isFlyableAtLocation(PlayerMoveData)`.
- `<X>HolderCreator implements Supplier<ApiHolder<?>>` — returns a new holder.
- `<X>Held extends HeldHolder` — pairs a `public static String IDENTIFIER`
  with the creator.

Then register it in the **`HookedPlugins` enum** and add a `softdepend` entry in
`plugin.yml`. `CompatManager.forHolder` constructs each one inside a
`try`/`catch (Throwable)`, so a missing plugin degrades to an `EmptyHolder`
rather than failing startup — this is why hooks can reference classes that may
not be on the server.

Guard every holder with `if (! isEnabled()) return FlightAbility.NO_API;` first,
and return `NO_CLAIM` (not `UNABLE_TO_FLY`) when the player is simply outside
any claim — the merge treats those very differently.

**WorldGuard is the exception to the pattern.** It registers a custom
`StateFlag` (`flyingallowed-allow-flight`), and WorldGuard requires flag
registration during *load*, not enable — hence `onBaseLoad()` in
`FlyingAllowed` calls `WGHolder.registerFlightFlag()` directly, before the
normal hook init. The `FlagConflictException` path handles `/reload`, where the
flag already exists from the previous load.

### Caching

`MoveDataCache` keeps two maps: `cache` (flushed on a `CacheTimer` every
`lookupCachedTicks`) and `cacheLong` (kept until disable). Since
`checkPermission()` returns early on a cache hit, **the cache period is the
latency of any claim change taking effect** — a stale entry means a player keeps
or loses flight until the flush. Note `cache(...)` no-ops if a key is already
present, so entries are not overwritten mid-period.

### Config

Three `SimpleConfiguration` subclasses (`MainConfig`, `MessageConfig`,
`WorldConfig`) held as statics on `FlyingAllowed`. The pattern is a getter per
key calling `reloadResource()` then `getOrSetDefault(path, default)` — so the
default lives in Java and the YAML file is self-healing, and `init()` just calls
every getter once to write out a complete file. Add a setting by adding a getter
and calling it from `init()`; the `.yml` in `resources/` does not need the key.

## Conventions

- Folia is a declared target (`folia-supported: true`). Schedule through BOU's
  `TaskManager` / `BaseRunnable`, never `Bukkit.getScheduler()`, and touch an
  entity only on its own region's thread.
- Extend `BetterPlugin` lifecycle hooks — `onBaseLoad` / `onBaseEnabled` /
  `onBaseDisable` — not `onEnable`/`onDisable`.
- Lombok `@Getter @Setter` on the class is used heavily, including on
  static-only holder classes.
- Messages go through BOU's `Sender`, not `player.sendMessage`.
- All four commands extend BOU's `SimplifiedCommand` (not `ComplexCommand`);
  they are constructed in `onBaseEnabled` and declared in `plugin.yml`.
- Permissions: `flyingallowed.allow.<world>` per world,
  `flyingallowed.command.<cmd>` per command, with claim/region and bypass
  permissions configurable in `config.yml` rather than hardcoded.
  `flyingallowed.in.lands` is a legacy alias still honored alongside the
  configured claim permission.
