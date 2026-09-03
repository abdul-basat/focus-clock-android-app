# Focus Clock — Minimalist Compact Deck & Inspector Enhancement Plan

## Executive Overview & Architectural Roadmap

This document captures the Senior UI/UX breakdown and phase-wise engineering plan to streamline the Clock Customization Studio's bottom inspector deck into a minimal, compact, tactile, and accurate inspector.

---

## 1. Key UI/UX Issues in Current Bottom Deck

### 1.1 Broken Tab Row Typography (Word Wrapping)
- **Symptom:** Tab names wrap awkwardly across multiple lines on standard screen densities:
  ```
  Positi / on
  Clock / Style
  Back / ground
  Cont / ent
  ```
- **Root Cause:** A fixed 4-column `PrimaryTabRow` squeezes both 16dp icons and text into ~75dp slots, breaking words mid-syllable and creating visual clutter.

### 1.2 Ghost Text Bleed-Through
- **Symptom:** Faint clock numerals, dates (e.g. `WED • SEP 2`), or dial markings bleed through behind the tab row and inspector controls.
- **Root Cause:** Semi-translucent deck surface (translucent alpha or glassmorphism) allows high-contrast clock elements positioned underneath to bleed into controls, creating visual noise and impairing legibility.

### 1.3 Bulky 9-Button Alignment Presets Grid
- **Symptom:** Nine large text buttons ("Top Left", "Top Center", "Top Right", etc.) occupy 3 full rows (~130dp vertical space) before sliders are visible.
- **Root Cause:** Text-heavy button grid layout rather than a dense, intuitive visual matrix.

### 1.4 Excessive Vertical Height (Over 340dp Total)
- **Symptom:** Between the drag handle, minimize button, tab row, and 250dp scroll area, the panel consumes almost 50% of the screen, suffocating the clock preview.
- **Root Cause:** Redundant vertical containers and lack of inline layout compression.

---

## 2. Enhancement Plan: The Minimalist Compact Deck

**Core Objective:** Reduce deck height by **35–40%** and make each sub-screen clean, minimal, tactile, and scannable.

```
┌──────────────────────────────────────────────────────────────────────┐
│                    LIVE PREVIEW (65–75% SCREEN)                     │
│                                                                      │
│                              21:05                                   │
│                        WEDNESDAY, SEP 2                              │
│                                                                      │
│          [Tap anywhere on canvas to dismiss / enter immersion]       │
├──────────────────────────────────────────────────────────────────────┤
│  ═══════════════════════ [Drag Indicator] ══════════════ [−] / [⤢]   │
│  [ (◎) Position ]  [ (🎨) Style ]  [ (🖼) Background ]  [ (★) Badges ]│
├──────────────────────────────────────────────────────────────────────┤
│  INSPECTOR CONTENT AREA (~160–180dp fixed/compact height)           │
│  • Inline Sliders (Label + Value + Slider on single row)             │
│  • Visual 3×3 Figma/Canva Dot Matrix for snapping                    │
│  • Horizontal Single-Line Chips & Swatches                           │
└──────────────────────────────────────────────────────────────────────┘
```

---

### A. Header & Tab Bar Redesign
1. **Compact Pill Tabs (Single Line, No Wrapping):**
   - Replace standard `PrimaryTabRow` with sleek horizontal filter pills/chips (`ScrollableTabRow` or single-line segment chips with `noWrap`).
   - Labels stay strictly on one line: **Position**, **Style**, **Background**, **Content / Badges** with smooth active pill indicators.
2. **Opaque Dark Matte Deck Background:**
   - Deep opaque dark surface (`#161618` with subtle border `#2A2A2E`) to eliminate ghost bleed-through from the clock canvas.
3. **Streamlined Integrated Header:**
   - Merge drag bar and collapse affordance into an integrated compact header bar:
     - Center: Tactile pill drag indicator.
     - Right: Subtle collapse/expand chevron button (`Modifier.size(32.dp)`).
     - Clean, minimal vertical footprint (24dp total header height).

---

### B. Sub-Screen 1: Position Inspector
- **Visual 3×3 Alignment Matrix Widget (Figma/Canva Style):**
  - Replace 9 bulky text buttons with a compact 3×3 visual anchor grid (~48×48dp total size) featuring 9 tactile dot/square nodes.
  - Active node highlights in vibrant accent (e.g. amber/orange `#FF9800`) with quick haptic pulse when tapped.
  - Instantly snaps $X$ and $Y$ offset coordinates to preset anchors (Top-Left, Center, Bottom-Right, etc.).
- **Inline Compact Sliders:**
  - Label, numerical percentage (`-50%` to `+50%`), and slider bar arranged on the same row.
- **Quick Reset Action:**
  - Compact reset icon button placed adjacent to the alignment matrix for rapid re-centering.

---

### C. Sub-Screen 2: Clock Style Inspector
- **Streamlined Style Carousels:**
  - Horizontal chip row for Clock Styles: **Digital**, **Clean**, **Analog**, **Flip**, **Minimal**.
- **Analog Numeral Layout Segmented Control:**
  - Replace full-width vertical cards with a compact segmented selector:
    - `Radial (3-6-9)`
    - `Full 12`
    - `Minimal Dots`
- **Compact Color & Typography Pickers:**
  - Sleek 28dp color dots with crisp selection rings.
  - Single-line horizontal chips for font family selection (`Classic Mono`, `Modern Sans`, `Serif`, `Cyberpunk Tech`).
- **Inline Feature Toggles:**
  - Slim switch row for Seconds Hand / Second Digits display.

---

### D. Sub-Screen 3: Background Inspector
- **3-Option Segmented Pill Selector:**
  - Compact segment: `[ Solid Color | Gallery Photo | Blur ]`.
- **Solid Colors:**
  - Clean horizontal scroll row of curated dark/AMOLED swatches (`#000000`, `#0F172A`, `#1E1B4B`, `#18181B`, `#052E16`, `#1C1917`).
- **Gallery Photo:**
  - Compact "Pick Photo" button with micro-thumbnail preview and slim blur/scrim sliders.

---

### E. Sub-Screen 4: Content & Badges Inspector
- **Compact Toggle Rows:**
  - Slim single-line switch rows with tight 8dp vertical spacing:
    - Show Date (`Wed, Sep 2`)
    - Show Focus Streak
    - Custom Motto
- **Contextual Inline Motto Input:**
  - Smoothly expands an inline compact text field only when Custom Motto is toggled on.

---

### F. Canvas Tap-to-Dismiss / 1-Tap Immersion Mode
- Tapping anywhere on the upper clock canvas automatically minimizes the deck to a slim bottom pill (`~36dp`).
- Gives the user a 100% full-screen unobstructed view of their wallpaper layout and clock scale.
- Tapping the minimized pill smoothly expands the inspector back into view.

---

## 3. Phase-Wise Implementation Breakdown

| Phase | Milestone | Scope of Work |
| :--- | :--- | :--- |
| **Phase 1** | **Surface & Header Overhaul** | • Replace translucent surface with `#161618` solid matte & `#2A2A2E` border.<br>• Implement single-line non-wrapping scrollable pill tabs.<br>• Unify drag handle and collapse chevron into compact 24dp header. |
| **Phase 2** | **Visual 3×3 Position Matrix** | • Build the `Alignment3x3Matrix` Compose widget.<br>• Convert $X$/$Y$ sliders to inline label + value + slider rows.<br>• Add tactile haptic feedback on node tap and snap. |
| **Phase 3** | **Style & Background Compactification** | • Convert Clock Style and Numeral mode selection to horizontal segmented chips.<br>• Replace large palette tiles with 28dp circular swatches.<br>• Compact background picker into 3-pill segment (`Solid`, `Photo`, `Blur`). |
| **Phase 4** | **Content Badges & 1-Tap Immersion** | • Streamline toggle rows (Date, Streak, Motto).<br>• Implement canvas `pointerInput` tap-to-minimize and bottom pill expander.<br>• Run Robolectric and screenshot tests to verify layout stability. |
