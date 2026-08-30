# Focus Clock — Phase 6

## Settings Hub, Focus Configuration, Audio & Background Controls, Profiles and UX Consolidation

You are continuing development of the existing native Android **Focus
Clock** application.

IMPORTANT:

Phase 1, Phase 2, Phase 3, Phase 4, Phase 4.5, and Phase 5 have already
been implemented.

The application now has:

-   Focus Session functionality
-   Countdown / Elapsed / Unlimited modes
-   Multiple clock styles
-   Active Focus screen
-   Media3 / ExoPlayer playback
-   MediaSession and background audio
-   Playback controls
-   Background Engine
-   Solid color backgrounds
-   User-selected images
-   Slideshow
-   Crossfade
-   Background dimming
-   AMOLED dark visual system
-   Responsive portrait and landscape layouts

This phase is about **product-level settings consolidation and
configuration UX**.

Do NOT rebuild the application.

Do NOT replace working architecture unnecessarily.

Do NOT implement app blocking, Device Owner, AccessibilityService, VPN,
or system-level restrictions.

Do NOT add a backend, account system, cloud sync, analytics, or
advertisements.

The objective is to make the application feel complete and coherent by
bringing all configurable features into a well-designed Settings / Focus
configuration experience.

------------------------------------------------------------------------

# 1. FIRST: FULL PROJECT AUDIT

Before making changes:

1.  Inspect the entire existing project.
2.  Inspect all Phase 1–5 screens.
3.  Inspect current navigation.
4.  Inspect DataStore/preferences.
5.  Inspect Clock Engine.
6.  Inspect FocusSessionManager.
7.  Inspect Media3 architecture.
8.  Inspect Background Engine.
9.  Inspect existing Focus Session screen.
10. Inspect existing Clock Style screen.
11. Inspect existing Background settings.
12. Inspect existing Audio settings/player configuration.

Identify duplicate settings.

Identify settings currently stored in multiple places.

Identify hardcoded configuration.

Then report briefly:

-   Current settings architecture.
-   Existing screens that can be reused.
-   Duplicate/overlapping settings.
-   Settings that need consolidation.
-   Files that will be modified.
-   Files that will be created.

Then implement the phase.

------------------------------------------------------------------------

# 2. PRODUCT GOAL

The user should be able to configure the entire Focus Clock experience
without becoming overwhelmed.

The Settings experience should answer:

> How long do I want to focus?

> How should the clock look?

> What should the background look like?

> What sound should play?

> What happens during Focus?

The Settings experience should NOT feel like an Android system settings
clone.

It should feel like a calm creative control panel.

------------------------------------------------------------------------

# 3. SETTINGS INFORMATION ARCHITECTURE

Use a simple hierarchy:

``` text
Settings
│
├── Focus
│   ├── Duration
│   ├── Timer Mode
│   └── Completion behavior
│
├── Clock
│   ├── Clock Style
│   ├── 12/24 hour format
│   ├── Date format
│   └── Clock appearance
│
├── Background
│   ├── Type
│   ├── Color
│   ├── Images
│   ├── Slideshow
│   ├── Dim
│   └── Transition
│
├── Ambient Sound
│   ├── Track
│   ├── Auto Play
│   ├── Loop
│   └── Volume
│
└── General
    ├── Keep Screen Awake
    ├── Controls Auto-Hide
    └── About
```

Do not necessarily expose every low-level option immediately.

Use progressive disclosure.

------------------------------------------------------------------------

# 4. SETTINGS HOME

Create or refine a Settings Hub.

Suggested structure:

``` text
Settings

Focus
25 min · Countdown

Clock
Clean Digital

Background
AMOLED Pure Black

Ambient Sound
Deep Focus

General
Screen stays awake during Focus
```

Each section should be a compact row/card.

Avoid giant cards.

Avoid excessive descriptions.

------------------------------------------------------------------------

# 5. SETTINGS VISUAL STYLE

Use the established app design language:

-   AMOLED/dark background
-   near-white primary text
-   muted secondary text
-   restrained accent
-   rounded surfaces where appropriate
-   clean typography
-   generous spacing
-   minimal borders
-   consistent icons

Do not introduce a new color system.

------------------------------------------------------------------------

# 6. SETTINGS ROW

A standard settings row should contain:

``` text
Icon

Title
Short current value

Chevron
```

Example:

``` text
⌛  Focus
    25 min · Countdown                         >
```

The current value is important.

The user should not need to open a screen just to remember what is
selected.

------------------------------------------------------------------------

# 7. FOCUS SETTINGS

Create/refine a Focus settings screen.

Support:

-   25 min
-   45 min
-   60 min
-   90 min
-   2 hours
-   Custom
-   Unlimited

Use the existing FocusSessionManager.

Do not create a second timer system.

------------------------------------------------------------------------

# 8. CUSTOM DURATION

Custom duration should provide a simple picker.

Allow sensible focus durations.

Prevent:

-   zero duration
-   negative duration
-   unreasonable overflow

Unlimited should remain a distinct option.

------------------------------------------------------------------------

# 9. TIMER DISPLAY MODE

Support:

``` text
Countdown
Elapsed
```

Explain concisely.

Countdown:

``` text
Counts down from the target duration.
```

Elapsed:

``` text
Counts upward from zero.
```

Do not use technical terminology.

------------------------------------------------------------------------

# 10. UNLIMITED MODE

Unlimited means:

-   no automatic countdown completion
-   elapsed time may still be displayed
-   user ends Focus manually

Do not treat Unlimited as a very large countdown.

------------------------------------------------------------------------

# 11. COMPLETION BEHAVIOR

If appropriate to the existing architecture, provide:

``` text
When Focus Ends
    Stop Sound
    Pause Sound
```

Keep the options simple.

Default should be sensible.

Do not create complicated automation rules.

------------------------------------------------------------------------

# 12. CLOCK SETTINGS

Clock section should expose existing clock styles:

``` text
Clean Digital
Flip Clock
Minimal Digital
Analog Minimal
```

Use the existing clock renderer.

Do not duplicate rendering code.

------------------------------------------------------------------------

# 13. CLOCK STYLE SCREEN

Improve the existing Clock Style screen rather than creating a second
implementation.

Each style should have:

-   live/current-time preview
-   style name
-   short description
-   selected state

The preview should use actual current time where practical.

Do not use stale hardcoded times.

------------------------------------------------------------------------

# 14. CLOCK STYLE DESCRIPTIONS

Use concise descriptions.

Examples:

``` text
Clean Digital
Stacked hours and minutes.

Flip Clock
Classic split-flap inspired display.

Minimal Digital
Ultra-clean digital typography.

Analog Minimal
Simple modern analog clock.
```

Do not claim the Flip Clock is a literal mechanical simulation unless it
actually is.

------------------------------------------------------------------------

# 15. 12/24-HOUR FORMAT

Provide:

``` text
12-hour
24-hour
System Default
```

If the existing clock architecture already follows the system setting,
preserve that behavior.

Do not break locale handling.

------------------------------------------------------------------------

# 16. DATE FORMAT

Keep date customization simple.

Possible options:

``` text
Thu · Aug 27
Thursday · Aug 27
27 Aug · Thu
```

If more options are implemented, keep them curated.

Do not create a complex date-format editor.

------------------------------------------------------------------------

# 17. LOCALE

Use the device locale for:

-   day names
-   month names
-   number formatting where appropriate

Do not hardcode English internally.

The default UI language can remain the existing product language.

Do not introduce a full translation system in this phase unless it
already exists.

------------------------------------------------------------------------

# 18. BACKGROUND SETTINGS

Integrate the Phase 5 Background Engine into the Settings architecture.

Do not create a second background system.

Expose:

``` text
Background
    AMOLED Pure Black
```

and allow the user to enter the detailed Background screen.

------------------------------------------------------------------------

# 19. BACKGROUND SUMMARY

Settings Home should display a useful summary.

Examples:

``` text
AMOLED Pure Black
```

or:

``` text
Mountain.jpg
```

or:

``` text
Slideshow · 30 sec
```

------------------------------------------------------------------------

# 20. BACKGROUND DETAIL

Keep the Phase 5 functionality:

-   Solid Color
-   Image
-   Slideshow
-   Dim
-   Crossfade
-   Shuffle if already implemented

Do not add video backgrounds.

Do not add online wallpaper search.

------------------------------------------------------------------------

# 21. BACKGROUND PREVIEW

Retain the Phase 5 preview.

The preview should show:

-   actual clock style
-   date
-   timer
-   background
-   dim overlay

Do not show the complete settings screen inside the preview.

------------------------------------------------------------------------

# 22. AMBIENT SOUND SETTINGS

Create/refine a dedicated Ambient Sound screen.

Show:

``` text
Ambient Sound

[Deep Focus]
[Rain]
[White Noise]
...
```

Use the actual tracks already supported by the Media3 architecture.

Do not introduce a new audio engine.

------------------------------------------------------------------------

# 23. AUDIO SUMMARY

Settings Home should show:

``` text
Deep Focus · Loop
```

or:

``` text
Off
```

------------------------------------------------------------------------

# 24. AUTO PLAY

Provide:

``` text
Auto Play
```

Description:

``` text
Start the selected sound when Focus begins.
```

Default should be conservative and consistent with the existing app
behavior.

Do not automatically start sound if the user has not enabled the
feature.

------------------------------------------------------------------------

# 25. LOOP

Expose:

``` text
Loop
```

Options:

``` text
Off
Track
Playlist
```

Map directly to Media3 repeat behavior.

Do not create custom playback logic.

------------------------------------------------------------------------

# 26. VOLUME

If volume is already supported:

Expose a simple slider.

Example:

``` text
Volume
────────●────
60%
```

Do not change the device's global volume unexpectedly.

Clearly distinguish:

**App playback volume**

from:

**Device volume**

if both exist.

------------------------------------------------------------------------

# 27. WAVEFORM SETTING

If the waveform exists:

Provide:

``` text
Audio Visualization
On / Off
```

Description:

``` text
Show a subtle visual response while sound plays.
```

Default:

ON if it is already part of the established design.

------------------------------------------------------------------------

# 28. PLAYER CONTROL VISIBILITY

Do not add unnecessary configuration unless already supported.

If configurable:

``` text
Auto-hide controls
On
```

Keep the default ON.

Do not let users accidentally make the Active Focus screen permanently
cluttered.

------------------------------------------------------------------------

# 29. SCREEN AWAKE

Provide:

``` text
Keep Screen Awake During Focus
```

Default:

ON.

Description:

``` text
Keep the display awake while Focus is active.
```

This setting should control the existing screen-awake behavior.

Do not use wake locks as a replacement.

Do not keep the screen awake after Focus ends unless Android behavior
requires it.

------------------------------------------------------------------------

# 30. SCREEN AWAKE LIMIT

Do not claim the app can keep the screen awake indefinitely under every
Android/OEM condition.

Use the normal Android window/screen-awake mechanism.

If the OS or manufacturer applies special battery policies, behavior may
vary.

------------------------------------------------------------------------

# 31. GENERAL SETTINGS

Keep General small.

Potential items:

``` text
Keep Screen Awake
Auto-hide Controls
Haptic Feedback
About
```

Do not create dozens of toggles.

------------------------------------------------------------------------

# 32. HAPTIC FEEDBACK

If implemented:

``` text
Haptic Feedback
On / Off
```

Use only subtle interaction feedback.

Do not vibrate continuously during Focus.

Do not request vibration permission if it is not required by the chosen
Android API/implementation.

------------------------------------------------------------------------

# 33. ABOUT

Create/refine an About screen.

Include:

-   app icon
-   app name
-   version
-   short product description
-   developer information
-   privacy policy link if one exists
-   open-source acknowledgements if applicable

Do not add fake links.

Do not create a social-media section unless real links are available.

------------------------------------------------------------------------

# 34. DEVELOPER CREDIT

If developer information is already part of the application, preserve
it.

Keep it understated.

The About screen should not compete with Focus functionality.

------------------------------------------------------------------------

# 35. SETTINGS SEARCH

Do NOT implement settings search in this phase unless the settings list
becomes genuinely large.

For the current MVP, direct navigation is preferable.

------------------------------------------------------------------------

# 36. QUICK START / HOME

If the application already has a Home screen, refine it so the user can
quickly:

-   see current Focus configuration
-   start Focus
-   open Settings

Do not add unnecessary dashboard widgets.

------------------------------------------------------------------------

# 37. PRIMARY START ACTION

The main start action should remain prominent.

Example:

``` text
▶ START FOCUS
```

It should communicate the currently selected configuration nearby.

Example:

``` text
25 min · Countdown
```

------------------------------------------------------------------------

# 38. PRE-FOCUS SUMMARY

Before starting Focus, the user should be able to understand:

``` text
25 min · Countdown
Clean Digital
AMOLED Pure Black
Deep Focus
```

Do not force users to open multiple screens to verify configuration.

------------------------------------------------------------------------

# 39. START FOCUS FLOW

Recommended:

``` text
Home
  ↓
Start Focus
  ↓
Active Focus
```

If configuration needs changing:

``` text
Home
  ↓
Focus Session
  ↓
Start Focus
```

Do not add unnecessary confirmation screens.

------------------------------------------------------------------------

# 40. ACTIVE FOCUS

Do NOT redesign the Active Focus screen.

Phase 4.5 established its visual language.

Phase 5 established the background layer.

Preserve both.

Only make small integration fixes required by Settings.

------------------------------------------------------------------------

# 41. SETTINGS WHILE ACTIVE

If settings can be accessed while Focus is running:

Be careful.

Changing:

-   background
-   clock style
-   sound

may be safe.

Changing:

-   focus duration
-   timer mode

during an active session can create confusion.

Recommended:

Allow visual/audio changes.

Require ending the current session before changing core timer
configuration.

Do not silently reset the current timer.

------------------------------------------------------------------------

# 42. ACTIVE SESSION PROTECTION

If the user attempts to change Focus duration during an active session:

Show a concise message:

``` text
Focus is currently running.

End this session to change its duration.
```

Actions:

``` text
CANCEL
END FOCUS
```

Do not modify the current session unexpectedly.

------------------------------------------------------------------------

# 43. PROFILE PREPARATION

Do not build a full profile system yet.

However, structure configuration so future Focus Profiles can be added.

Potential future profiles:

``` text
Study
Work
Reading
Meditation
Driving
```

Do not implement them now unless the existing architecture already
supports them.

------------------------------------------------------------------------

# 44. DATA MODEL

Avoid one giant settings object if it becomes difficult to maintain.

Prefer logical configuration groups:

``` text
FocusSettings
ClockSettings
BackgroundSettings
AudioSettings
GeneralSettings
```

These may map to existing DataStore/preferences.

The exact implementation is your engineering decision.

------------------------------------------------------------------------

# 45. SINGLE SOURCE OF TRUTH

Each setting must have one authoritative source.

Avoid:

``` text
UI value
+
ViewModel value
+
DataStore value
+
manager value
```

all independently storing the same configuration.

Use a clean unidirectional state flow.

------------------------------------------------------------------------

# 46. DATASTORE

Continue using the existing DataStore architecture.

Do not create:

-   SharedPreferences duplicate
-   database solely for settings
-   JSON settings file
-   static singleton settings object

unless a genuine requirement exists.

------------------------------------------------------------------------

# 47. DEFAULTS

Define centralized defaults.

Examples:

``` text
Focus:
25 minutes

Timer:
Countdown

Clock:
Clean Digital

Background:
AMOLED Pure Black

Slideshow:
Off

Transition:
Crossfade

Audio:
Off or existing established default

Keep Screen Awake:
On

Auto-hide Controls:
On
```

Use the actual existing product defaults where they already exist.

Do not reset existing user settings.

------------------------------------------------------------------------

# 48. RESET SETTINGS

Consider adding:

``` text
Reset Settings
```

only if useful.

If implemented:

-   clearly warn the user
-   reset only application preferences
-   do not delete user photos
-   do not delete external media
-   do not modify Android system settings

------------------------------------------------------------------------

# 49. RESET CONFIRMATION

Use:

``` text
Reset settings?

This will restore Focus Clock's default preferences.

CANCEL
RESET
```

Do not imply that user photos will be deleted.

------------------------------------------------------------------------

# 50. NAVIGATION

Keep navigation predictable.

Every detail screen:

-   has back navigation
-   has clear title
-   does not unexpectedly reset scroll position
-   returns to the previous settings location

Do not add bottom navigation.

------------------------------------------------------------------------

# 51. RESPONSIVE SETTINGS

Test:

-   small portrait
-   normal portrait
-   large portrait
-   landscape
-   large font
-   TalkBack

Settings must remain usable.

------------------------------------------------------------------------

# 52. LANDSCAPE SETTINGS

Landscape is not the primary configuration orientation.

Still ensure:

-   no clipping
-   no horizontal overflow
-   no giant empty regions
-   proper back navigation

If portrait-only settings are more appropriate, follow Android best
practices rather than forcing a bad landscape layout.

------------------------------------------------------------------------

# 53. TYPOGRAPHY

Use the existing typography system.

Suggested hierarchy:

Page title: 24–28sp

Section title: 12–14sp uppercase or appropriate existing style

Setting title: 16–18sp

Setting summary: 13–15sp

Do not use tiny text for important information.

------------------------------------------------------------------------

# 54. SPACING

Use a consistent spacing system.

Prefer multiples of:

``` text
4dp / 8dp
```

Examples:

-   section spacing: 24–32dp
-   row padding: 16dp
-   icon/text gap: 12–16dp
-   card gap: 12dp

Do not hardcode random spacing values.

------------------------------------------------------------------------

# 55. CORNER RADIUS

Reuse the existing radius system.

Do not introduce a new radius for every component.

Keep the visual language consistent.

------------------------------------------------------------------------

# 56. ICONS

Use the existing icon family.

Icons should:

-   be consistent
-   have accessible descriptions
-   not become oversized
-   not rely solely on color

------------------------------------------------------------------------

# 57. SELECTION STATES

For:

-   clock styles
-   colors
-   audio tracks
-   timer mode

use consistent selection treatment.

Possible combination:

-   accent border
-   subtle accent background
-   checkmark
-   selected label

Do not rely only on color.

------------------------------------------------------------------------

# 58. SWITCHES

Use switches only for true binary settings.

Examples:

``` text
Auto Play
On / Off

Keep Screen Awake
On / Off

Auto-hide Controls
On / Off
```

Do not use switches for:

-   duration
-   clock style
-   repeat mode
-   background type

Use selection controls for those.

------------------------------------------------------------------------

# 59. SEGMENTED CONTROLS

Use segmented controls for small mutually exclusive sets.

Examples:

``` text
Countdown | Elapsed
```

or:

``` text
12h | 24h
```

Do not overcrowd segmented controls.

------------------------------------------------------------------------

# 60. BOTTOM ACTIONS

On configuration screens with a primary action:

Use a bottom-aligned action where appropriate.

Example:

``` text
START FOCUS
```

Respect safe-area insets.

Do not allow buttons to overlap gesture navigation.

------------------------------------------------------------------------

# 61. SETTINGS PREVIEW

Where appropriate, show current selection visually.

For example:

Clock:

``` text
[ 04 ]
[ 41 ]
Clean Digital
```

Background:

actual background preview.

Audio:

track name + subtle waveform.

This makes settings feel more tangible.

------------------------------------------------------------------------

# 62. NO SETTINGS CLUTTER

Do not show every advanced option at once.

Use sections.

Example:

``` text
BACKGROUND

AMOLED Pure Black
Brightness
Slideshow
```

Keep related options together.

------------------------------------------------------------------------

# 63. MICROCOPY

Use concise, user-friendly wording.

Good:

``` text
Keep Screen Awake
Start sound automatically
Repeat current track
Change how the clock looks
```

Avoid developer terminology.

------------------------------------------------------------------------

# 64. PERMISSIONS

This phase should NOT introduce new dangerous permissions merely for
settings.

Do not add:

-   Accessibility
-   VPN
-   Device Owner
-   phone
-   contacts
-   microphone
-   location
-   broad storage

If the Photo Picker from Phase 5 requires no new permission, keep it
that way.

Preserve only legitimate existing Media3/notification permissions.

------------------------------------------------------------------------

# 65. SYSTEM SETTINGS

Do not attempt to modify:

-   global Do Not Disturb
-   mobile data
-   Wi-Fi
-   other apps
-   battery saver
-   system brightness

The app controls its own experience only.

------------------------------------------------------------------------

# 66. NOTIFICATION SETTINGS

If the Media3 notification already exists:

Do not duplicate notification controls inside Settings.

A simple explanation can be provided if needed.

Do not create a custom notification settings system.

------------------------------------------------------------------------

# 67. PRIVACY

Settings should not collect user data.

Do not introduce:

-   analytics
-   tracking
-   cloud accounts
-   telemetry

in this phase.

------------------------------------------------------------------------

# 68. PERFORMANCE

Settings screens should load quickly.

Do not:

-   decode large images unnecessarily
-   initialize ExoPlayer just to display a settings row
-   start slideshow in Settings
-   run continuous timers unnecessarily

Use lightweight previews.

------------------------------------------------------------------------

# 69. AUDIO SETTINGS PERFORMANCE

Do not initialize a full playback session merely to render:

``` text
Deep Focus
```

Only connect to MediaController when actual playback interaction
requires it.

------------------------------------------------------------------------

# 70. BACKGROUND SETTINGS PERFORMANCE

Do not load every slideshow image at full resolution when opening
Settings.

Use thumbnails/previews.

------------------------------------------------------------------------

# 71. CONFIGURATION PREVIEW PERFORMANCE

Preview should be lightweight.

Avoid rendering unnecessary Active Focus functionality.

For example:

Settings background preview does not need:

-   Media3
-   actual waveform playback
-   full timer engine

unless there is a compelling reason.

------------------------------------------------------------------------

# 72. ACCESSIBILITY

Every setting needs:

-   readable title
-   useful summary
-   correct control semantics
-   selected state
-   content description where needed

TalkBack should make sense when navigating the Settings screens.

------------------------------------------------------------------------

# 73. TOUCH TARGETS

Interactive elements should have approximately:

**48dp × 48dp**

minimum touch area.

This applies to:

-   rows
-   switches
-   swatches
-   clock style cards
-   audio selection
-   sliders
-   buttons

------------------------------------------------------------------------

# 74. LARGE TEXT

Test with Android large font settings.

Do not:

-   clip labels
-   hide values
-   overlap controls
-   force fixed heights that break text

Allow rows to grow when needed.

------------------------------------------------------------------------

# 75. DARK THEME

The Settings system should remain optimized for dark/AMOLED use.

Avoid:

-   pure gray backgrounds everywhere
-   excessive borders
-   bright cards
-   too many accent elements

Use black as the visual foundation where appropriate.

------------------------------------------------------------------------

# 76. LIGHT THEME

If the application already supports light theme, preserve it.

Do not remove existing theme support.

If light theme does not exist, do not build a complete light theme in
this phase unless it is already part of the product requirements.

------------------------------------------------------------------------

# 77. CONFIGURATION CONSISTENCY

If the user changes a setting:

The summary on Settings Home should update immediately.

Example:

Before:

``` text
Background
AMOLED Pure Black
```

After:

``` text
Background
Mountain.jpg
```

No restart should be necessary.

------------------------------------------------------------------------

# 78. LIVE STATE

Where safe, configuration changes should apply immediately.

Examples:

-   clock style
-   background
-   dim
-   audio selection
-   waveform

Core Focus timer configuration should be protected during an active
session.

------------------------------------------------------------------------

# 79. PERSISTENCE TEST

For every setting:

1.  Change it.
2.  Leave screen.
3.  Return.
4.  Verify value.
5.  Close app.
6.  Reopen.
7.  Verify value.

Do not assume persistence works.

Test it.

------------------------------------------------------------------------

# 80. ROTATION TEST

Change settings.

Rotate device.

Verify:

-   current selection
-   scroll position where reasonable
-   preview
-   background
-   navigation

No state should unexpectedly reset.

------------------------------------------------------------------------

# 81. PROCESS RECREATION

If Activity is recreated:

-   configuration must remain
-   DataStore remains authoritative
-   UI reloads state correctly

Do not rely solely on in-memory variables.

------------------------------------------------------------------------

# 82. ACTIVE FOCUS REGRESSION

After Settings changes, verify:

1.  Start Focus.
2.  Clock is correct.
3.  Timer is correct.
4.  Background is correct.
5.  Audio is correct.
6.  Controls are correct.
7.  Screen stays awake according to setting.
8.  Orientation works.

------------------------------------------------------------------------

# 83. MEDIA3 REGRESSION

Verify:

-   Play
-   Pause
-   Stop
-   Next
-   Previous
-   Loop
-   background playback
-   notification
-   audio focus

Settings changes must not break the Media3 architecture.

------------------------------------------------------------------------

# 84. BACKGROUND REGRESSION

Verify:

-   solid color
-   image
-   slideshow
-   crossfade
-   dim
-   missing image fallback

Settings consolidation must not duplicate or break Background Engine
state.

------------------------------------------------------------------------

# 85. CLOCK REGRESSION

Verify all existing clock styles:

-   Clean Digital
-   Flip Clock
-   Minimal Digital
-   Analog Minimal

Check:

-   current time
-   minute rollover
-   midnight
-   12/24-hour mode
-   date

------------------------------------------------------------------------

# 86. TIMER REGRESSION

Verify:

-   Countdown
-   Elapsed
-   Unlimited
-   Pause
-   Resume
-   End Focus
-   completion behavior

Do not introduce timer drift.

------------------------------------------------------------------------

# 87. HOME SCREEN REGRESSION

Verify:

-   current configuration summary
-   Start Focus
-   navigation
-   settings
-   no stale values

------------------------------------------------------------------------

# 88. EMPTY STATES

Handle:

No audio:

``` text
No sound selected
```

No slideshow images:

``` text
No images selected
```

Missing image:

``` text
Image unavailable
```

Do not show blank broken components.

------------------------------------------------------------------------

# 89. ERROR HANDLING

Settings errors should be graceful.

If persistence fails:

-   do not crash
-   retain current UI state where possible
-   show a concise message
-   avoid corrupting unrelated settings

------------------------------------------------------------------------

# 90. NO DATA LOSS

Never delete:

-   user-selected photos
-   external audio
-   unrelated settings

when changing configuration.

------------------------------------------------------------------------

# 91. SETTINGS RESET SAFETY

If Reset Settings exists:

It must NOT:

-   delete user photos
-   delete files outside app-owned data
-   uninstall anything
-   modify Android settings

It should only reset application preferences.

------------------------------------------------------------------------

# 92. ABOUT SCREEN SAFETY

Only display real:

-   version
-   developer information
-   links

Do not invent:

-   website
-   privacy policy
-   social links
-   licenses

------------------------------------------------------------------------

# 93. FINAL SETTINGS INFORMATION ARCHITECTURE

Target:

``` text
HOME
│
├── Start Focus
│
└── Settings
    │
    ├── Focus
    │   ├── Duration
    │   ├── Timer Mode
    │   └── Completion
    │
    ├── Clock
    │   ├── Style
    │   ├── Time Format
    │   └── Date Format
    │
    ├── Background
    │   ├── Type
    │   ├── Color
    │   ├── Photos
    │   ├── Slideshow
    │   └── Dim
    │
    ├── Ambient Sound
    │   ├── Track
    │   ├── Auto Play
    │   ├── Loop
    │   ├── Volume
    │   └── Visualization
    │
    └── General
        ├── Keep Screen Awake
        ├── Auto-hide Controls
        ├── Haptics
        └── About
```

This is the target information architecture, not a requirement to create
every screen if an existing screen already performs the job well.

------------------------------------------------------------------------

# 94. FUTURE-READY ARCHITECTURE

Prepare, but DO NOT IMPLEMENT:

### Focus Profiles

``` text
Study
Work
Reading
Meditation
```

### Advanced Focus Rules

``` text
Scheduled Focus
Automatic sessions
Statistics
History
```

### More background effects

``` text
Blur
Gradient
Parallax
Video
```

These are future phases.

Do not add them now.

------------------------------------------------------------------------

# 95. DO NOT IMPLEMENT APP BLOCKING

This is extremely important.

This phase must NOT implement:

-   app blocking
-   package disabling
-   AccessibilityService
-   Device Owner
-   kiosk mode
-   VPN
-   network blocking
-   force-stop of other apps
-   notification interception

The Focus Clock should remain a user-controlled visual/audio focus
environment.

------------------------------------------------------------------------

# 96. DO NOT IMPLEMENT SYSTEM CONTROL

Do not attempt to:

-   disable other applications
-   block WhatsApp
-   disable calls
-   disable internet
-   modify mobile data
-   kill background apps
-   change system permissions
-   become Device Owner

Those are outside this phase and require separate
Android/platform/policy analysis.

------------------------------------------------------------------------

# 97. PLAY STORE SAFETY

Do not introduce permissions merely because a feature might be useful
later.

Every permission in the manifest should correspond to an implemented and
legitimate feature.

Do not add sensitive permissions preemptively.

------------------------------------------------------------------------

# 98. BUILD VALIDATION

After implementation:

1.  Build.
2.  Fix compilation errors.
3.  Fix relevant warnings.
4.  Launch.
5.  Test Settings Home.
6.  Test Focus.
7.  Test Clock.
8.  Test Background.
9.  Test Audio.
10. Test General.
11. Test persistence.
12. Test rotation.
13. Test Active Focus.
14. Test Media3.
15. Test slideshow.
16. Test large fonts.

Do not claim a test was performed if it was not.

------------------------------------------------------------------------

# 99. SUCCESS CRITERIA

Phase 6 is complete only when:

1.  Settings has a clear information architecture.
2.  Focus configuration is easy to understand.
3.  Clock configuration is consolidated.
4.  Background configuration is consolidated.
5.  Audio configuration is consolidated.
6.  General settings remain small.
7.  Settings summaries show current values.
8.  Existing DataStore architecture remains the source of truth.
9.  No duplicate settings system exists.
10. Active Focus remains visually intact.
11. Background Engine remains intact.
12. Media3 remains intact.
13. Focus Timer remains intact.
14. Clock styles remain intact.
15. Portrait works.
16. Landscape works.
17. Large fonts work reasonably.
18. TalkBack semantics are meaningful.
19. Touch targets are appropriate.
20. Settings persist after app restart.
21. Rotation does not unexpectedly reset state.
22. No unnecessary permissions were added.
23. No app-blocking functionality was added.
24. No Device Owner/VPN/Accessibility functionality was added.
25. No backend/cloud system was added.
26. The app now feels like a coherent product rather than a collection
    of independently built screens.

------------------------------------------------------------------------

# 100. FINAL PRODUCT EXPERIENCE

The user journey should feel simple:

``` text
OPEN APP
   ↓
SEE CURRENT FOCUS SETUP
   ↓
START FOCUS
   ↓
ENTER CALM FULL-SCREEN ENVIRONMENT
   ↓
CLOCK
TIMER
BACKGROUND
AMBIENT SOUND
   ↓
FOCUS
```

Settings should exist to prepare the environment.

They should not become the product itself.

------------------------------------------------------------------------

# 101. PHASE COMPLETION REPORT

When finished, report:

1.  Full architecture audit.
2.  Existing settings reused.
3.  Duplicate settings removed/consolidated.
4.  Files created.
5.  Files modified.
6.  Settings Hub implementation.
7.  Focus settings.
8.  Clock settings.
9.  Background settings integration.
10. Ambient Sound settings.
11. General settings.
12. About screen.
13. DataStore changes.
14. Navigation changes.
15. Permission changes.
16. Accessibility improvements.
17. Responsive improvements.
18. Persistence testing.
19. Rotation testing.
20. Active Focus regression testing.
21. Media3 regression testing.
22. Background regression testing.
23. Clock regression testing.
24. Timer regression testing.
25. Known limitations.
26. Confirmation that Phase 6 is complete.

Then:

**STOP.**

Do not implement Phase 7 automatically.

Wait for the next instruction.

------------------------------------------------------------------------

# FINAL DESIGN PRINCIPLE

Focus Clock should now feel like one complete product:

**Configure your environment once.**

**Start Focus.**

**Put the phone down.**

**See the time.**

**Know how much focus remains.**

**Hear a calm ambient track.**

**Let the background create atmosphere.**

**Keep everything else out of the way.**

The Settings system should support this philosophy rather than distract
from it.
