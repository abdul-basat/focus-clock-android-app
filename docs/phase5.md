# Focus Clock --- Phase 5 Plan & Architecture

## Background Engine, Photos, Solid Colors, Slideshow & Smooth Transitions

This document details the architecture, design, and implementation plan for Phase 5 of the native Android **Focus Clock** application.

---

## 1. Architectural Strategy

Phase 5 introduces a decoupled **Background Engine** that operates independently from the Clock Engine, Focus Session Engine, and Media Engine.

```
Focus Clock Architecture
│
├── Clock Engine
│   ├── Clean Digital
│   ├── Flip Clock
│   ├── Minimal Digital
│   └── Analog Minimal
│
├── Focus Session Engine
│   ├── Countdown
│   ├── Elapsed
│   └── Unlimited
│
├── Media Engine (Media3)
│   ├── FocusPlaybackService
│   ├── ExoPlayer
│   └── FocusPlayerManager
│
├── Background Engine (Phase 5)
│   ├── Solid Color Background (Curated neutrals, calms, accents + Custom HEX)
│   ├── Single Image Background (Android Photo Picker)
│   ├── Slideshow Background (Multi-photo picker, interval, shuffle)
│   ├── Smooth Crossfade Transition (Lightweight 2-layer crossfade)
│   ├── Readability Dim Overlay (0% to 70% dimming)
│   └── Safe Image Persistence & Content URI Handling
│
├── Active Focus UI Layer
│   ├── Background Layer (FocusBackground)
│   ├── Readability Dim Overlay
│   ├── Hero Clock
│   ├── Date & Day of Week
│   ├── Focus Timer
│   ├── Waveform & Track Area
│   └── Auto-hiding Minimal Controls
│
└── Settings & Configuration
    ├── Focus Session Duration & Modes
    ├── Hero Clock Style Selector
    ├── Background Configuration Screen & Live Preview
    └── Ambient Sound & Playback
```

---

## 2. Core Functional Requirements

1. **Default Experience:**
   - AMOLED Pure Black (`#000000`) remains default.
   - Zero visual distraction on OLED screens.

2. **Solid Color Backgrounds:**
   - Curated color palettes:
     - **Neutrals:** Pure Black (`#000000`), Charcoal (`#1A1A1E`), Slate (`#242830`), Warm Gray (`#2C2A29`), Soft White (`#F0F0F2`)
     - **Calm:** Deep Navy (`#0D1B2A`), Forest Green (`#0F251E`), Deep Teal (`#0B252C`), Muted Plum (`#241628`), Earth Brown (`#251D18`)
     - **Accent:** Midnight Indigo (`#161B33`), Dark Cypress (`#13221B`), Burnt Ember (`#2A150A`)
   - Custom HEX color input with validation and real-time preview.

3. **Single Image Background:**
   - Android System Photo Picker (`ActivityResultContracts.PickVisualMedia`).
   - Center crop scaling (`ContentScale.Crop`).
   - Persist URI in DataStore safely.
   - Graceful fallback to pure black if URI is missing or deleted.

4. **Multi-Image Slideshow:**
   - Multi-photo picker (`ActivityResultContracts.PickMultipleVisualMedia`).
   - Thumbnail collection with delete action.
   - Interval settings: 5s, 15s, 30s, 1m, 5m, 10m.
   - Optional Shuffle toggle.
   - Cinematic smooth crossfade transition (700-1000ms duration).
   - Low memory footprint: at most 2 image layers in memory during transitions.
   - Slideshow lifecycle bound to UI visibility; no background service or wake locks.

5. **Readability Dim Overlay:**
   - Adjustable black overlay from 0% to 70%.
   - Guarantees high-contrast readability for white clock numerals even on bright/colorful wallpapers.

6. **Live Preview Component:**
   - Real-time preview inside Background Settings showing the active clock style, date, timer, background, and dim overlay.

7. **Zero Regression:**
   - Active Focus session timer, clock updates, and Media3 audio playback remain completely uninterrupted when changing background settings or during slideshow image transitions.
