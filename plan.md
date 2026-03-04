# Kotlin 2.3.10 + AGP 9+ Migration Plan

## 1. Goal
Migrate this project to AGP `9+` and Kotlin `2.3.10` with minimum disruption, keeping module boundaries (`app`, `core`, `feature/plugins`) intact and moving to AGP 9-compatible Gradle DSL.

## 2. Current Structure Snapshot
- Application module: `app`
- Core library modules: `core/common`, `core/resources`
- Feature library modules: `feature/editor`, `feature/monaco-editor`, `feature/preferences`, `feature/plugins`
- Central config files:
  - `settings.gradle.kts`
  - `build.gradle.kts` (root)
  - `gradle/libs.versions.toml`

## 3. Migration Principles
- Do migration in small reversible commits.
- Upgrade build tooling first, then module plugins/DSL, then code-level compile errors.
- Remove legacy AGP APIs (`BaseExtension`, old Kotlin Android plugin usage patterns).
- Keep `app` and libraries using consistent JVM/SDK/desugaring setup from one shared convention.

## 4. Phase Plan

### Phase 0: Safety + Baseline
- Create branch: `migration/agp9-kotlin-2.3.10`.
- Record current state:
  - `./gradlew :app:dependencies --configuration debugCompileClasspath`
  - `./gradlew :app:dependencyInsight --configuration debugCompileClasspath --dependency org.jetbrains.kotlin:kotlin-stdlib`
- Freeze non-migration feature work until toolchain is stable.

### Phase 1: Version Catalog + Wrapper
Files:
- `gradle/libs.versions.toml`
- `gradle/wrapper/gradle-wrapper.properties`

Tasks:
- Set versions:
  - AGP plugin IDs to `9.x` (target exact patch you choose).
  - Kotlin version to `2.3.10`.
- Upgrade Gradle wrapper to version required by selected AGP 9 patch.
- Keep all library dependency versions unchanged unless forced by AGP/Kotlin incompatibility.

Exit criteria:
- `./gradlew --version` matches required Gradle.
- Settings sync runs without plugin resolution failure.

### Phase 2: Root Build Logic Modernization
File:
- `build.gradle.kts`

Tasks:
- Remove deprecated import and API usage:
  - delete `com.android.build.gradle.BaseExtension`
  - stop using `extensions.findByType(BaseExtension::class)`
- Replace with AGP new DSL-compatible approach:
  - configure Android modules via `com.android.build.api.dsl.CommonExtension<*, *, *, *, *, *>` pattern.
- Keep shared values centralized:
  - `compileSdk`, `minSdk`, `targetSdk`
  - Java compatibility (`17`) and Kotlin JVM target (`17`)
- Revisit global `resolutionStrategy`:
  - avoid hard forcing Kotlin stdlib unless necessary.
  - if kept, force to `2.3.10` and document reason.
- Align compiler options to Kotlin `2.3` (`apiVersion`/`languageVersion` strategy).

Exit criteria:
- No `BaseExtension` deprecation warnings in configuration phase.

### Phase 3: Plugin Management for AGP 9
Files:
- `settings.gradle.kts`
- `build.gradle.kts` (plugins block)
- module `build.gradle.kts` files

Tasks:
- Keep Android plugins via version catalog aliases.
- Remove `org.jetbrains.kotlin.android` from Android modules (AGP 9 has built-in Kotlin support).
- Keep only Kotlin plugins still needed explicitly, module-specific:
  - `org.jetbrains.kotlin.plugin.compose` (where Compose is used)
  - `org.jetbrains.kotlin.plugin.serialization` (where serialization is used)
  - `kotlin-parcelize` (if Parcelize annotations are used)
- Keep Navigation Safe Args plugin only in `app` if you still use generated directions.

Exit criteria:
- No error: "The 'org.jetbrains.kotlin.android' plugin is no longer required..."
- No error: plugin already on classpath with unknown version.

### Phase 4: Module-by-Module Gradle Refactor

#### 4.1 `app/build.gradle.kts` (application)
Tasks:
- Keep plugin set minimal and AGP9-safe.
- Validate Safe Args plugin order and compatibility with AGP 9.
- Ensure Android block has explicit:
  - `namespace`, `compileSdk`, `defaultConfig.minSdk/targetSdk`
  - `compileOptions` with desugaring + Java 17
- If desugaring stays enabled, ensure `multiDexEnabled = true` and dependency if needed.
- Keep Compose config only if used (`buildFeatures.compose = true`, compose plugin).

#### 4.2 `core/common/build.gradle.kts`, `core/resources/build.gradle.kts` (library)
Tasks:
- Replace `id("kotlin-android")` with AGP9-safe plugin set (no kotlin-android).
- Keep `com.android.library`.
- Ensure namespace + sdk values are inherited or explicitly set consistently.
- Keep build features only where needed.

#### 4.3 `feature/editor`, `feature/monaco-editor`, `feature/preferences`, `feature/plugins` (library)
Tasks:
- Same plugin migration as core modules.
- Apply compose plugin only for modules with Compose APIs (`feature/editor`, `feature/plugins` if Compose UI is present).
- Verify custom tasks still work (e.g., `copyJarToAssets` in `feature/plugins`).
- Ensure no module relies on removed synthetic extensions.

Exit criteria:
- `./gradlew help` and `./gradlew tasks` pass configuration for all modules.

### Phase 5: Dependency Graph Cleanup
Tasks:
- Remove legacy Kotlin artifacts causing duplicates:
  - `kotlin-android-extensions-runtime` must not be present.
- Confirm single Kotlin stdlib version (`2.3.10`) across classpath:
  - use `dependencyInsight` for `kotlin-stdlib`, `kotlin-parcelize-runtime`.
- Keep excludes only where truly required (avoid broad excludes masking real issues).

Exit criteria:
- No duplicate-class errors for parcelize/android-extensions classes.
- No metadata mismatch (`expected 2.1.0 / found 2.3.0`) style errors.

### Phase 6: Kotlin/Compose API Compatibility Fixes
Scope:
- Compile errors from API signature changes (Compose, Material, editor libs, etc.).

Tasks:
- Fix callsites that changed signatures.
- Replace removed/renamed APIs.
- Resolve imports broken by transitive dependency shifts.

Exit criteria:
- `:app:compileDebugKotlin` and Java compile tasks pass.

### Phase 7: Optional Safe Args Removal Path
If you decide to remove Safe Args entirely:
- Remove plugin `androidx.navigation.safeargs.kotlin` from `app`.
- Replace generated Directions/NavArgs usage with:
  - route-based navigation args
  - typed wrappers/manual argument mapping
- Remove Safe Args classpath from root `buildscript` once no longer used.

Exit criteria:
- No unresolved `*Directions`/`*Args` references.

### Phase 8: Stabilization + Documentation
Tasks:
- Add a migration note in repo docs:
  - final toolchain versions
  - removed plugins/APIs
  - module conventions for app/library setup
- Add CI checks for:
  - dependency insight for Kotlin stdlib
  - configuration-only Gradle check

## 5. File-Level Change Checklist

### Root
- `gradle/libs.versions.toml`
- `settings.gradle.kts`
- `build.gradle.kts`
- `gradle/wrapper/gradle-wrapper.properties`

### App Module
- `app/build.gradle.kts`

### Core Modules
- `core/common/build.gradle.kts`
- `core/resources/build.gradle.kts`

### Feature/Plugin Library Modules
- `feature/editor/build.gradle.kts`
- `feature/monaco-editor/build.gradle.kts`
- `feature/preferences/build.gradle.kts`
- `feature/plugins/build.gradle.kts`

## 6. Recommended Commit Sequence
1. `chore(build): bump wrapper + AGP/Kotlin versions`
2. `refactor(build): migrate root gradle DSL to AGP9 CommonExtension`
3. `refactor(build): remove kotlin-android plugin from android modules`
4. `fix(build): align Kotlin stdlib/parcelize dependency graph`
5. `fix(app): desugaring + multidex + safe args compatibility`
6. `fix(compile): Kotlin/Compose API adjustments`
7. `docs(build): AGP9/Kotlin2.3 migration notes`

## 7. Risk Register
- AGP 9 + toolchain mismatch with local JDK/Gradle on Android PE.
- Safe Args plugin compatibility edge cases with Kotlin plugin changes.
- Transitive dependency pulling older/newer Kotlin modules.
- Compose API breakages due to compiler/runtime alignment.

## 8. Fast Validation Commands (After Each Phase)
- `./gradlew --version`
- `./gradlew help`
- `./gradlew :app:dependencyInsight --configuration debugCompileClasspath --dependency org.jetbrains.kotlin:kotlin-stdlib`
- `./gradlew :feature:plugins:compileDebugJavaWithJavac`
- `./gradlew :app:compileDebugKotlin`

---
This is a planning document only; no migration changes are applied by this file itself.
