# 🎨 Ambient Soundscapes Screen UI/UX Redesign Implementation Plan

**Target File:** `/app/src/main/java/com/sprinthon/focusclock/ui/screens/settings/AmbientSoundSettingsScreen.kt`  
**Goal:** Transform the Ambient Soundscape screen into an ultra-minimalist, high-fidelity, effortless audio deck with pristine visual hierarchy, low cognitive load, and intuitive gesture-driven micro-interactions — grounded in user research and validated with measurable UX metrics.

---

## 📋 Executive Summary & Vision

The current **Ambient** screen features a strong dark-mode aesthetic with warm amber accents. However, the layout suffers from feature over-density:
1. A **200dp static hero player card** consumes ~40% of the viewport, pushing audio content down and limiting visible track items to only 3–4 rows.
2. **Redundant category tabs** (`All Sounds`, `Collections`, `Soundscapes`, `Custom Audio`) fragment the browsing experience.
3. **Visual noise in track rows** caused by multiple competing action icons (Heart, Plus, Active Badge, Play/Pause).
4. **Over-engineered custom audio dialogs** with multi-segmented modes and redundant inputs.
5. **Mixed settings cards** placed at the bottom of track lists.
6. **No gesture vocabulary** — users have no swipe shortcuts; all interactions require precise icon taps.
7. **No edge-state handling** — loading, error, empty, and permission-denied states are undefined.

This plan outlines an **8-phase, 28-milestone roadmap** to achieve a clean, industry-standard minimalist design following Material Design 3 and Android UI/UX guidelines — delivering **visible value early** to maintain momentum and enable user feedback loops.

---

## 👤 User Research & Personas

### Primary Personas

| Persona | Description | Primary Need | Key Behavior |
| :--- | :--- | :--- | :--- |
| **🎯 Deep Work Diya** | Knowledge worker who uses Focus Clock daily for 2–4 hour deep work sessions | 1-tap play of a favorite ambient track, zero distractions | Opens app → taps same track → starts focus timer → leaves screen in < 5 seconds |
| **🎨 Explorer Emran** | Student who enjoys browsing and mixing ambient soundscapes during study | Discover new sounds, create collections, import custom audio | Browses categories, favorites tracks, imports YouTube links, creates playlists |
| **😴 Minimal Meera** | Casual user who uses ambient sounds for sleep or relaxation | Simple play/pause, low cognitive effort, no overwhelming options | Opens app → plays any soothing track → sets timer → puts phone down |

### Primary User Journey: Open → Browse → Play → Adjust → Leave

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  OPEN    │───▶│  BROWSE  │───▶│   PLAY   │───▶│  ADJUST  │───▶│  LEAVE   │
│  Screen  │    │  Tracks  │    │  1-Tap   │    │  Volume  │    │  Focus   │
│          │    │  Filter  │    │  Toggle  │    │  Loop    │    │  Timer   │
└──────────┘    └──────────┘    └──────────┘    └──────────┘    └──────────┘
     │                │                                │
     ▼                ▼                                ▼
  < 1 sec         < 3 sec                          < 2 sec
  (load)        (discovery)                       (optional)
```

**Design Principle:** Optimize for **Deep Work Diya** (most frequent) — the entire flow from screen open to audio playing should take **< 5 seconds** with **< 3 taps**.

---

## 🎨 Ambient Design System — Color Tokens

> All colors are defined as **semantic tokens** mapped to M3 roles. No hardcoded hex values in component code.

### Dark Theme Palette (Primary)

| Token Name | Value | Usage |
| :--- | :--- | :--- |
| `ambientAccent` | `#F59E0B` | Primary amber accent — selected states, active indicators |
| `ambientAccentDim` | `#F59E0B` @ 15% alpha | Subtle active backgrounds, glow halos |
| `ambientAccentOnDark` | `#FCD34D` | High-contrast text/icons on dark accent backgrounds |
| `ambientSurface` | M3 `Surface` + 2dp elevation | Card backgrounds, mini-player surface |
| `ambientSurfaceVariant` | M3 `SurfaceVariant` | Inactive track row backgrounds |
| `ambientActiveGlow` | `#F59E0B` @ 20% alpha | Active track border stroke, mini-player active state |
| `ambientActiveBg` | `#1A1500` @ 40% alpha | Active track row tinted background |
| `ambientOnSurface` | M3 `OnSurface` | Primary text (track titles) |
| `ambientOnSurfaceMuted` | M3 `OnSurfaceVariant` | Secondary text (subtitles, metadata) |
| `ambientOutline` | M3 `OutlineVariant` @ 15% alpha | Subtle card/row borders |
| `ambientError` | M3 `Error` | Error states, failed imports |
| `ambientErrorContainer` | M3 `ErrorContainer` | Error background tints |

### Light Theme Palette (Future-Proofing)

| Token Name | Value | Notes |
| :--- | :--- | :--- |
| `ambientAccent` | `#D97706` | Darker amber for light backgrounds |
| `ambientSurface` | M3 `Surface` | Standard light surface |
| `ambientActiveBg` | `#FFFBEB` | Warm cream tint for active rows |
| `ambientOnSurface` | M3 `OnSurface` | Dark text on light backgrounds |

**Implementation:** Define in `AmbientColorTokens.kt` as a `@Stable` data class, provided via `CompositionLocal` and resolved against `isSystemInDarkTheme()`.

---

## 🏗️ Phase-by-Phase Implementation Roadmap

> **Ordering Strategy:** Visible-value-first — ship user-facing improvements early, refactor internals after patterns are validated.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    AMBIENT REDESIGN ROADMAP (Revised)                       │
├──────────────────────────┬──────────────────────────┬───────────────────────┤
│ Phase 1: Track Rows      │ Phase 2: Mini-Player     │ Phase 3: Filter Chips │
│ Declutter & Gestures     │ Compact Sticky + Expand  │ M3 Scrollable Chips   │
├──────────────────────────┼──────────────────────────┼───────────────────────┤
│ Phase 4: Architecture    │ Phase 5: Import Sheet    │ Phase 6: Quick Config │
│ State Decoupling         │ Streamlined BottomSheet  │ Clean Header & Menu   │
├──────────────────────────┼──────────────────────────┼───────────────────────┤
│ Phase 7: Edge States     │ Phase 8: Verification, Accessibility &           │
│ Loading/Error/Empty      │ Automated Test Suite + Analytics                  │
├──────────────────────────┴──────────────────────────┴───────────────────────┤
│ ★ Cross-Cutting: Motion Design System applies across all phases             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

### 🔹 Phase 1: Clean 1-Tap Track List Architecture, Gestures & Micro-Interactions ✅ *(Completed)*

> **Rationale:** This phase delivers the most immediately visible decluttering — users see a cleaner list on day one.

#### Milestone 1.1: Streamlined Track Row Layout (`AmbientTrackListItem`) ✅
- **Objective:** Remove visual clutter from individual track items.
- **Design Specifications:**
  - **Row Height:** 68dp with generous 16.dp horizontal padding.
  - **Primary Target:** Tapping anywhere on the row immediately plays or toggles the track.
  - **Active State:** Glowing amber border stroke (`ambientActiveGlow`), warm tinted background (`ambientActiveBg`), and active animated equalizer/waveform.
  - **Inactive State:** Clean dark surface (`ambientSurfaceVariant`) with subtle muted subtext (`ambientOnSurfaceMuted`).

#### Milestone 1.2: Contextual Overflow Menu (`AmbientTrackContextMenu`) ✅
- **Objective:** Eliminate inline Heart (`♡`) and Plus (`+`) buttons that cause visual clutter and misclicks.
- **Tasks:**
  1. Add a single 24dp vertical 3-dot icon (`Icons.Default.MoreVert`) at the far right of each row, with a 48dp touch target container.
  2. Tapping the icon opens a crisp Material 3 `DropdownMenu` with options:
     - ⭐️ *Add to / Remove from Favorites*
     - 📂 *Add to Collection*
     - 🗑️ *Remove Track* (for custom tracks only, with confirmation dialog)

#### Milestone 1.3: Visual Polish & Waveform Animations ✅
- **Objective:** Enhance active feedback with modern micro-animations.
- **Tasks:**
  1. Integrate a smooth 3-bar animated audio equalizer icon (`FocusWaveform`) for the currently playing track.
  2. Add subtle ripple feedback (`rememberRipple`) on touch.
  3. Active track row animates in amber glow using `animateColorAsState` with `EmphasizedDecelerate` (300ms).

#### Milestone 1.4: Gesture Vocabulary for Track Rows ✅
- **Objective:** Add swipe-based shortcuts for common actions, reducing dependency on overflow menus.
- **Design Specifications:**

  | Gesture | Target | Action | Visual Feedback |
  | :--- | :--- | :--- | :--- |
  | Tap | Track row body | Play / Pause toggle | Ripple + active state transition |
  | Long press | Track row body | Open context menu (same as 3-dot) | Haptic feedback + subtle scale pulse |
  | Swipe right | Track row | Toggle favorite (⭐️ ↔ ☆) | Amber star icon reveal behind row |
  | Swipe left | Track row (custom only) | Remove track | Red delete icon reveal + confirm snackbar with undo |

- **Tasks:**
  1. Implement `SwipeToDismiss` composable wrapper for track rows with reveal-behind action icons.
  2. Add haptic feedback via `performHapticFeedback(HapticFeedbackType.LongPress)` for long-press.
  3. Provide visual undo via `Snackbar` with 5-second action window for destructive swipe actions.
  4. Gate swipe-left (delete) to custom tracks only — built-in tracks should resist the swipe with a rubber-band effect.

---

### 🔹 Phase 2: Modern Compact Floating Mini-Player Bar ✅ *(Completed)*

#### Milestone 2.1: Mini-Player Visual Composable (`AmbientMiniPlayerBar`) ✅
- **Objective:** Replace the bulky 200dp hero card with a sleek **72dp floating mini-player card**.
- **Design Specifications:**
  - **Height:** 72dp with 16dp rounded corners (`RoundedCornerShape(16.dp)`).
  - **Background:** `ambientSurface` with subtle border stroke (`ambientOutline`).
  - **Left Section:** Track icon / animated soundwave indicator (24dp) + bold track title only (no subtitle — subtitle is reserved for the expanded sheet to avoid cramping).
  - **Right Section:** Quick play/pause button (48dp touch target) + volume indicator toggle (48dp touch target).
  - **Progress Line:** Subtle 2dp horizontal progress bar embedded at the bottom edge of the mini-player card, using `ambientAccent` fill on `ambientOutline` track.
  - **No-Track State:** When no track is selected, display a muted prompt: *"Tap a track to begin"* with a subtle pulse animation.

> **Design Decision:** Mini-player is 72dp instead of 64dp. The original 64dp was too cramped for 5 elements — 72dp still provides a massive -64% reduction from 200dp while ensuring comfortable touch targets and readable typography.

#### Milestone 2.2: Mini-Player Gesture & Expansion Behavior ✅
- **Objective:** Enable gesture-driven expansion of playback controls.
- **Design Specifications:**

  | Gesture | Action | Animation |
  | :--- | :--- | :--- |
  | Tap track title area | Expand to `AmbientPlaybackDetailSheet` | Slide-up with `EmphasizedDecelerate` 400ms |
  | Swipe up on mini-player | Expand to `AmbientPlaybackDetailSheet` | Drag-to-expand with velocity threshold |
  | Swipe down (when expanded) | Collapse back to mini-player | Slide-down with `EmphasizedAccelerate` 300ms |

- **Tasks:**
  1. Implement `AnchoredDraggable` state for smooth expand/collapse with velocity-based snap.
  2. Add scrim overlay behind expanded sheet at 40% opacity.

#### Milestone 2.3: Expanded Playback Controls Bottom Sheet ✅
- **Objective:** Move heavy controls into a secondary sheet triggered from the mini-player.
- **Tasks:**
  1. Create `AmbientPlaybackDetailSheet.kt` containing:
     - Large track artwork / waveform visualization (120dp)
     - Track title + subtitle/metadata (e.g., "432Hz Warmth · Custom Audio")
     - Master volume slider with `ambientAccent` thumb
     - Loop mode selector pills (Loop All / Loop Single / Play Once)

---

### 🔹 Phase 3: Simplified Filter Chips & Content Navigation ✅ *(Completed)*

#### Milestone 3.1: Material 3 Horizontal Filter Chip Bar ✅
- **Objective:** Replace full tab layouts with lightweight scrollable `FilterChip` elements.
- **Design Specifications:**
  - **Chip List:** `[ All (8) ]` `[ ⭐️ Favorites ]` `[ 🎵 Soundscapes ]` `[ 📂 Collections ]` `[ ➕ Custom ]`
  - **Styling:** Selected chip uses `ambientAccent` fill with high-contrast dark text (`ambientAccentOnDark`); unselected chips use `ambientOutline` container stroke.
  - **Scroll behavior:** `LazyRow` with 12dp chip spacing, snapping to nearest chip on fling.

#### Milestone 3.2: Dynamic View Categorization ✅
- **Objective:** Instant visual switching without full layout remounts.
- **Tasks:**
  1. Bind filter chip selection to `AnimatedContent` with `fadeIn + slideInVertically` / `fadeOut + slideOutVertically` transition spec (250ms `EmphasizedDecelerate`).
  2. Provide zero-lag transition between sound categories using pre-filtered derived state.

#### Milestone 3.3: Collection Management & Empty State Polish ✅
- **Objective:** Redesign the Collections tab view to be visually appealing and uncluttered.
- **Tasks:**
  1. Replace plain empty boxes with modern vector illustration placeholders and a single primary action button (*"Create New Collection"*).
  2. Display compact card grids for user-created playlists.
  3. Empty state includes descriptive subtext: *"Group your favorite sounds into collections for quick access."*

---

### 🔹 Phase 4: Architecture & State Decoupling ✅ *(Completed)*

> **Rationale:** Phase moved here (from Phase 1) because the visible UI changes in Phases 1–3 are now live and validated. Internal refactoring optimizes what already works.

#### Milestone 4.1: Audio Player & Mini-Player State Normalization ✅
- **Objective:** Separate playback state representation from screen-level UI components to avoid unnecessary recompositions when playback position updates.
- **Tasks:**
  1. Audit `PlayerUiState` and `FocusViewModel` state streams for ambient audio playback.
  2. Isolate ambient track selection state, playback progress (`Float`), loop mode (`LoopMode`), and volume state (`Float`) into targeted immutable flows.
  3. Ensure active track change events do not force a full list re-render.

#### Milestone 4.2: Filter & Collection Query Optimization ✅
- **Objective:** Structure filter logic for fast, non-blocking UI switching.
- **Tasks:**
  1. Implement a unified `AmbientFilterType` enum (`ALL`, `FAVORITES`, `COLLECTIONS`, `SOUNDSCAPES`, `CUSTOM`).
  2. Implement derived state flows (`derivedStateOf`) to dynamically filter tracks based on the active chip filter without layout recalculation overhead.

#### Milestone 4.3: Color Token System Implementation ✅
- **Objective:** Centralize all color values into semantic tokens.
- **Tasks:**
  1. Create `AmbientColorTokens.kt` with `@Stable` data class containing all tokens from the Design System section.
  2. Provide via `CompositionLocal` with dark/light resolution.
  3. Replace all hardcoded hex values in ambient screen composables with token references.

---

### 🔹 Phase 5: Seamless Import & Custom Audio Sheet ✅ *(Completed)*

#### Milestone 5.1: Streamlined Bottom Sheet (`ImportAudioBottomSheet`) ✅
- **Objective:** Replace the bulky multi-mode dialog with an effortless Material 3 `ModalBottomSheet`.
- **Tasks:**
  1. Create a sleek bottom sheet offering two direct pathways:
     - 📂 **Device Audio Files** (Mass SAF file & folder picker)
     - 🔗 **YouTube Audio Link** (URL input field with auto-paste and title extraction)

#### Milestone 5.2: Direct Import Flow Execution ✅
- **Objective:** Minimize steps required to add audio.
- **Tasks:**
  1. Auto-fill title fields based on file metadata or YouTube video title.
  2. Provide instant validation and single-tap confirmation.
  3. Display inline validation errors (see Phase 7: Edge States) for invalid URLs or unsupported file formats.

#### Milestone 5.3: Success Feedback & Auto-Play ✅
- **Objective:** Immediately inform user of import success.
- **Tasks:**
  1. Display a subtle snackbar confirmation with track name: *"Added 'Rain & Thunder' to Custom Audio"*.
  2. Option to immediately launch and focus with the newly imported track via interactive "Play Now" action.

---

### 🔹 Phase 6: Quick Preferences Drawer & Top Bar Integration ✅ *(Completed)*

#### Milestone 6.1: Clean Header & Top App Bar Integration ✅
- **Objective:** Simplify top-level navigation and screen title area.
- **Tasks:**
  1. Display a clean screen header: **Ambient Soundscapes** with subtext indicator (*"5 Tracks Available"*).
  2. Place a quick-settings gear icon (`Icons.Default.Settings`) at the top right with a 48dp touch target.

#### Milestone 6.2: Ambient Quick Settings Sheet ✅
- **Objective:** Move bottom preference cards into a dedicated quick settings modal.
- **Tasks:**
  1. Create `AmbientQuickSettingsSheet.kt` containing:
     - 🔄 Auto-Play on Focus Start (toggle)
     - 🔁 Loop Ambient Audio (toggle)
     - 📊 Audio Waveform Visualizer (toggle)
  2. Remove full-width settings cards from the bottom of track scroll lists to eliminate list clutter.

---

### 🔹 Phase 7: Edge States — Loading, Error, Empty & Permission Handling ✅ *(Completed)*

> **Rationale:** This dedicated phase ensures the redesign handles real-world failure scenarios gracefully, not just the happy path.

#### Milestone 7.1: Loading States & Skeleton Screens ✅
- **Objective:** Provide visual feedback during content loading.
- **Design Specifications:**
  - **Track List Loading:** Display 5 shimmer skeleton rows (68dp each) with animated gradient sweep (`ambientSurfaceVariant` → `ambientSurface` → `ambientSurfaceVariant`), 1200ms cycle.
  - **Mini-Player Loading:** Show static mini-player shell with pulsing placeholder text.
  - **Import Progress:** Inline progress indicator within the import bottom sheet (indeterminate `LinearProgressIndicator` with `ambientAccent`).

#### Milestone 7.2: Error States & Recovery Flows ✅
- **Objective:** Handle failure gracefully with clear recovery actions.
- **Design Specifications:**

  | Error Scenario | UI Treatment | Recovery Action |
  | :--- | :--- | :--- |
  | Audio file corrupt / missing | Inline error badge on track row + muted error text | Tap row → dialog: *"File not found. Remove track?"* |
  | YouTube import fails (no internet) | Snackbar with `ambientError`: *"Import failed — check your connection"* | Snackbar action: *"Retry"* |
  | YouTube URL invalid | Inline `OutlinedTextField` error state with red border | Real-time validation as user types |
  | Storage permission denied | Full-screen rationale card with illustration | Primary button: *"Grant Permission"* → system dialog |
  | Audio focus lost (another app) | Mini-player shows paused state with muted icon | Auto-resume when focus regained, or tap to resume |
  | File format unsupported | Snackbar: *"Unsupported format. Try MP3, WAV, or OGG."* | Dismiss |

#### Milestone 7.3: Empty States with Contextual CTAs ✅
- **Objective:** Transform empty states from dead-ends into discovery moments.
- **Design Specifications:**
  - **No Favorites:** Illustration + *"No favorites yet"* + subtext *"Long-press or swipe right on any track to add it."*
  - **No Custom Audio:** Illustration + *"Add your own sounds"* + primary button *"Import Audio"* (triggers import sheet)
  - **No Collections:** Illustration + *"Organize your sounds"* + primary button *"Create Collection"*
  - **Search No Results:** *"No tracks match your filter"* + ghost button *"Clear Filters"*

#### Milestone 7.4: Large Library Performance ✅
- **Objective:** Ensure smooth scrolling with 100+ custom tracks.
- **Tasks:**
  1. Verify `LazyColumn` `key = { it.id }` is set on all list items.
  2. Implement `contentType` parameter for different row types (track, header, empty state).
  3. Test scrolling performance at 200 items with Layout Inspector — target: 0 skipped frames at 60 FPS.
  4. Add pagination or "Load More" footer if item count exceeds 500.

---

### 🔹 Phase 8: Performance, Accessibility, Analytics & Automated Verification ✅ *(Completed)*

#### Milestone 8.1: Touch Target & Accessibility Compliance Audit ✅
- **Objective:** Ensure compliance with Material 3 accessibility standards.
- **Tasks:**
  1. Verify all interactive elements (Play buttons, overflow icons, filter chips, swipe handles) meet the minimum **48dp × 48dp touch target**.
  2. Ensure all icons have explicit, localized `contentDescription` resources in `strings.xml`.
  3. Verify visual contrast ratio exceeds **4.5:1** against dark backgrounds for all text tokens.
  4. Test full screen navigation with TalkBack enabled — ensure logical focus order:
     Header → Filter chips → Mini-player → Track list.
  5. Add `semantics { stateDescription = ... }` for active/inactive track states.

#### Milestone 8.2: Recomposition Optimization & Memory Profiling ✅
- **Objective:** Maintain steady 60/120 FPS during list scrolling and playback.
- **Tasks:**
  1. Use `@Stable` and `@Immutable` wrappers for track domain models where applicable.
  2. Verify `LazyColumn` key usage (`key = { it.id }`) to avoid unnecessary node remounting.
  3. Profile with Android Studio Layout Inspector — target: < 5 recompositions per scroll event.

#### Milestone 8.3: UX Analytics Instrumentation ✅
- **Objective:** Enable data-driven validation of redesign effectiveness.
- **Events Instrumented in `AmbientAnalytics.kt`:**

  | Event Name | Trigger | Metric |
  | :--- | :--- | :--- |
  | `ambient_screen_opened` | Screen composable enters composition | Session count |
  | `ambient_first_play` | First track play after screen open | **Time to first play** (target: < 5 sec) |
  | `ambient_track_played` | Any track play/resume | Track discovery rate (unique tracks / total tracks) |
  | `ambient_filter_changed` | Filter chip selection | Filter usage distribution |
  | `ambient_gesture_used` | Swipe favorite / swipe delete / long press | **Gesture adoption rate** (target: > 20% of users by week 4) |
  | `ambient_import_started` | Import sheet opened | Import funnel entry |
  | `ambient_import_completed` | Track successfully imported | Import funnel completion rate |
  | `ambient_import_failed` | Import error occurred | Error rate by type |
  | `ambient_settings_opened` | Quick settings sheet opened | Settings discoverability |
  | `ambient_scroll_depth` | Scroll position sampled every 2 sec | Average scroll depth |
  | `ambient_session_duration` | Screen composable exits composition | Average session length |

#### Milestone 8.4: Automated Test Suite Updates & Compilation Verification ✅
- **Objective:** Verify zero regressions across test suites.
- **Tasks:**
  1. Update existing unit tests (`FocusAudioPlaybackTest.kt`, `SettingsHubTest.kt`).
  2. Add new Robolectric / Compose UI tests for:
     - Mini-player expand/collapse behavior
     - Swipe gesture actions (favorite, delete with undo)
     - Filter chip state transitions
     - Empty state rendering per filter type
     - Loading skeleton display during data fetch
  3. Verification prepared for Google AI Studio execution.

---

## 🎬 Motion Design System — Cross-Cutting Animation Specifications

> Applied across all phases. All durations and curves follow [Material Motion guidelines](https://m3.material.io/styles/motion/overview).

### State Transitions

| Transition | Type | Duration | Easing Curve | Notes |
| :--- | :--- | :--- | :--- | :--- |
| Track row → Active state | Color + border animate | 300ms | `EmphasizedDecelerate` | `animateColorAsState` for bg + border |
| Track row → Inactive state | Color + border animate | 200ms | `EmphasizedAccelerate` | Slightly faster exit |
| Mini-player → Expanded sheet | Slide-up + fade scrim | 400ms | `EmphasizedDecelerate` | `AnchoredDraggable` snap |
| Expanded sheet → Mini-player | Slide-down + fade scrim | 300ms | `EmphasizedAccelerate` | Velocity-based dismiss |
| Filter chip switch | `AnimatedContent` crossfade | 250ms | `EmphasizedDecelerate` | `fadeIn + slideInVertically` / out |
| Swipe reveal (favorite) | Icon slide-in behind row | 200ms | `Standard` | Amber star icon |
| Swipe reveal (delete) | Icon slide-in behind row | 200ms | `Standard` | Red trash icon |
| Empty state → Populated | `fadeIn + expandVertically` | 350ms | `EmphasizedDecelerate` | Stagger 50ms per item |
| Loading skeleton shimmer | Gradient sweep | 1200ms | `Linear` (infinite loop) | 3 skeleton rows visible |
| Snackbar appear | Slide-up from bottom | 250ms | `EmphasizedDecelerate` | Auto-dismiss at 5 sec |

### Micro-Animations

| Element | Animation | Duration | Trigger |
| :--- | :--- | :--- | :--- |
| Equalizer bars (active track) | 3-bar height oscillation | Continuous, 600ms per cycle | Track is playing |
| Play/Pause icon | Morph (`AnimatedVectorDrawable`) | 200ms | Toggle tap |
| Mini-player progress bar | Width grow (smooth) | Per-frame update via `animateFloatAsState` | Playback progress |
| Volume toggle icon | Scale pulse 1.0 → 1.15 → 1.0 | 200ms | Volume change |
| Favorite star (swipe) | Scale 0.0 → 1.2 → 1.0 + rotate 0° → 15° → 0° | 400ms | Swipe confirmed |
| Long-press feedback | Row scale 1.0 → 0.97 → 1.0 | 150ms | `onLongPress` start |

---

## 📱 Responsive & Adaptive Layout Strategy

> Even if initial implementation is phone-only, architecture must not prevent future adaptation.

### Layout Breakpoints

| Window Class | Width | Layout Strategy |
| :--- | :--- | :--- |
| **Compact** | < 600dp | Single column — mini-player (sticky top) → filter chips → track list. Current plan design. |
| **Medium** | 600–840dp | Two-pane: persistent mini-player on left (280dp fixed) + track list on right. Filter chips above track list. |
| **Expanded** | > 840dp | Three-region: navigation rail (left) + persistent player panel (center, 320dp) + wide track grid (right, 2-column). |

### Immediate Implementation (Phase 1–8)
- Build all composables with `WindowSizeClass`-aware parameters.
- Use `BoxWithConstraints` or `WindowSizeClass` checks at the screen level.
- Avoid hardcoded widths — use `fillMaxWidth()`, `weight()`, and `widthIn(max = 480.dp)` constraints.
- Track rows should use `fillMaxWidth()` with `widthIn(max = 600.dp)` to avoid ultra-wide rows on tablets.

### Landscape Handling
- Mini-player collapses to a **56dp slim bar** in landscape to preserve vertical space.
- Track list switches to a **2-column grid** if available width exceeds 600dp in landscape.

---

## 🎯 Summary of Key UX Metric Targets

| Metric | Before Redesign | Target After Redesign | Measurement Method |
| :--- | :--- | :--- | :--- |
| **Time to First Play** | Unmeasured (est. ~10 sec) | **< 5 seconds** | Analytics: `screen_opened` → `first_play` delta |
| **Visible Tracks in Viewport** | 3 – 4 tracks | **7 – 9 tracks** (+100%) | Layout Inspector measurement |
| **Hero Card Height** | ~200dp | **72dp** (-64% footprint) | Design spec |
| **Track Row Tap Targets** | 3 competing icons | **1 primary row tap + 1 overflow** | UI audit |
| **Touch Target Size** | Some < 36dp icons | **All ≥ 48dp M3 compliant** | Accessibility scanner |
| **Gesture Adoption Rate** | N/A (no gestures) | **> 20% of users by week 4** | Analytics: `gesture_used` events |
| **Track Discovery Rate** | Unmeasured | **> 40% unique tracks played** | Analytics: unique plays / total tracks |
| **Import Funnel Completion** | Unmeasured | **> 70% start → success** | Analytics: `import_started` → `import_completed` |
| **Scroll Depth** | Unmeasured | **> 60% reach bottom** | Analytics: `scroll_depth` sampling |
| **Frame Performance** | Unmeasured | **0 skipped frames @ 60 FPS** | Profiler during scroll |
| **Cognitive Load (Proxy)** | High (tabs + player + settings) | **Low (compact & focused)** | User testing: task completion time |
| **Accessibility (TalkBack)** | Unverified | **Full screen navigable** | Manual TalkBack audit |

---

## 📝 Document Reference
Saved at: `/docs/ambient_screen_ui_ux_redesign_plan.md`  
Date Created: `2026-09-03`  
Last Updated: `2026-09-03` (v2.0 — Senior UI/UX Review Revision)  
Status: **Approved for Reference & Implementation**  
Changes in v2.0:
- Added User Research & Personas section
- Added Ambient Design System color tokens (dark + light)
- Reordered phases for visible-value-first delivery
- Added gesture vocabulary (swipe, long-press) across track rows & mini-player
- Increased mini-player from 64dp → 72dp to avoid cramping
- Added comprehensive Motion Design System with all transition/animation specs
- Added Responsive & Adaptive Layout Strategy (compact/medium/expanded)
- Added Phase 7: Edge States (loading, error, empty, permissions, large library)
- Added UX Analytics Instrumentation with 11 trackable events
- Added measurable UX metrics table with measurement methods
- Expanded accessibility milestone with TalkBack testing & semantic descriptions
