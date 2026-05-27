# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Full debug build
./gradlew assembleDebug

# Build a single module
./gradlew :feature:consultation:compileDebugKotlin

# Clean and rebuild
./gradlew clean assembleDebug

# Run all tests
./gradlew test

# Run a single test class
./gradlew :core:data:test --tests "com.zencare.data.repository.ConsultationRepositoryTest"
```

**Environment:** `JAVA_HOME` must point to a JDK 17+ (Android Studio's bundled JBR works). `ANDROID_HOME` (or `local.properties` `sdk.dir`) must point to the Android SDK.

## Architecture Overview

This is **智医康 (Zencare)** — a medical health platform Android app with three business modules: AI chat consultation, health monitoring, and medical e-commerce.

**Tech stack:** Kotlin 2.1 + Jetpack Compose (Material3) + MVVM + UDF (Unidirectional Data Flow) + Hilt DI.

### Module Dependency Graph

```
:core:model        ← pure Kotlin, zero deps (DTOs, domain models, Route sealed interface)
:core:common       ← zero deps (AppResult, extensions)
:core:network      ← depends on :core:model (Retrofit APIs, MockInterceptor, OkHttp)
:core:data         ← depends on :core:model, :core:network (Room DB, Repository, DataStore)
:core:ui           ← depends on :core:model (Theme, shared Compose components)
:feature:*         ← depends on all :core:* modules (feature screens, ViewModels, NavGraphs)
:app               ← depends on everything (Application, MainActivity, top-level NavHost)
```

**Key rule:** Feature modules must NOT depend on each other. All shared code goes through `:core:model` for data types and `:core:ui` for shared composables.

### MVVM + UDF Pattern

Every screen follows this pattern:

```
Screen (Composable) ← observes UiState via StateFlow
     ↓ actions
ViewModel ← sealed Action interface
     ↓ calls
Repository ← AppResult<T> (Success/Error/Loading)
     ↓ uses
RemoteDataSource (Retrofit API) + LocalDataSource (Room DAO / DataStore)
```

**UiState** is a data class per screen. **Action** is a sealed interface per ViewModel. No screen mutates state directly — everything flows through `ViewModel.action()` → `StateFlow.update{}`.

### Navigation

Type-safe Navigation Compose routes defined in `:core:model` (`Route.kt`). Single Activity + NavHost with bottom tab bar (问诊/健康/商城). Each feature module exposes a `NavGraphBuilder.*NavGraph(navController)` extension function; the app module wires them together in `AppNavHost.kt`.

### Network Layer

API interfaces in `:core:network/api/` use Retrofit annotations. `ApiResponse<T>` wraps all responses with `{code, message, data}`. During development, `MockInterceptor` (enabled via `BuildConfig.MOCK_API` in debug builds) returns fake data so frontend work is unblocked while backend APIs are being built. Mock data is defined inline in the interceptor.

### Offline Caching

Room database (`ZencareDatabase`) has three entities: `ChatMessageEntity`, `HealthRecordEntity`, `CartItemEntity`. Repositories follow a "remote-first, cache-later" strategy: fetch from API, cache to Room, serve from cache on error. `CartDao` functions are observeable via `Flow`.

## Dependency Management

Gradle Version Catalog at `gradle/libs.versions.toml`. All version numbers and coordinates are centralized there. Never hardcode version strings in individual `build.gradle.kts` files.

## Mirror Configuration

Alibaba Cloud mirrors are configured in `settings.gradle.kts` for Maven repositories (Google, Maven Central, Gradle Plugin Portal). The Gradle wrapper (`gradle-wrapper.properties`) uses `gradle-8.11.1-all.zip` from Tencent Cloud mirror. A global `~/.gradle/init.gradle` also exists for all projects on this machine. If Android Studio reverts the wrapper URL (known issue), the `-all` distribution is pre-cached in `~/.gradle/wrapper/dists/` under both the mirror and official URL hashes.

## Platform Compatibility

- **minSdk**: 23 (Android 6.0)
- **targetSdk / compileSdk**: 36
- Compose requires Material3 components that may need manual `EdgeToEdge` handling pre-API 26.

## CodeGraph

This project has a CodeGraph MCP server configured. Use `codegraph_*` tools for structural queries (symbol lookup, call graphs, impact analysis) instead of grep. See `.cursor/rules/codegraph.mdc` for detailed usage patterns.
