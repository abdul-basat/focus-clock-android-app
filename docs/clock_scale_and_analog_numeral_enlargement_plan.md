# Focus Clock — Clock Scaling & Analog Numeral Prominence Architecture

## Detailed Implementation & UX Modernization Plan

**Document Path:** `/docs/clock_scale_and_analog_numeral_enlargement_plan.md`  
**Author:** Senior UI/UX Designer & Android Systems Architect  
**Status:** Ready for Review & Implementation  
**Target Milestone:** Release 2.4 — Hero Clock Typography & Adaptive Dial Proportions

---

## 1. Executive Summary & Senior UX Critique

### 1.1 The User Problem & Competitive Analysis
1. **Clock Too Small on Screen:**
   - On modern tall displays (aspect ratios 19.5:9 to 21:9), the current active focus clock is restricted by aggressive box constraints (`coerceIn(120f, 280f).dp`).
   - In Active Focus mode, over 65% of the vertical canvas consists of barren empty space, making the clock feel distant, detached, and lacking visual weight.
2. **Analog Clock Numerals ("Digits") are Inconspicuous:**
   - In `AnalogClockDialEngine.kt`, numeral size is clamped to `coerceIn(16f, 32f)`. Even on high-density screens (xxhdpi/xxxhdpi), numbers 1–12 render as diminutive tick marks rather than prominent typographic elements.
   - **Competitor Benchmark (MD Clock / StandBy Mode / Zen Clock):** In top-tier desk and focus clocks, numerals command the dial face (accounting for 35%–45% of dial radius). High-legibility, bold geometric digits transform the phone into an executive desk timepiece visible from 2–3 meters away.
3. **Absence of Cohesive Customization:**
   - While Wallpaper mode has an isolated scale factor, Active Focus mode lacks user-controlled clock sizing.
   - Users cannot configure whether they prefer an understated minimalist watch face or bold, room-filling typography.

---

## 2. Design System & Typographic Ergonomics

```
      COMPACT (0.85x)               STANDARD (1.0x)               PROMINENT (1.25x)               JUMBO (1.50x)
  ┌──────────────────────┐      ┌──────────────────────┐      ┌──────────────────────┐      ┌──────────────────────┐
  │                      │      │                      │      │         12           │      │        12            │
  │        12            │      │        12            │      │     11       1        │      │    11      1         │
  │      9  ●  3         │      │     9   ●   3        │      │   10     ●     2      │      │  10    ●    2        │
  │        6             │      │        6             │      │    9           3       │      │  9           3       │
  │                      │      │                      │      │    8           4       │      │   8         4        │
  │                      │      │                      │      │      7   6   5         │      │     7  6  5          │
  └──────────────────────┘      └──────────────────────┘      └──────────────────────┘      └──────────────────────┘
  Subtle Desk Companion        Balanced Default Focus         High Readability (2m)          Hero Desk Timepiece
```

### 2.1 The Two-Axis Customization Framework
To avoid cluttered settings, customization is structured along two complementary axes:
1. **Overall Clock Footprint (`clockScale`):**
   - Controls the diameter of the analog dial or bounding box of the digital/flip clock.
   - **Range:** `0.75f` (Compact) to `1.60f` (Ultra Jumbo).
   - **Presets:** Compact (`0.85x`), Standard (`1.00x`), Large (`1.25x`), Jumbo (`1.50x`).
2. **Analog Numeral Prominence (`analogNumeralScale`):**
   - Controls the relative typography scale of numbers 1–12 inside the dial, independent of hands and ticks.
   - **Modes:**
     - `STANDARD` (1.0x — ~22% radius): Classic dress watch proportion.
     - `LARGE` (1.35x — ~30% radius): Modern legibility watch face.
     - `JUMBO` (1.70x — ~38% radius): Bold typographic layout matching user screenshot.
     - `CARDINAL_EMPHASIS` (1.50x on 12/3/6/9, subtle ticks on intermediate hours): Bauhaus minimalist aesthetic.

### 2.2 Collision Prevention & Radial Spacing Formula
When numerals expand to 1.70x, naive centering causes collision with the outer bezel or hour hands:
$$\text{Numeral Center Radius } R_{\text{num}} = R \times \left(0.82 - 0.08 \times (\text{analogNumeralScale} - 1.0)\right)$$
$$\text{Inner Hand Safety Zone } R_{\text{hand\_clearance}} = R \times 0.58$$
This dynamic formula automatically pushes larger numerals outward toward the rim while contracting tick marks, ensuring clean negative space.

---

## 3. Architecture & Impacted Components

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Data & Persistence Layer                           │
│                                                                             │
│   FocusPreferences (FocusModels.kt)                                         │
│   ├── clockScale: Float (0.75f .. 1.60f, default 1.15f)                     │
│   ├── analogNumeralScale: Float (1.0f .. 1.80f, default 1.35f)              │
│   └── analogNumeralSize: AnalogNumeralSize (STANDARD, LARGE, JUMBO, CARDINAL)│
│                                                                             │
│   FocusPreferencesRepository.kt (DataStore Keys & Reactive Flows)           │
│   └── updateClockScale(Float), updateAnalogNumeralSize(AnalogNumeralSize)   │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │ Flow<FocusPreferences>
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Compose UI Rendering Pipeline                      │
│                                                                             │
│   ActiveFocusScreen.kt ──► ClockRenderer.kt                                 │
│                               ├── AnalogClockRenderer.kt                    │
│                               │   └── AnalogClockDialEngine.kt (Scale Aware)│
│                               ├── CleanDigitalClockRenderer.kt              │
│                               ├── FlipClockRenderer.kt                      │
│                               └── MinimalClockRenderer.kt                   │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │ Shared Preferences State
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                       Wallpaper Engine (Canvas Direct)                      │
│                                                                             │
│   WallpaperConfig (WallpaperModels.kt)                                      │
│   └── clockScale: Float, analogNumeralScale: Float                          │
│                                                                             │
│   WallpaperBitmapRenderer.kt (drawAnalogClock, drawCleanDigitalClock)       │
│   └── Dynamic Paint textSize calculation and dial radius layout             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Phased Implementation Plan & Manageable Milestones

### Milestone 1: Data Model & DataStore Persistence
- **Objective:** Extend persistence layer without breaking existing saved user preferences.
- **Files to Modify:**
  - `/app/src/main/java/com/sprinthon/focusclock/domain/model/FocusModels.kt`
  - `/app/src/main/java/com/sprinthon/focusclock/domain/model/WallpaperModels.kt`
  - `/app/src/main/java/com/sprinthon/focusclock/data/FocusPreferencesRepository.kt`
- **Detailed Tasks:**
  1. Add `enum class AnalogNumeralSize(val displayName: String, val scale: Float)`:
     - `STANDARD("Standard", 1.0f)`
     - `LARGE("Large", 1.35f)`
     - `JUMBO("Jumbo", 1.70f)`
     - `CARDINAL("12-3-6-9 Hero", 1.50f)`
  2. Add `clockScale: Float = 1.15f` and `analogNumeralScale: Float = 1.35f` to `FocusPreferences` data class.
  3. Add `PreferencesKeys.CLOCK_SCALE` and `PreferencesKeys.ANALOG_NUMERAL_SCALE` to `FocusPreferencesRepository`.
  4. Ensure safe fallback defaults if keys are missing from DataStore.
  5. Add `updateClockScale(scale: Float)` and `updateAnalogNumeralScale(scale: Float)` repository methods.
  6. Synchronize `WallpaperConfig` in `WallpaperModels.kt` to include `analogNumeralScale: Float = 1.35f`.

---

### Milestone 2: Analog Dial & Digital Renderers Uncapping (Compose)
- **Objective:** Allow clock dials and digital typography to dynamically scale up to hero sizes while maintaining sharp vector geometry and layout balance.
- **Files to Modify:**
  - `/app/src/main/java/com/sprinthon/focusclock/ui/clock/AnalogClockRenderer.kt`
  - `/app/src/main/java/com/sprinthon/focusclock/ui/clock/AnalogClockDialEngine.kt`
  - `/app/src/main/java/com/sprinthon/focusclock/ui/clock/CleanDigitalClockRenderer.kt`
  - `/app/src/main/java/com/sprinthon/focusclock/ui/clock/ClockRenderer.kt`
- **Detailed Tasks:**
  1. **Dial Size Constraints (`AnalogClockRenderer.kt`):**
     - Replace hardcoded `coerceIn(120f, 280f).dp` with dynamic calculation:
       ```kotlin
       val baseSize = minOf(constraints.maxWidth, constraints.maxHeight)
       val dialSizeDp = (baseSize * 0.72f * clockScale).coerceIn(140.dp, 420.dp)
       ```
  2. **Numeral Geometry & Sizing (`AnalogClockDialEngine.kt`):**
     - In `drawDialNumbers()`, remove `coerceIn(16f, 32f)` ceiling.
     - Compute base numeral size as:
       ```kotlin
       val numeralFontSizePx = (radius * 0.20f * analogNumeralScale)
       ```
     - For `CARDINAL` mode: render 12, 3, 6, 9 at `radius * 0.28f` with bold weight, while 1, 2, 4, 5, 7, 8, 10, 11 render as sleek accent pips or muted mini-numbers.
     - Adjust baseline Y offset calculation so larger numbers remain vertically centered on their radial coordinate points.
  3. **Digital Hero Typography (`CleanDigitalClockRenderer.kt`):**
     - Expand font size calculation from `coerceIn(36f, 140f)` to up to `210sp` in portrait stacked layout (as seen in user screenshot).
     - Maintain negative letter-spacing (`tracking = -0.04em`) for a tight, high-fashion Scandinavian digital watch appearance.

---

### Milestone 3: Active Focus Screen Responsive Layout
- **Objective:** Give the clock visual dominance on `ActiveFocusScreen` while ensuring session controls (timer, ambient sound, stop button) gracefully fit without clipping.
- **Files to Modify:**
  - `/app/src/main/java/com/sprinthon/focusclock/ui/screens/ActiveFocusScreen.kt`
- **Detailed Tasks:**
  1. Pass `preferences.clockScale` and `preferences.analogNumeralScale` into `ClockRenderer`.
  2. Structure the central clock container using flexible weight distribution (`Modifier.weight(1f)` with `BoxWithConstraints`).
  3. When controls auto-fade (Zen immersion mode), dynamically expand clock padding so the clock gracefully commands the full screen.
  4. Ensure landscape orientation preserves a minimum 48dp margin around the dial.

---

### Milestone 4: Wallpaper Bitmap Engine Synchronization (Native Canvas)
- **Objective:** Ensure that when users set their home/lock screen wallpaper or live wallpaper, the analog digits and clock footprint precisely mirror their in-app settings.
- **Files to Modify:**
  - `/app/src/main/java/com/sprinthon/focusclock/playback/WallpaperBitmapRenderer.kt`
  - `/app/src/main/java/com/sprinthon/focusclock/playback/FocusClockWallpaperService.kt`
- **Detailed Tasks:**
  1. Update `drawAnalogClock()` in `WallpaperBitmapRenderer.kt` to accept `analogNumeralScale`.
  2. Scale `dialPaint.textSize` according to `radius * 0.20f * analogNumeralScale`.
  3. Synchronize `drawCleanDigitalClock()` to scale digits up to 30% larger when `clockScale` is increased.
  4. Recalculate tick marks and date badge vertical offset so enlarged numerals never overlap supporting text.

---

### Milestone 5: Intuitive Customization UI & Interactive Previews
- **Objective:** Give users an effortless, delightful UI to customize their clock size and numeral prominence with instant visual feedback.
- **Files to Modify:**
  - `/app/src/main/java/com/sprinthon/focusclock/ui/screens/settings/ClockSettingsScreen.kt`
  - `/app/src/main/java/com/sprinthon/focusclock/ui/screens/ClockWallpaperCustomizationScreen.kt`
- **Detailed Tasks:**
  1. **New "Clock Size & Dial Digits" Section in `ClockSettingsScreen.kt`:**
     - **Live Dial Preview Card:** Real-time mini preview that updates instantaneously as the user changes scale.
     - **Quick-Select Preset Chips:**
       - `Compact (85%)` | `Standard (100%)` | `Prominent (125%)` | `Jumbo (150%)`
     - **Fine-Tuning Slider:** Continuous range from `75%` to `160%` with haptic detents at 100% and 125%.
     - **Analog Numeral Prominence Selector:**
       - Interactive segmented button or chip row: `Standard` | `Large` | `Jumbo` | `12-3-6-9 Focus`.
       - Conditionally visible when Clock Style is `ANALOG` or `MINIMAL`.
  2. **Wallpaper Studio Deck Update (`ClockWallpaperCustomizationScreen.kt`):**
     - In the "Style" deck tab, add the **Digit Prominence** control alongside the existing position/scale sliders.

---

### Milestone 6: Quality Assurance, Verification & Automated Tests
- **Objective:** Verify compilation, UI rendering across screen densities, and backward-compatible DataStore persistence.
- **Files to Modify/Run:**
  - `/app/src/test/java/com/sprinthon/focusclock/WallpaperClockPersistenceTest.kt`
  - `/app/src/test/java/com/sprinthon/focusclock/AnalogClockDialEngineTest.kt`
- **Verification Checklist:**
  - [ ] `compile_applet` passes cleanly without warnings or errors.
  - [ ] Robolectric unit tests confirm `clockScale` and `analogNumeralScale` persist and restore accurately.
  - [ ] Analog dial numerals scale up to 1.7x without clipping outer dial boundary.
  - [ ] Digital clock in Active Focus renders bold hero digits matching competitor screenshot.
  - [ ] Live wallpaper renders at 60 FPS without memory leaks or Canvas allocation in draw loops.

---

## 5. Risk Assessment & Mitigations

| Risk | Impact | Mitigation Strategy |
| :--- | :---: | :--- |
| **Digit-Bezel Clipping:** At 1.7x, "10", "11", and "12" double digits could touch the outer bezel. | Low | Implement dynamic radial offset $R_{\text{num}} = R \times 0.76$ for Jumbo mode to pull numerals slightly inward. |
| **Small Screen Overlap:** On small budget devices (e.g. 320dp width), large dial might push timer off-screen. | Medium | Use `BoxWithConstraints` and calculate dial size as a fraction of available screen height after subtracting top bar and timer. |
| **DataStore Migration:** Existing installs might load `0.0f` if key missing. | Low | Use `defaultIfNull` in DataStore map, providing standard defaults (1.15f scale, 1.35f numeral scale). |

---

## 6. Implementation Readiness

This plan is completely self-contained and ready for immediate implementation.  
Proceed to review the milestones above, and upon confirmation, Milestone 1 through Milestone 5 will be systematically executed.
