# FocusClock: Optimization, Obfuscation & Android 16 (API 36) Compliance Plan

## Document Overview
This document outlines a structured, phased roadmap to resolve the Google Play Store Android 16 (API 36) requirement, harden security/code obfuscation, drastically reduce download and installed app footprint, and enhance adaptive screen responsiveness.

---

## Roadmap at a Glance

```
┌───────────────────────────┐
│ Phase 1: API 36 Upgrade   │ ──► TargetSdk 36, edge-to-edge, foreground service checks
└─────────────┬─────────────┘
              ▼
┌───────────────────────────┐
│ Phase 2: R8 & Obfuscation │ ──► Eliminate blanket keeps, harden reverse-engineering defense
└─────────────┬─────────────┘
              ▼
┌───────────────────────────┐
│ Phase 3: App Size Trim    │ ──► Remove material-icons-extended, prune unused Media3 modules
└─────────────┬─────────────┘
              ▼
┌───────────────────────────┐
│ Phase 4: Responsive UI    │ ──► Window Size Classes for tablets, foldables, and landscape
└─────────────┬─────────────┘
              ▼
┌───────────────────────────┐
│ Phase 5: Verification     │ ──► Robolectric testing, Release AAB build, size benchmarks
└───────────────────────────┘
```

---

## Phase 1: Android 16 (API Level 36) Compliance & Platform Modernization

### Objective
Update target and compile SDKs to meet Google Play Console requirements while ensuring system compatibility with Android 16 (API 36) behavior changes.

### Key Milestones
- [x] **1.1 Gradle Configuration Update**
  - Update `compileSdk = 36` and `targetSdk = 36` in `app/build.gradle.kts`.
  - Update Android Gradle Plugin / Kotlin version catalog alignments if needed in `gradle/libs.versions.toml`.
- [x] **1.2 Edge-to-Edge & System Bar Handling**
  - Audit `MainActivity.kt` and top-level screen Scaffolds (`HomeScreen`, `ActiveFocusScreen`, `SettingsHubScreen`) to ensure compliance with Android 16 edge-to-edge enforcement.
  - Verify status bar and navigation bar insets (`WindowInsets.safeDrawing`, `WindowInsets.systemBars`) render without UI clipping.
- [x] **1.3 Predictive Back & Foreground Service Verification**
  - Ensure `android:enableOnBackInvokedCallback="true"` is declared in `AndroidManifest.xml`.
  - Validate `FocusPlaybackService` foreground service declaration with `android:foregroundServiceType="mediaPlayback"`.

---

## Phase 2: ProGuard & R8 Obfuscation Hardening (Ethical Hacker Grade)

### Objective
Remove excessive wildcard `-keep` rules that currently expose business logic, state models, and ViewModels to decompiler analysis (e.g. JADX), while maintaining runtime stability.

### Key Milestones
- [x] **2.1 Audit & Remove Blanket Keep Rules**
  - Removed blanket rules in `app/proguard-rules.pro`:
    - Removed `androidx.compose.**` wildcard retention
    - Removed `data.**`, `domain.model.**`, and `ui.navigation.**` blanket keeping
    - Removed `FocusViewModel` and `FocusPlayerManager` exposure
- [x] **2.2 Precision Retention for Serialization & Lifecycle**
  - Retained only Android Manifest entry points (`MainActivity`, `FocusPlaybackService`, `FocusMediaListenerService`).
  - Configured safe minification for Media3 interfaces and DataStore field members without exposing class names.
- [x] **2.3 Production Log & Metadata Sanitization**
  - Verified `-assumenosideeffects` strips all debug/verbose logs (`Log.d`, `Log.v`, `Log.i`) from release binaries.
  - Retained `SourceFile` and `LineNumberTable` obfuscated mappings for crash diagnostics.

---

## Phase 3: Binary & Installed Size Reduction (Shrinking DEX & ART Footprint)

### Objective
Eliminate bloat that inflates download size and drastically reduces installed storage consumption from Android runtime (`dex2oat`) compilation.

### Key Milestones
- [x] **3.1 Packaging & Resource Metadata Stripping**
  - Added APK packaging resource exclusions (`META-INF/{AL2.0,LGPL2.1}`, `*.version`, `INDEX.LIST`, `DEPENDENCIES`) to strip redundant metadata from APK packages.
- [x] **3.2 Modularize Media3 / ExoPlayer Dependencies**
  - Excluded unused `androidx.media3.ui` dependency, retaining only lightweight core `exoplayer` and `session` engines for audio playback.
- [x] **3.3 Resource & Asset Optimization**
  - Verified zero static `.ttf` font bundling — all clock typefaces use `androidx.compose.ui.text.google.fonts` with secure Google Play Services dynamic font fetching.
  - Verified WebP adaptive mipmap icons and vector drawable hierarchy.

---

## Phase 4: Screen Responsiveness & Adaptive Multi-Window Layouts

### Objective
Provide optimal viewing and ergonomics across compact phones, unfolded foldables, tablets, and landscape desktop/DeX modes.

### Key Milestones
- [x] **4.1 Window Size Class & Adaptive Layouts**
  - Added orientation and height responsiveness (`BoxWithConstraints`, `isCompact`, landscape two-pane layouts).
- [x] **4.2 Responsive Screen Refactoring**
  - **Clock & Dashboard (`HomeScreen`, `ActiveFocusScreen`)**:
    - *Compact*: Vertical stack layout with hero clock and quick controls.
    - *Expanded / Landscape*: Two-pane layout with clock canvas on the leading side and session controls on the trailing side.
  - **Configuration Screens (`StartFocusScreen`, `SettingsHubScreen`)**:
    - Applied `Modifier.widthIn(max = 600.dp)` and `Modifier.widthIn(max = 500.dp)` centering on wide/tablet screens to avoid stretched list rows and oversized buttons.
- [x] **4.3 Touch Target & Accessibility Standards**
  - Verified touch targets meet the standard 48dp minimum across dialog chips, action FABs, and timer controls (`minimumInteractiveComponentSize`).

---

## Phase 5: Verification, Automated Testing & Release Validation

### Objective
Verify that all changes build cleanly, pass unit/integration tests without regressions, and produce optimized release artifacts.

### Key Milestones
- [x] **5.1 Test Suite Execution**
  - Ran JVM and Robolectric test suites (`ActiveFocusTest`, `FocusAudioPlaybackTest`, `FocusSessionManagerTest`, `ExternalMusicIntegrationTest`, `SettingsHubTest`).
- [x] **5.2 Compilation & ProGuard Verification**
  - Executed full compilation and R8 optimization check via `compile_applet`.
  - Verified R8 minification and resource shrinking complete cleanly without runtime crashes.
- [x] **5.3 Size & Obfuscation Audit Report**
  - Documented before-and-after improvements for APK size, DEX method count, API 36 compliance, and security posture.

---

## Review & Approval Checklist

| Phase | Description | Status |
| :--- | :--- | :--- |
| **Phase 1** | Target API 36 Update & System Compatibility | Completed  |
| **Phase 2** | ProGuard / R8 Hardening & Security | Completed  |
| **Phase 3** | App Size & DEX Optimization | Completed  |
| **Phase 4** | Adaptive Screen Layouts (Tablets/Foldables) | Completed  |
| **Phase 5** | Verification & Final Release Build | Completed  |
