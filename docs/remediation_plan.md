# Focus Clock — Remediation & Code Quality Implementation Plan

This document details the phased remediation plan to resolve all lint failures, Compose best-practice warnings, security considerations, and resource cleanup identified during the senior codebase audit.

---

## Overview of Findings

| Phase | Category | Target Files | Priority | Status |
|---|---|---|---|---|
| **Phase 1** | Fatal Lint Errors & State Bugs | `AmbientSoundSettingsScreen.kt` | 🚨 Blocker | ✅ **Completed** |
| **Phase 2** | Compose API Standards & Deprecations | `ActiveFocusScreen.kt`, `FocusSettingsScreen.kt`, `StartFocusScreen.kt` | ⚠️ Medium | ✅ **Completed** |
| **Phase 3** | Manifest, Security & Resource Hygiene | `AndroidManifest.xml`, `colors.xml`, `ActiveFocusScreen.kt` | ℹ️ Low-Medium | ✅ **Completed** |
| **Phase 4** | Automated Verification & Regression Suite | Unit & Robolectric Tests | ✅ Verification | ✅ **Completed** |

---

## Detailed Phases

### 🔹 Phase 1: Critical Lint Error & State Management Fix
**Objective**: Fix the blocking lint failure that prevents `:app:lintDebug` from passing and causes dialog state loss on recomposition.

1. **Fix `UnrememberedMutableState` in `AmbientSoundSettingsScreen.kt`**:
   - **File**: `app/src/main/java/com/sprinthon/focusclock/ui/screens/settings/AmbientSoundSettingsScreen.kt` (Line 88)
   - **Current**:
     ```kotlin
     var showAddTrackDialog by androidx.compose.runtime.mutableStateOf(false)
     ```
   - **Correction**:
     ```kotlin
     var showAddTrackDialog by remember { mutableStateOf(false) }
     ```
   - **Impact**: Ensures the custom track addition dialog state persists during recomposition and eliminates the fatal Android Lint error.
2. **Phase 1 Verification**:
   - Run `:app:lintDebug` to verify fatal error elimination.

---

### 🔹 Phase 2: Jetpack Compose API Best Practices & Deprecations
**Objective**: Standardize composable API parameter signatures and resolve deprecated Material icon usages.

1. **Modifier Parameter Order (`ModifierParameter`)**:
   - Per Compose API guidelines, `modifier: Modifier = Modifier` should be the first optional parameter in public composable signatures.
   - **Files to update**:
     - `app/src/main/java/com/sprinthon/focusclock/ui/screens/ActiveFocusScreen.kt`: Reorder `modifier: Modifier = Modifier` in `ActiveFocusScreen` composable signature.
     - `app/src/main/java/com/sprinthon/focusclock/ui/screens/settings/FocusSettingsScreen.kt`: Reorder `modifier: Modifier = Modifier` in `FocusSettingsScreen`.
     - `app/src/main/java/com/sprinthon/focusclock/ui/screens/StartFocusScreen.kt`: Reorder `modifier: Modifier = Modifier` in `StartFocusScreen`.
2. **Deprecated Icon References**:
   - **File**: `app/src/main/java/com/sprinthon/focusclock/ui/screens/settings/FocusSettingsScreen.kt` (Line 324)
   - **Correction**: Replace `Icons.Filled.VolumeUp` with `Icons.AutoMirrored.Filled.VolumeUp`.
3. **Phase 2 Verification**:
   - Run compilation and verify composable callers remain 100% binary/source compatible.

---

### 🔹 Phase 3: Manifest, Security & Resource Hygiene
**Objective**: Clean up redundant manifest declarations, document/secure background services and WebViews, and purge unused template resources.

1. **Clean Redundant Manifest Attributes**:
   - **File**: `app/src/main/AndroidManifest.xml`
   - **Change**: Remove redundant `android:label="@string/app_name"` from `MainActivity` declaration (already declared at `<application>` root level).
2. **Review & Annotate Media3 Service Export**:
   - **File**: `app/src/main/AndroidManifest.xml`
   - **Change**: Verify `FocusPlaybackService` `android:exported="true"` configuration with MediaSession intent filters and add lint suppression or permission tags as recommended by Media3 documentation.
3. **WebView Security Hardening**:
   - **File**: `app/src/main/java/com/sprinthon/focusclock/ui/screens/ActiveFocusScreen.kt`
   - **Change**: Ensure YouTube WebView configuration restricts allowed domains to official YouTube player origins and disables unnecessary web features (e.g. file access, geolocation).
4. **Remove Unused Color Assets**:
   - **File**: `app/src/main/res/values/colors.xml`
   - **Change**: Remove legacy unreferenced color keys (`purple_200`, `purple_500`, `purple_700`, `teal_200`, `teal_700`, `black`, `white`) that conflict with the centralized M3 token system in `Color.kt`.
5. **Phase 3 Verification**:
   - Check APK resource size and verify no runtime or compile-time resource ID breaks.

---

### 🔹 Phase 4: Test Suite Verification & Quality Assurance
**Objective**: Guarantee that all critical user journeys (active focus session, ambient sound configuration, background switching, preferences storage) remain defect-free.

1. **Run Full Robolectric Test Suite**:
   - Execute `:app:testDebugUnitTest`.
   - Verify `ActiveFocusTest`, `FocusAudioPlaybackTest`, `FocusBackgroundTest`, `FocusSessionManagerTest`, `SettingsHubTest`.
2. **Run Full Build & Lint**:
   - Execute full compilation and Android Lint checks.
   - Verify 0 errors.

---

## Execution Readiness
 
- Phase 1 has been executed and verified (Android Lint `:app:lintDebug` builds green with 0 errors).
- Phase 2 has been executed and verified (Compose modifier parameter order standardized, modern auto-mirrored icons applied).
- Phase 3 has been executed and verified (Manifest cleanup, WebView security hardening, and unused resource cleanup).
- Phase 4 has been executed and verified (Full compilation and lint verification completed with 0 errors).
- 🎉 **All 4 Phases Complete & Verified.**
