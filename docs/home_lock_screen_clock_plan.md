# Focus Clock — Phase 7: Home & Lock Screen Clock & Live Wallpaper

## Implementation Plan & Architectural Specification

This document provides a comprehensive, phase-wise architectural and implementation plan for the **Home and Lock Screen Clock Customization Studio & Live Wallpaper Service** in the native Android **Focus Clock** application.

---

## 1. Executive Summary & Core Objectives

### User Needs & Key Capabilities
1. **Full Free-form Positioning**: Effortless drag-and-drop and fine-tuned slider adjustment for clock positioning ($X$ and $Y$ offsets, anchor presets like Top, Center, Bottom, Top-Left, etc.).
2. **Analog Numeral Customization**: Support for both **Horizontal / Upright** numbers (always readable on vertical axis) and **Radial / Tangential** numbers (rotating along dial perimeter), plus minimalist pips/ticks.
3. **Contextual Live Preview (WYSIWYG)**: Customization screen that simulates real device environments (Home screen app launcher grids, search bar, and Lock screen notifications/shortcuts) so users customize against realistic content.
4. **Adaptive Backgrounds & Typography**: Full control over background solid colors, custom gallery photos, dimming/scrim filters, blur effects, clock font families, and scale multipliers.
5. **Native Android Live Wallpaper Integration**: High-performance, battery-efficient `WallpaperService` engine that renders smoothly on both the Home Screen and Lock Screen.

---

## 2. Architecture & System Flow

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
│                                                                        │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │            ClockWallpaperCustomizationScreen (WYSIWYG)         │   │
│   │  - Interactive Drag-and-Snap Canvas with Haptic Feedback       │   │
│   │  - Mock Home Screen Overlay (Icons, Search Bar, Dock)          │   │
│   │  - Mock Lock Screen Overlay (Keyguard Icons, Notifications)    │   │
│   │  - Floating Bottom Sheet Deck (Style, Position, Background)    │   │
│   └────────────────────────────────────────────────────────────────┘   │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Updates State
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        Data & Domain Layer                             │
│                                                                        │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │ FocusPreferencesRepository (DataStore)                         │   │
│   │  - WallpaperClockPosition (xOffsetRatio, yOffsetRatio, anchor) │   │
│   │  - AnalogNumeralStyle (HorizontalUpright, RadialRotated, Pips) │   │
│   │  - WallpaperBackgroundConfig (Color, ImageUri, Blur, Scrim)    │   │
│   │  - WallpaperElementsConfig (ShowDate, ShowStreak, Motto)       │   │
│   └────────────────────────────────────────────────────────────────┘   │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Emits State Flow
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        Android OS Integration Layer                    │
│                                                                        │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │ FocusClockWallpaperService (WallpaperService.Engine)           │   │
│   │  - Hardware-accelerated Canvas rendering                       │   │
│   │  - Visibility-aware loop (0% idle battery drain)                │   │
│   │  - Flow-driven dynamic re-renders on preference change         │   │
│   └────────────────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Detailed Milestone Breakdown

### Milestone 1: Domain Models & Data Persistence
**Goal**: Define all state models and integrate persistence into `FocusPreferencesRepository`.

- **1.1 Clock Position Models**:
  - `ClockAlignment` (TopStart, TopCenter, Center, BottomCenter, CustomOffset).
  - `WallpaperClockPosition` (xPercent: Float [-1.0f..1.0f], yPercent: Float [-1.0f..1.0f], scale: Float [0.6f..1.6f]).
- **1.2 Analog Numeral Orientation Models**:
  - `AnalogNumeralOrientation`:
    - `HORIZONTAL_UPRIGHT`: Numbers 1 to 12 rendered upright (0° rotation relative to screen).
    - `RADIAL_ROTATED`: Numbers rotate tangentially to dial curvature.
    - `MINIMAL_TICKS`: Clean hour tick marks without numerals.
    - `MINIMAL_PIPS`: Hour dot markers.
- **1.3 Wallpaper Configuration Model**:
  - `WallpaperConfig`:
    - `clockStyle`: Digital, Analog, Flip, Minimal.
    - `fontFamily`: ClockFont enum.
    - `color`: Hex color or Primary dynamic color.
    - `backgroundType`: SolidColor, GalleryImage.
    - `backgroundColorHex`: String.
    - `backgroundImageUri`: String?.
    - `scrimOpacity`: Float (0.0f to 0.8f).
    - `blurRadius`: Int (0 to 25).
    - `showDate`: Boolean.
    - `showMotto`: Boolean.
    - `customMotto`: String.
- **1.4 DataStore Preferences**:
  - Add DataStore keys and getter/setter flows in `FocusPreferencesRepository.kt`.

---

### Milestone 2: Analog Numeral & Clock Canvas Engine Updates
**Goal**: Enhance analog clock rendering algorithms to support vertical/horizontal numerals and positioning calculations.

- **2.1 Numeral Layout Geometry in `AnalogClockRenderer.kt`**:
  - Calculate trigonometric dial coordinates for each hour $1 \dots 12$:
    $$\theta = \left(i \times \frac{\pi}{6}\right) - \frac{\pi}{2}$$
    $$x = cx + r \cdot \cos(\theta), \quad y = cy + r \cdot \sin(\theta)$$
  - **Horizontal/Upright Mode**: Draw text centered at $(x, y)$ without Canvas rotation.
  - **Radial Mode**: Translate and rotate Canvas by $\theta + 90^\circ$ at $(x, y)$ before drawing text.
- **2.2 Decoupled Core Canvas Painter**:
  - Create a reusable `ClockCanvasEngine` capable of drawing to both:
    1. Jetpack Compose `DrawScope` (for in-app UI & Preview).
    2. Native Android `android.graphics.Canvas` (for `WallpaperService.Engine`).

---

### Milestone 3: Interactive Customization Studio (WYSIWYG Screen)
**Goal**: Build the user-facing customization screen with real-content simulation and drag gestures.

- **3.1 Interactive Canvas Viewport**:
  - Full-screen interactive surface supporting drag gestures with clamped bounds and snap lines (horizontal & vertical center).
  - Haptic feedback when crossing alignment snap points.
- **3.2 Real Screen Overlays (Simulation Modes)**:
  - **Home Screen Simulator**: Semi-transparent mock app grid (4x5 icon placeholders, mock Google search bar, bottom launcher dock).
  - **Lock Screen Simulator**: Lock icon, mock notification card, bottom flashlight and camera shortcut buttons.
  - **Clean Mode**: Unobstructed wallpaper view.
- **3.3 Bottom Customization Control Deck**:
  - **Position Tab**: Up/Down & Left/Right fine-tuning sliders, Quick Alignment Presets (Top, Center, Bottom, Left, Right).
  - **Clock Tab**: Style selector, Numeral orientation toggle (Upright vs Rotated), Font selector, Scale slider.
  - **Background Tab**: Color swatches, Custom Hex Picker, Photo picker (`PickVisualMedia`), Scrim tint & Blur sliders.
  - **Content Tab**: Date toggle, Motto input field, Focus streak toggle.
- **3.4 One-Tap System Apply**:
  - Action button to trigger Android's `WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER` intent.

---

### Milestone 4: Android Live Wallpaper Service Engine
**Goal**: Implement the native Android `WallpaperService` for home and lock screen rendering.

- **4.1 `FocusClockWallpaperService.kt`**:
  - Extends `android.service.wallpaper.WallpaperService`.
  - Inner `Engine` managing `SurfaceHolder` callbacks (`onSurfaceCreated`, `onSurfaceChanged`, `onVisibilityChanged`, `onOffsetsChanged`).
- **4.2 Battery-Efficient Animation Loop**:
  - Render thread active only when `isVisible == true`.
  - Dynamic frame-rate throttle: 1 Hz (or sweeping tick if enabled) during screen-on; completely paused (0% CPU) when screen is off.
- **4.3 Lifecycle & Flow Collector**:
  - Collects `FocusPreferencesRepository` flows to trigger immediate re-draws when settings change in the app.

---

### Milestone 5: System Manifest, Settings Navigation & Testing
**Goal**: Integrate with the rest of the app, configure permissions, and test.

- **5.1 Android Manifest Configuration**:
  - Register `<service android:name=".wallpaper.FocusClockWallpaperService" android:permission="android.permission.BIND_WALLPAPER">`.
  - Add wallpaper metadata XML `res/xml/focus_clock_wallpaper.xml`.
  - Declare `<uses-permission android:name="android.permission.SET_WALLPAPER" />`.
  - Declare `<uses-permission android:name="android.permission.SET_WALLPAPER_HINTS" />`.
- **5.2 Settings Hub & Navigation**:
  - Add "Home & Lock Screen Wallpaper" entry in `SettingsHubScreen.kt`.
  - Register route in `AppNavigation.kt`.
- **5.3 Test Coverage & Verification**:
  - Unit tests for clock positioning and analog numeral calculations.
  - Robolectric CUJ tests verifying preference persistence and preview state changes.

---

## 4. Permissions & Play Store Policy Compliance

| Requirement | Implementation Detail | Compliance Rationale |
| :--- | :--- | :--- |
| **Photo Selection** | `ActivityResultContracts.PickVisualMedia` | Uses zero-permission Android Photo Picker. No `READ_EXTERNAL_STORAGE` or `READ_MEDIA_IMAGES` requested. |
| **Live Wallpaper** | `android.permission.BIND_WALLPAPER` | Standard Android system binding for live wallpapers. |
| **Battery Safety** | Visibility-aware render suspension | Halts drawing loop on screen-off / screen-obscured to prevent background battery drain. |

---

## 5. Implementation Readiness

This plan is organized to be executed step-by-step across Milestones 1 to 5.
All changes build additively upon the existing codebase without breaking existing Focus sessions or playback architecture.
