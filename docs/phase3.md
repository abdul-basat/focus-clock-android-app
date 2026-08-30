# Phase 3 --- Active Focus Experience

## Updated Implementation Prompt with Competitor Screenshot References

You are continuing development of the existing native Android **Focus Clock** application.

> **IMPORTANT:** Phase 1 and Phase 2 have already been implemented.
>
> Do **not** rebuild the project from scratch. Do **not** replace working Phase 1 or Phase 2 architecture unnecessarily.
>
> This prompt is an updated version of the Phase 3 specification. It incorporates visual lessons from competitor clock-app screenshots that will be attached to this prompt.

------------------------------------------------------------------------

## 1. First: Audit the Existing Project

Before writing code:

1. Inspect the complete project structure.
2. Inspect the Phase 1 and Phase 2 implementations.
3. Identify:
    - FocusSessionManager
    - FocusSession model
    - timer state
    - clock renderer architecture
    - clock styles
    - DataStore/preferences
    - navigation
    - theme
    - existing Home screen
    - existing Start Focus screen
4. Determine how the current application navigates into Focus mode.
5. Reuse existing clock renderers.
6. Reuse existing timer/session state.
7. Do not create duplicate timer logic.
8. Do not create a second clock implementation.
9. Do not create a second DataStore/preferences system.

Before implementation, briefly report:

- What Phase 1 currently contains.
- What Phase 2 currently contains.
- Which files/components will be modified.
- Which architecture improvements are needed.
- Which parts will remain unchanged.

Then proceed with implementation.

------------------------------------------------------------------------

# 2. Competitor Screenshots --- How to Use Them

I am attaching screenshots from an existing clock application that I researched.

These screenshots are **visual and UX references only**.

Study them carefully for:

- large clock typography
- digit proportions
- spacing
- alignment
- visual hierarchy
- flip-clock presentation
- split/folded digit presentation
- minimal digital layouts
- analog clock presentation
- dark/light contrast
- clock positioning
- portrait composition
- landscape composition
- how a clock can become the primary visual element
- how secondary information such as date/day can remain visually subordinate

## Do NOT Copy the Competitor

Do **not** reproduce or imitate the competitor's:

- exact UI
- exact layouts
- exact typography
- exact font treatment
- exact colors
- exact icons
- exact artwork
- exact animations
- exact component shapes
- exact spacing values
- exact branding
- assets
- screenshots
- proprietary visual identity

Do not create a clone.

The screenshots should answer only this question:

> "What makes a clock-focused interface visually polished, readable, calm, and premium?"

Then apply those principles to our own product.

------------------------------------------------------------------------

# 3. Important Product Difference

The competitor is primarily a **clock application**.

Our product is fundamentally different:

**FOCUS CLOCK**

Beautiful Clock\
+\
Focus Timer\
+\
Ambient Audio\
+\
Distraction-Free Environment

The clock is the **hero**, but the purpose is **focus**.

Therefore:

- Do not add World Clock content to Active Focus.
- Do not add weather.
- Do not add multiple clocks.
- Do not add dashboards.
- Do not add statistics.
- Do not add unnecessary widgets.
- Do not allow the player to dominate the screen.

The Active Focus screen must remain extremely minimal.

------------------------------------------------------------------------

# 4. Active Focus Screen --- Product Goal

The Active Focus screen is the most important screen in the entire application.

The user should be able to place the phone:

- on a desk
- beside a laptop
- on a workspace
- on a bedside table
- on a stand
- in an appropriate vehicle mount

and glance at it without being distracted.

The screen should communicate:

1. Current time
2. Focus timer
3. Current track
4. Minimal playback controls
5. Background

Nothing else should compete for attention.

The design principle is:

> **Everything that is not necessary for focus should disappear.**

It should feel like a premium ambient digital clock rather than a traditional timer application.

------------------------------------------------------------------------

# 5. Visual Hierarchy

The visual priority must be:

1. **TIME**
2. **FOCUS TIMER**
3. **DATE / DAY**
4. **CURRENT TRACK**
5. **PLAYBACK CONTROLS**

The clock must immediately attract the user's eye.

The date/day must never compete with the clock.

The timer must be clearly identifiable but secondary.

The track title should be subtle.

Playback controls should be visually quiet but easy to touch.

------------------------------------------------------------------------

# 6. Active Focus Content

The screen should contain these primary elements:

- Clock
- Date/day
- Focus timer
- Current track area
- Subtle waveform
- Playback controls
- Background

The actual Media3 player will be implemented in Phase 4.

Therefore, create a clean player abstraction/placeholder in this phase so Phase 4 can connect real playback without redesigning the Active Focus screen.

------------------------------------------------------------------------

# 7. Portrait Layout

Create a dedicated portrait Active Focus composition.

Do NOT simply reuse the Home layout.

Do NOT simply stretch the Start Focus screen.

Conceptually:

```text
                 07
                 15

            Thu · Aug 27

                47:23


             Deep Focus

          ▁▂▃▅▆▃▂▅▇▅▃▂▁

          ◀   ▶   ■   ▶   ↻
```

This is a conceptual reference, not a pixel-perfect specification.

The actual design should be more polished.

## Upper Area

Keep it quiet.

Do not show:

- app title
- large toolbar
- navigation bar
- dashboard controls

## Center Area

The clock must dominate.

For the Clean Digital style:

```text
07
15
```

Hour above minute.

No colon.

Position the clock around the visual center, but account for the timer and player below it.

Do not mathematically center it if that creates poor visual balance.

## Below Clock

Show:

```text
Thu · Aug 27
```

Then:

```text
47:23
```

The timer should remain clearly smaller than the clock.

## Bottom Area

Reserve space for:

- track title
- waveform
- playback controls

Keep the player visually quiet.

------------------------------------------------------------------------

# 8. Clock Style Integration

Use the Clock Style Engine created in Phase 2.

Do NOT duplicate clock rendering.

The Active Focus screen should simply render the currently selected clock renderer.

Expected existing styles may include:

- Clean Digital
- Flip
- Minimal
- Analog

If these already exist, integrate them without rebuilding them.

------------------------------------------------------------------------

# 9. Clean Digital Clock

The primary digital representation should be:

```text
07
15
```

not:

```text
07:15
```

Requirements:

- large typography
- modern sans-serif
- highly readable
- medium/semi-bold weight
- tight vertical spacing
- strong alignment
- premium proportions

Do not use one fixed huge font size for all devices.

The clock must adapt to available width and height.

------------------------------------------------------------------------

# 10. Flip Clock

If the Flip Clock renderer exists from Phase 2, integrate it naturally.

Use the competitor screenshots only to understand the visual qualities of a good flip clock:

- strong digit scale
- split panels
- central divider
- subtle surface contrast
- balanced rounded corners
- readable digits
- restrained styling

Do NOT copy the competitor's exact appearance.

Do not introduce complicated 3D mechanical animation in this phase.

The result should feel like **our own Focus Clock flip style**.

------------------------------------------------------------------------

# 11. Minimal Clock

The Minimal style should have almost no decoration.

Conceptually:

```text
07
15
```

directly on the background.

No card.

No unnecessary border.

No unnecessary shadow.

No decorative container unless the selected theme genuinely needs one.

This should be one of the strongest styles for AMOLED black backgrounds.

------------------------------------------------------------------------

# 12. Analog Clock

If the Analog renderer exists from Phase 2:

- integrate it
- keep it independent
- use an adaptive square area
- keep it visually dominant
- avoid excessive decoration

Do not force an analog clock into the digital clock layout.

------------------------------------------------------------------------

# 13. Date and Day

Use the existing date formatter from Phase 1/2.

Do not duplicate date formatting logic.

Default visual format:

```text
Thu · Aug 27
```

or the equivalent existing format.

Typography:

- approximately 14--18sp
- regular/medium
- reduced visual emphasis
- sufficient contrast

The date must remain clearly subordinate to the clock.

------------------------------------------------------------------------

# 14. Focus Timer

Use the existing **FocusSessionManager** from Phase 2.

Do NOT implement another timer.

Countdown:

```text
47:23
```

Elapsed:

```text
01:24:18
```

Unlimited:

```text
01:24:18
```

The timer is secondary.

Recommended portrait position:

```text
Clock

Date

Timer
```

Do not put the timer directly beside the clock in portrait.

------------------------------------------------------------------------

# 15. Timer Typography

Starting range:

- 16--24sp
- regular or medium
- readable
- clearly smaller than clock

Adapt the final size according to device dimensions.

Do not make the timer look like the primary clock.

------------------------------------------------------------------------

# 16. Screen Awake

When Active Focus starts:

Keep the screen awake.

Use the appropriate modern Android Activity/window mechanism.

Prefer the window-level keep-screen-on approach.

Do NOT modify the user's global system screen-timeout setting.

Do NOT permanently change device brightness.

When Focus ends:

- remove temporary keep-screen-on behavior
- restore normal application behavior

Respect the existing Phase 2 setting if one exists.

Do not add unnecessary permissions just to keep the screen awake.

------------------------------------------------------------------------

# 17. Immersive Experience

Active Focus should use the available display area intelligently.

Avoid a traditional:

- toolbar
- bottom navigation
- app title
- persistent settings button

during Focus.

Use current Android edge-to-edge/window inset APIs.

Do not use deprecated system UI techniques unnecessarily.

Do not request `USE_FULL_SCREEN_INTENT`.

This is a normal Activity-based focus experience.

------------------------------------------------------------------------

# 18. System Bars and Safe Areas

Handle:

- status bar insets
- navigation bar insets
- gesture navigation
- 3-button navigation
- display cutouts
- rounded display corners

The clock and controls must not be hidden under unsafe system areas.

The UI should remain visually balanced even when system bars are present.

------------------------------------------------------------------------

# 19. User Exit

The user must have a reliable way to exit Focus.

However, the exit control should not permanently clutter the screen.

Recommended:

Single tap on the screen:

- reveal minimal controls

When controls are visible, expose a subtle:

**END FOCUS**

action.

Back button/back gesture should trigger deliberate exit handling rather than accidentally terminating a session.

------------------------------------------------------------------------

# 20. Control Visibility

Use two visual states.

## Calm State

Mostly show:

- Clock
- Date
- Timer
- Track

Controls should be hidden or very subtle.

## Interaction State

When the user taps:

- playback controls become visible
- End Focus becomes visible

After approximately 4--6 seconds of inactivity:

- controls fade back to the calm state

Make the delay easy to change later.

Do not create a separate timer that conflicts with the FocusSessionManager.

------------------------------------------------------------------------

# 21. Touch Behavior

Single tap on empty Active Focus area:

- toggle/reveal controls

Do NOT:

- pause music when tapping the clock
- change clock style
- open Settings
- navigate Home
- end Focus

Active Focus should remain stable.

------------------------------------------------------------------------

# 22. Exit Confirmation

When End Focus is selected:

Show:

**End Focus?**

**Your current session will end.**

Actions:

**KEEP FOCUSING**

**END SESSION**

Do not use an aggressive red UI.

The destructive action can be visually distinguishable without making the entire dialog alarming.

------------------------------------------------------------------------

# 23. Player Placeholder

Create a reusable:

`FocusPlayerArea`

component.

It should be ready to receive future:

- track title
- artist
- playback state
- progress
- play/pause
- stop
- previous
- next
- loop
- waveform state

For this phase, mocked/static state is acceptable.

Do not create fake Media3 business logic.

The Phase 4 Media3 implementation should be able to connect to this area cleanly.

------------------------------------------------------------------------

# 24. Player Visual Design

The player must remain secondary.

Conceptually:

```text
             Deep Focus

          ▁▂▃▅▆▃▂▅▇▅▃▂▁

          ◀   ▶   ■   ▶   ↻
```

Do not copy this exact visualization.

Use:

- calm spacing
- restrained contrast
- small track typography
- subtle waveform
- clear primary Play/Pause

Play/Pause visual size:

approximately 48--56dp.

Secondary control touch targets:

approximately 48dp.

The visual icons may be smaller inside their touch targets.

------------------------------------------------------------------------

# 25. Playback Control Order

Use this logical order:

Previous\
Play/Pause\
Stop\
Next\
Loop

Play/Pause has the strongest visual emphasis.

Loop has selected/unselected states.

Actual audio behavior belongs to Phase 4.

------------------------------------------------------------------------

# 26. Waveform Placeholder

Create a lightweight reusable waveform component.

Support future states:

- PLAYING
- PAUSED
- STOPPED

For now:

- static waveform is acceptable
- or use a very lightweight calm animation

Avoid CPU-heavy animation.

Do not make the waveform brighter or more prominent than the clock.

------------------------------------------------------------------------

# 27. Portrait Responsiveness

Do not use fixed pixel coordinates.

Avoid code such as:

```text
y = 350px
```

or giant hardcoded top paddings.

Prefer:

- BoxWithConstraints
- adaptive dimensions
- weight
- Arrangement
- alignment
- safe insets
- responsive typography

The layout must work on:

- compact phones
- normal phones
- large phones
- tablets

------------------------------------------------------------------------

# 28. Landscape Layout

Create a dedicated landscape composition.

Do NOT simply rotate the portrait layout.

Suggested conceptual arrangement:

```text
LEFT / CENTER

        07
        15

        Thu · Aug 27

           47:23


RIGHT / LOWER

        Deep Focus

     ▁▂▃▅▆▃▂▅▇▅▃▂▁

      ◀   ▶   ■   ▶   ↻
```

This is conceptual.

Use the actual available dimensions to create the best composition.

------------------------------------------------------------------------

# 29. Landscape Rules

Priority:

1. Clock
2. Timer
3. Music

Do not make the clock vertically enormous.

Avoid wasting vertical space.

Controls must remain touch friendly.

Support:

- short landscape heights
- wide phones
- tablets
- gesture navigation
- 3-button navigation

------------------------------------------------------------------------

# 30. Responsive Testing

Test at minimum:

- 320dp-class compact phone
- normal phone
- large phone
- portrait
- landscape
- tablet

The UI must never:

- clip clock digits
- overlap timer
- overlap player
- hide controls
- create horizontal scrolling
- push End Focus outside the visible area
- create unexpected vertical scrolling

------------------------------------------------------------------------

# 31. Typography

Maintain a clear hierarchy.

TIME: Largest element.

DATE: Secondary.

TIMER: Secondary but stronger than date.

TRACK: Small.

CONTROLS: Interactive.

Starting values:

DATE: 14--18sp

TIMER: 16--24sp

TRACK: 14--18sp

These are starting points, not rigid specifications.

Use the existing typography system from Phase 1/2 if it is good.

------------------------------------------------------------------------

# 32. Background Architecture

Phase 5 will implement the full background system.

For this phase:

- use the existing background implementation
- default to AMOLED black
- keep background rendering independent from content

Conceptually:

```text
Box
 ├── Background
 ├── Clock
 ├── Date
 ├── Timer
 ├── Player
 └── Controls
```

Do not mix background logic into the clock renderer.

Phase 5 should later be able to add:

- solid color
- image
- slideshow
- shuffle
- intervals
- crossfade
- overlays

without rewriting the Active Focus layout.

------------------------------------------------------------------------

# 33. Background Contrast

Support:

- light text
- dark text
- future background overlay

Do not permanently hardcode white text.

Default:

AMOLED black + near-white text.

------------------------------------------------------------------------

# 34. No Unnecessary UI

Do NOT add:

- weather
- World Clock
- multiple clocks
- statistics
- productivity scores
- motivational quotes
- battery information
- Wi-Fi information
- notification feeds
- app lists
- social features
- advertisements

The Active Focus screen must remain quiet.

------------------------------------------------------------------------

# 35. Screen Transition

When navigating from Start Focus to Active Focus:

Use a subtle transition.

Recommended:

- fade
- very subtle scale

Avoid:

- bouncing
- spinning
- flashy zoom
- long transitions

The transition should feel calm.

------------------------------------------------------------------------

# 36. Time Updates

The clock must update accurately.

Do not recreate the entire screen unnecessarily every second.

Use the existing time state from Phase 1/2.

Ensure:

- minute changes update
- date changes at midnight
- day changes correctly
- 12-hour mode works
- 24-hour mode works
- AM/PM works

Do not display seconds unless a clock style specifically supports them.

------------------------------------------------------------------------

# 37. Midnight Behavior

Test:

`23:59 → 00:00`

At midnight:

- clock updates
- date updates
- day updates
- focus timer continues independently

The date must not remain stale until Activity recreation.

------------------------------------------------------------------------

# 38. Timer Independence

System clock and Focus Timer are independent.

Example:

At:

```text
23:59
```

the Focus Timer may show:

```text
12:45
```

After midnight:

```text
00
00
```

while the Focus Timer remains:

```text
12:45
```

This separation must be preserved.

------------------------------------------------------------------------

# 39. Lifecycle

Test:

- Active Focus → background → return
- rotation
- Activity recreation
- Back navigation
- screen off/on if practical
- process lifecycle where possible

The session timer must not reset.

Do not create multiple timer jobs.

The Active Focus UI must reconstruct from existing session state.

------------------------------------------------------------------------

# 40. Orientation

Respect the Phase 2 orientation configuration if it exists.

Possible modes:

- Auto
- Portrait
- Landscape

When Auto:

Follow device orientation.

When Portrait:

Lock Focus Activity to portrait.

When Landscape:

Lock Focus Activity to landscape.

Do not globally alter orientation outside the Focus Activity.

------------------------------------------------------------------------

# 41. Brightness

Do not implement complex brightness controls in this phase unless already present.

If Phase 2 has temporary brightness behavior:

Integrate it without rewriting it.

Any temporary brightness change must be scoped to the Focus Activity.

When Focus ends, normal behavior must return.

------------------------------------------------------------------------

# 42. Accessibility

Even though this is a minimal screen:

- important controls need content descriptions
- buttons need meaningful labels
- touch targets should be approximately 48dp or larger
- text must have sufficient contrast
- TalkBack should identify important controls
- controls must not depend only on color
- larger system font settings should not catastrophically break the layout

Do NOT use AccessibilityService.

------------------------------------------------------------------------

# 43. OEM Compatibility

Be careful with Android variations from:

- Samsung
- Xiaomi
- Tecno
- Infinix
- other Android manufacturers

Do not rely on OEM-specific behavior.

The Active Focus screen should use standard Android APIs.

------------------------------------------------------------------------

# 44. Performance

The screen may remain visible for hours.

Therefore:

- avoid unnecessary recompositions
- avoid continuous CPU loops
- avoid heavy animation
- avoid unnecessary object allocation
- avoid memory leaks
- avoid Activity leaks
- use lifecycle-aware state collection
- do not create multiple timers

Clock updates should be lightweight.

------------------------------------------------------------------------

# 45. Battery

The screen intentionally remains awake during Focus.

That is expected.

However:

- do not use high-frequency CPU loops
- update displayed time approximately once per second or at appropriate boundaries
- keep animations lightweight

------------------------------------------------------------------------

# 46. Settings Integration

Do NOT build the complete Settings screen in this phase.

However, Active Focus should read existing configuration where available.

Potential settings include:

- clock style
- timer visibility
- date visibility
- player visibility
- waveform visibility
- background
- orientation
- screen awake
- brightness
- control visibility

If a setting does not yet exist:

- use a sensible default
- do not scatter hardcoded temporary logic throughout the UI

Phase 6 will complete Settings.

------------------------------------------------------------------------

# 47. Component Architecture

Use reusable components.

Conceptually:

```text
ActiveFocusScreen
 ├── FocusBackground
 ├── ActiveFocusClock
 ├── FocusDate
 ├── FocusTimer
 ├── FocusPlayerArea
 │    ├── TrackTitle
 │    ├── Waveform
 │    └── PlaybackControls
 └── FocusExitControl
```

Do not create one enormous Composable.

------------------------------------------------------------------------

# 48. Active Focus UI State

Create an `ActiveFocusUiState` if one does not already exist.

It may contain:

- current time
- date
- clock style
- timer display
- timer mode
- session state
- controls visible
- track information
- playback state
- background state
- exit dialog visibility

Do not duplicate domain state unnecessarily.

------------------------------------------------------------------------

# 49. Active Focus ViewModel

If one does not exist, create an `ActiveFocusViewModel`.

Responsibilities:

- expose Active Focus UI state
- observe FocusSessionManager
- manage temporary control visibility
- manage auto-hide
- manage exit confirmation state

Do not put timer business logic in the ViewModel if FocusSessionManager already owns it.

------------------------------------------------------------------------

# 50. Auto-Hide

When the user interacts:

```text
controlsVisible = true
```

Start/restart a delayed hide job.

After approximately 4--6 seconds:

```text
controlsVisible = false
```

On every new interaction:

- cancel the previous hide job
- restart it

Ensure only one active hide job exists.

Cancel the job when the screen is disposed.

------------------------------------------------------------------------

# 51. Final Visual Target

The conceptual target is:

```text
                 07
                 15

            Thu · Aug 27

                47:23


             Deep Focus

          ▁▂▃▅▆▃▂▅▇▅▃▂▁

          ◀   ▶   ■   ▶   ↻
```

with a beautiful, quiet background.

Again, this is not a pixel-perfect specification.

The actual UI should be original and more polished.

------------------------------------------------------------------------

# 52. Do Not Overdesign

Do NOT add:

- excessive glassmorphism
- excessive gradients
- excessive shadows
- bright neon controls
- huge floating cards
- giant player panels
- unnecessary borders
- large "Focus Mode" title
- decorative dashboards

The premium feeling must come from:

- typography
- spacing
- alignment
- proportions
- contrast
- subtle animation
- consistency

Empty space is intentional.

------------------------------------------------------------------------

# 53. Small Screen Priority

If available space becomes constrained, preserve this priority:

1. Clock
2. Timer
3. Date
4. Track
5. Controls

Do not shrink the clock into insignificance just to preserve every secondary element.

On short landscape screens:

- reduce spacing
- reduce visual sizes where appropriate
- maintain touch targets

------------------------------------------------------------------------

# 54. Large Screen Priority

On tablets/large phones:

Do not simply enlarge everything.

Use maximum visual sizes where appropriate.

Use additional space to create calmness.

The design should feel elegant, not oversized.

------------------------------------------------------------------------

# 55. Permissions

Do not add unnecessary permissions in this phase.

The Active Focus screen does NOT require:

- VPN
- AccessibilityService
- Device Owner
- exact alarm permission
- full-screen notification permission
- broad storage permission
- microphone
- location

Do not introduce any of these.

------------------------------------------------------------------------

# 56. No App Blocking

Absolutely do not implement app blocking in Phase 3.

Do not implement:

- VPN
- AccessibilityService
- Device Owner
- package suspension
- process killing
- background restriction hacks

The future RestrictionEngine remains a separate concern.

------------------------------------------------------------------------

# 57. No Music Implementation Yet

Do not implement the real Media3 player in this phase.

Phase 4 will handle:

- Media3
- ExoPlayer
- MediaSession
- MediaSessionService
- background playback
- playback notification
- MediaController
- real controls
- real waveform integration

The current player should be an integration-ready placeholder.

------------------------------------------------------------------------

# 58. Prepare for Phase 4

Make sure `FocusPlayerArea` can later receive real:

- playback state
- track metadata
- progress
- controls
- waveform state

Do not tightly couple it to fake data.

The Phase 4 implementation should not require rewriting the entire Active Focus layout.

------------------------------------------------------------------------

# 59. Prepare for Phase 5

The next phase will add:

- Photo Picker
- solid colors
- image backgrounds
- multiple images
- slideshow
- shuffle
- intervals
- crossfade
- overlays

Therefore:

- background must be an independent layer
- content must remain independent
- text contrast must be adaptable

Do not mix image/background logic into the clock renderer.

------------------------------------------------------------------------

# 60. Prepare for Phase 6

Settings will eventually control:

- clock style
- timer visibility
- date visibility
- player visibility
- waveform visibility
- background
- orientation
- screen awake
- brightness
- auto-hide behavior

Do not hardcode these values throughout Active Focus.

------------------------------------------------------------------------

# 61. Build and Runtime Testing

After implementation:

1. Build the application.
2. Fix all compilation errors.
3. Launch the application.
4. Start a Focus Session.
5. Verify navigation into Active Focus.
6. Verify current time.
7. Verify hour above minute.
8. Verify date/day.
9. Verify countdown.
10. Verify elapsed mode.
11. Verify Unlimited mode.
12. Verify selected clock style.
13. Verify every Phase 2 clock style that exists.
14. Verify portrait.
15. Verify landscape.
16. Verify screen remains awake.
17. Tap the screen and verify controls appear.
18. Verify controls auto-hide.
19. Verify End Focus.
20. Verify exit confirmation.
21. Verify timer is not reset by rotation.
22. Verify timer is not reset by Activity recreation.
23. Verify midnight behavior if practical.
24. Verify compact-screen layout.
25. Verify large-screen layout.
26. Verify no overlap or clipping.
27. Verify no horizontal scrolling.
28. Verify no obvious timer/coroutine leaks.

If actual device testing is unavailable, clearly distinguish:

- static code inspection
- emulator testing
- physical-device testing

Never claim physical-device testing if it was not performed.

------------------------------------------------------------------------

# 62. Visual QA --- Especially Important

Perform a dedicated visual review.

Review:

- clock alignment
- clock size
- digit proportions
- hour/minute spacing
- date spacing
- timer hierarchy
- player spacing
- waveform scale
- playback control spacing
- safe-area handling
- portrait balance
- landscape balance
- compact-screen behavior
- large-screen behavior

Compare the result conceptually against the attached competitor screenshots.

Ask:

> Does our clock have the same level of visual confidence and readability?

Then ask:

> Does our screen feel like a Focus environment rather than a clock clone?

If the first answer is no, improve typography, spacing, proportions, and alignment.

If the second answer is no, remove unnecessary UI.

------------------------------------------------------------------------

# 63. Originality Check

Before completing this phase, perform a deliberate originality review.

The implementation must NOT appear to be a clone of the competitor.

The inspiration should be visible only in general design quality:

- strong clock hierarchy
- excellent readability
- polished typography
- thoughtful spacing
- minimal presentation

Our visual identity should remain distinct.

------------------------------------------------------------------------

# 64. Code Quality

Do not:

- duplicate clock code
- duplicate timer code
- duplicate DataStore
- use global mutable state
- use fixed pixel positioning
- use deprecated APIs unnecessarily
- create unnecessary dependencies
- put business logic inside Composables
- leak Activity references
- launch unmanaged coroutines
- create multiple timers for one session

Prefer:

- existing project architecture
- ViewModel
- StateFlow
- lifecycle-aware collection
- reusable Composables
- adaptive layouts
- theme-based colors
- immutable UI state
- testable state

------------------------------------------------------------------------

# 65. Scope Limit

This phase is ONLY for the Active Focus experience.

Do NOT automatically implement:

- Media3
- real music playback
- full waveform analysis
- Photo Picker
- image slideshow
- background editor
- complete Settings
- app blocking
- Device Owner
- AccessibilityService
- VPN
- Focus History
- analytics
- cloud sync

Create clean interfaces/placeholders where necessary.

------------------------------------------------------------------------

# 66. Success Criteria

Phase 3 is complete only when:

1. Active Focus is a dedicated immersive screen.
2. Clock is the dominant visual element.
3. Hour is above minute.
4. Date/day is secondary.
5. Focus Timer is visible but secondary.
6. Countdown works using Phase 2 session state.
7. Elapsed/Unlimited mode works.
8. Existing clock styles integrate correctly.
9. Portrait has a dedicated composition.
10. Landscape has a dedicated composition.
11. The layout is responsive.
12. Screen remains awake during Focus.
13. Controls can be shown/hidden.
14. Controls auto-hide after inactivity.
15. Exit requires deliberate confirmation.
16. Player area is ready for Media3.
17. Waveform area is ready for Phase 4.
18. Background is an independent layer.
19. No unnecessary UI exists.
20. No VPN/accessibility/device-owner/app-blocking functionality was added.
21. No unnecessary permissions were added.
22. Existing Phase 1/2 functionality remains stable.
23. Timer survives normal Activity recreation/orientation changes.
24. Code is modular and maintainable.
25. The screen feels like a premium Focus Clock.
26. The result is visually inspired by high-quality clock apps but is clearly original.
27. The competitor screenshots have influenced quality and visual thinking, not copied design.

------------------------------------------------------------------------

# 67. Phase Completion Report

When finished, report:

1. Phase 1/2 architecture inspected.
2. Files created.
3. Files modified.
4. Active Focus architecture.
5. Portrait implementation.
6. Landscape implementation.
7. Clock integration.
8. Timer integration.
9. Screen-awake implementation.
10. Control visibility/auto-hide.
11. Exit confirmation.
12. Player placeholder architecture.
13. Background abstraction.
14. Permissions added/changed.
15. Build result.
16. Runtime tests performed.
17. Visual QA performed.
18. Known limitations.
19. Any technical decisions Phase 4 needs to know.
20. Any places where the attached competitor screenshots influenced the design.

Then **STOP**.

Do not automatically implement Phase 4.

Wait for the next instruction.

------------------------------------------------------------------------

## Final Product Direction

Always keep this mental model while implementing:

**Focus Clock is not a clock app with a timer added to it.**

It is a:

**FOCUS ENVIRONMENT**

with the clock as its visual center.

The final Active Focus experience will eventually combine:

**CLOCK**\
+\
**FOCUS TIMER**\
+\
**MUSIC**\
+\
**WAVEFORM**\
+\
**AMBIENT BACKGROUND**

inside one extremely minimal, calm, distraction-free screen.
