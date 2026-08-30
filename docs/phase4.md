# Focus Clock --- Phase 4

## Media3 Audio, Background Playback, Media Controls & Waveform

You are continuing development of the existing native Android **Focus
Clock** application.

IMPORTANT: Phase 1, Phase 2, and Phase 3 have already been implemented.

Do NOT rebuild the project from scratch. Do NOT replace working
architecture unnecessarily. Do NOT redesign the Active Focus screen
unless a small integration change is required.

This phase is specifically for:

**REAL AUDIO PLAYBACK + MEDIA3 + BACKGROUND PLAYBACK + MEDIA CONTROLS +
WAVEFORM INTEGRATION**

The goal is to replace the Phase 3 player placeholder with a reliable,
production-quality audio system while preserving the clock as the visual
hero.

------------------------------------------------------------------------

# 1. FIRST: AUDIT THE EXISTING PROJECT

Before modifying anything:

1.  Inspect the complete project.
2.  Inspect Phase 1, Phase 2, and Phase 3 implementations.
3.  Identify:
    -   ActiveFocusScreen
    -   ActiveFocusViewModel
    -   FocusSessionManager
    -   DataStore/preferences
    -   FocusPlayerArea
    -   playback placeholder
    -   waveform placeholder
    -   navigation
    -   theme
    -   existing dependencies
    -   AndroidManifest.xml
4.  Determine exactly how the Phase 3 player placeholder is currently
    connected.
5.  Reuse existing architecture.
6.  Do not create duplicate playback state.
7.  Do not create a second player abstraction if one already exists.

Before implementation, briefly report:

-   Existing playback-related components.
-   What will be replaced.
-   What will remain unchanged.
-   Which dependencies will be added/updated.
-   Which Android permissions will be added/changed.

Then implement this phase.

------------------------------------------------------------------------

# 2. TECHNOLOGY

Use current Android Jetpack Media3 APIs.

Prefer:

-   Media3 ExoPlayer
-   MediaSession
-   MediaSessionService
-   MediaController
-   appropriate Media3 UI/utilities where useful

Use versions compatible with the current project and current stable
Android/Media3 ecosystem.

Do NOT blindly copy obsolete ExoPlayer APIs from old tutorials.

Use the current Media3 package structure.

------------------------------------------------------------------------

# 3. PRIMARY ARCHITECTURE

The recommended architecture is:

``` text
ActiveFocusScreen
        |
        v
ActiveFocusViewModel
        |
        v
MediaController
        |
        v
MediaSessionService
        |
        v
ExoPlayer
```

The UI should NOT directly own the ExoPlayer instance.

The MediaSessionService should own playback.

The UI communicates through MediaController/MediaSession.

This is important because the user may:

-   leave the Activity
-   rotate the device
-   lock/unlock the screen
-   background the app
-   interact with headset controls
-   use Android media controls

while audio should continue appropriately.

------------------------------------------------------------------------

# 4. MEDIA SESSION SERVICE

Create a dedicated playback service, for example:

``` text
FocusPlaybackService
```

It should extend the appropriate current Media3 service class, such as:

``` text
MediaSessionService
```

Responsibilities:

-   own ExoPlayer
-   own MediaSession
-   maintain playlist
-   handle playback commands
-   handle repeat
-   handle next/previous
-   handle play/pause
-   handle stop
-   expose media metadata
-   handle lifecycle cleanup

Do NOT put UI code in the service.

Do NOT hold Activity references.

------------------------------------------------------------------------

# 5. FOREGROUND SERVICE

For background media playback, use the correct foreground-service
architecture and service type.

Declare only the permissions actually required by the current Android
version and Media3 implementation.

Potential permissions:

``` text
FOREGROUND_SERVICE
FOREGROUND_SERVICE_MEDIA_PLAYBACK
```

Use the appropriate foreground service type for media playback.

Do NOT add unrelated foreground-service types.

Do NOT add:

-   location foreground service
-   camera foreground service
-   microphone foreground service
-   connected-device foreground service

unless a future feature genuinely requires one.

------------------------------------------------------------------------

# 6. NOTIFICATION

The MediaSessionService should expose appropriate media controls through
the Android media notification/system media controls.

The notification should contain:

-   track title
-   playback state
-   play/pause
-   previous
-   next

Stop may be available where appropriate.

Do not create a giant custom notification.

Use the standard Android media playback experience.

The notification should remain useful even when the app UI is not
visible.

------------------------------------------------------------------------

# 7. POST_NOTIFICATIONS

Android 13+ may require notification permission.

Handle notification permission correctly.

IMPORTANT:

The core clock application should not become unusable merely because
notification permission is denied.

If notification permission is denied:

-   the Active Focus clock must still work
-   the timer must still work
-   audio behavior should be handled according to Android's applicable
    foreground-service rules
-   explain limitations honestly

Do not repeatedly request notification permission.

Ask at an appropriate user-initiated moment if needed.

------------------------------------------------------------------------

# 8. MEDIA3 DEPENDENCIES

Add only the required Media3 modules.

Likely components:

-   media3-exoplayer
-   media3-session
-   media3-common

Add other Media3 modules only when required.

Do not add the entire Media3 ecosystem unnecessarily.

Keep dependency versions consistent.

------------------------------------------------------------------------

# 9. PLAYER LIFECYCLE

The service should:

1.  Create ExoPlayer.
2.  Configure appropriate audio attributes.
3.  Create MediaSession.
4.  Publish media session.
5.  Handle media items.
6.  Start foreground playback appropriately when required.
7.  Release player and MediaSession when service is destroyed.

Avoid leaking player resources.

Do not create a new ExoPlayer instance every time the user opens Active
Focus.

There should be one controlled playback owner.

------------------------------------------------------------------------

# 10. AUDIO ATTRIBUTES

Configure appropriate Media3 audio attributes for music/ambient
playback.

The application should identify itself correctly as media playback.

Do not request microphone access.

Do not use audio recording.

Do not capture the microphone for waveform visualization.

------------------------------------------------------------------------

# 11. AUDIO FOCUS

Implement Android audio focus appropriately through Media3/ExoPlayer.

Handle interruptions such as:

-   phone calls
-   another app starting audio
-   transient audio focus loss
-   permanent audio focus loss
-   headset disconnect

Expected general behavior:

On transient interruption: - pause or duck according to the appropriate
Android/media behavior.

On permanent audio focus loss: - stop/pause appropriately.

When audio focus becomes available again: - resume only according to the
user's expected playback behavior and Android media conventions.

Do not implement aggressive automatic resume that surprises the user.

------------------------------------------------------------------------

# 12. PHONE CALLS

The application should not request phone permissions just to detect
calls.

Use standard Android audio focus/media behavior.

If a phone call interrupts playback:

-   allow the system to manage the interruption
-   pause/duck appropriately
-   do not attempt to interfere with the call

Do not attempt to block calls.

------------------------------------------------------------------------

# 13. BLUETOOTH / HEADPHONES

The media session should support standard Android media controls.

Where Android supports it, headset/Bluetooth controls should allow:

-   play/pause
-   next
-   previous

Do not request unnecessary Bluetooth permissions merely for standard
MediaSession behavior.

If a future feature requires explicit Bluetooth device management, that
should be separately evaluated.

------------------------------------------------------------------------

# 14. MEDIA ITEMS

Create a clean media model.

Conceptually:

``` text
FocusTrack
    id
    title
    artist
    uri
    artworkUri
    duration
    type
```

The implementation may differ, but the model must be clean and
serializable where appropriate.

Do not put UI-specific state into the media item model.

------------------------------------------------------------------------

# 15. PLAYLIST

Support:

-   one track
-   multiple tracks
-   next
-   previous
-   repeat
-   shuffle-ready architecture

Initial playlist state should be deterministic.

For example:

``` text
Track A
Track B
Track C
```

The user should be able to move between tracks.

------------------------------------------------------------------------

# 16. PLAYBACK STATES

The UI should support at minimum:

``` text
IDLE
BUFFERING
READY / PAUSED
PLAYING
ENDED
ERROR
```

Map Media3 playback state to a clean application-level UI state.

Do not expose raw ExoPlayer internals everywhere in Compose.

------------------------------------------------------------------------

# 17. PLAY / PAUSE

Play/Pause must be connected to MediaController.

When playing:

-   show Pause icon.

When paused:

-   show Play icon.

The UI must reflect the actual Media3 playback state.

Do not use a fake local boolean as the source of truth.

------------------------------------------------------------------------

# 18. STOP

The Stop control should:

-   stop playback
-   reset/retain position according to the defined product behavior
-   update the UI
-   update MediaSession state

Recommended MVP behavior:

Stop returns the current track to the beginning or appropriate default
position.

Document the chosen behavior.

Do not terminate the entire Focus Session when Stop is pressed.

IMPORTANT:

**Stopping music does NOT stop Focus.**

------------------------------------------------------------------------

# 19. PREVIOUS

Previous should:

-   go to previous media item when appropriate
-   if the current track has played beyond a reasonable threshold,
    optionally restart the current track instead of moving backwards

Use conventional media-player behavior.

Keep the exact threshold easy to change.

------------------------------------------------------------------------

# 20. NEXT

Next should:

-   advance to next track
-   obey repeat/shuffle configuration when implemented
-   gracefully handle the end of a playlist

Do not crash if the playlist is empty.

------------------------------------------------------------------------

# 21. LOOP

Support:

-   Off
-   Repeat Track
-   Repeat Playlist

Use Media3 repeat modes.

Do not create a custom loop timer.

Persist the user's loop preference through the existing DataStore
architecture.

------------------------------------------------------------------------

# 22. SHUFFLE

Prepare the architecture for shuffle.

If full shuffle UI is not part of this phase, keep the player
architecture compatible with future shuffle.

Do not create a second custom playlist algorithm if Media3 can manage
the playback order.

------------------------------------------------------------------------

# 23. TRACK TITLE

Active Focus should display:

``` text
Track Name
```

Optionally:

``` text
Artist
```

Keep metadata visually secondary.

Long titles:

-   truncate gracefully
-   do not wrap into a huge multi-line block
-   do not push the clock upward

Use ellipsis where appropriate.

------------------------------------------------------------------------

# 24. PLAYER AREA DESIGN

Preserve the Phase 3 Active Focus layout.

Do NOT redesign the entire screen.

The player should remain visually subordinate to the clock.

Recommended hierarchy:

``` text
Clock

Date

Focus Timer


Track Title

Waveform

Previous  Play/Pause  Stop  Next  Loop
```

The clock must remain the most visually prominent element.

------------------------------------------------------------------------

# 25. PLAYER CONTROL SIZES

Main Play/Pause:

approximately 48--56dp visual area.

Other controls:

approximately 40--48dp visual icon area.

Actual touch target:

approximately 48dp or larger.

Controls must be comfortable for:

-   finger tapping
-   desk use
-   vehicle stand/mount use where appropriate

Do not make controls tiny just to preserve minimalism.

Minimal does NOT mean difficult to touch.

------------------------------------------------------------------------

# 26. CONTROL VISIBILITY

Preserve Phase 3 behavior:

CALM STATE:

-   clock
-   date
-   timer
-   track

INTERACTION STATE:

-   playback controls
-   End Focus

After approximately 4--6 seconds:

controls fade.

Do not permanently show a large player panel.

------------------------------------------------------------------------

# 27. WAVEFORM

Replace the Phase 3 waveform placeholder with a real playback-aware
visualization where technically appropriate.

IMPORTANT:

Do NOT use microphone recording.

Do NOT request RECORD_AUDIO.

The waveform/equalizer must be derived from playback data or a
lightweight visualization strategy appropriate for the selected audio.

If true waveform extraction from the current playback stream would be
expensive or unreliable, use a carefully designed playback-reactive
visualizer rather than a fake microphone-based solution.

------------------------------------------------------------------------

# 28. WAVEFORM DESIGN

The waveform should be:

-   subtle
-   smooth
-   low contrast
-   calm
-   visually secondary

It must NOT:

-   flash
-   pulse aggressively
-   dominate the clock
-   consume excessive CPU
-   cause frame drops

Conceptual appearance:

``` text
▁▂▃▅▆▃▂▅▇▅▃▂▁
```

But create an original visual design.

------------------------------------------------------------------------

# 29. WAVEFORM STATES

PLAYING:

-   subtle movement

PAUSED:

-   freeze or calm animation

STOPPED:

-   static/low activity

BUFFERING:

-   calm loading state

ERROR:

-   do not animate aggressively

------------------------------------------------------------------------

# 30. PERFORMANCE OF WAVEFORM

The Focus Clock may run for hours.

Therefore:

-   avoid high-frequency CPU processing
-   avoid allocating large arrays every frame
-   avoid expensive audio analysis
-   avoid unnecessary recomposition
-   avoid creating bitmap/audio buffers unnecessarily

Target smooth rendering while keeping CPU usage low.

If necessary, use a simplified visualization.

Quality of the overall Focus experience is more important than
mathematically accurate waveform analysis.

------------------------------------------------------------------------

# 31. PROGRESS

If the player UI includes track progress:

Use actual Media3 playback position.

Do not create a separate local progress timer.

If progress is shown:

-   update at a reasonable frequency
-   do not refresh the entire screen unnecessarily
-   keep it visually subtle

The Focus Timer remains separate from track progress.

------------------------------------------------------------------------

# 32. MEDIA CONTROLLER

Create the MediaController from the UI/application layer using the
appropriate Media3 APIs.

The controller should:

-   connect to MediaSessionService
-   observe playback state
-   send commands
-   receive metadata
-   receive playlist updates

Handle connection/disconnection cleanly.

Do not crash if the service is temporarily unavailable.

------------------------------------------------------------------------

# 33. SERVICE CONNECTION

When Active Focus starts with audio selected:

1.  Connect to MediaSessionService.
2.  Create/use MediaController.
3.  Load selected media.
4.  Start playback if auto-play is enabled.

If audio is not selected:

-   do not start the service unnecessarily.

This reduces battery use and complexity.

------------------------------------------------------------------------

# 34. AUTO-PLAY

Respect the user's existing/prepared music configuration.

If Auto Play is ON:

-   begin selected track when Focus starts.

If Auto Play is OFF:

-   show the selected track
-   remain paused

Do not automatically play audio without an explicit user setting.

------------------------------------------------------------------------

# 35. FOCUS SESSION VS MUSIC SESSION

These are separate concepts.

Focus Session:

-   has its own timer
-   may continue without audio

Music Playback:

-   can play/pause/stop independently

Examples:

User presses Stop:

Music stops. Focus continues.

User pauses music:

Music pauses. Focus continues.

Timer reaches zero:

Focus ends. Music behavior follows the configured completion behavior.

Do NOT tightly couple the two state machines.

------------------------------------------------------------------------

# 36. FOCUS COMPLETION

When Focus Session reaches zero:

Use the existing FocusSessionManager completion state.

The Active Focus UI should decide what happens to audio.

Default recommended behavior:

-   pause/stop audio when Focus ends

But keep this behavior configurable later.

Do not terminate the entire MediaSessionService if the user is still
using the player elsewhere unless the product state requires it.

------------------------------------------------------------------------

# 37. BACKGROUND PLAYBACK

Audio should continue when the Active Focus Activity is no longer
visible if:

-   playback is active
-   the user expects background playback
-   Android allows it under current lifecycle/service rules

The clock Activity itself does not need to remain alive merely to keep
audio playing.

The MediaSessionService owns playback.

------------------------------------------------------------------------

# 38. LOCK SCREEN

Where supported by Android:

The MediaSession should expose appropriate playback controls through
system media surfaces/lock screen.

Do not create custom lock-screen UI.

Do not request unrelated permissions.

------------------------------------------------------------------------

# 39. NOTIFICATION CHANNEL

If a foreground media notification is required:

Create a dedicated notification channel.

Channel name should be clear, e.g.:

**Focus Clock Playback**

Do not create multiple duplicate channels.

Respect user notification settings.

------------------------------------------------------------------------

# 40. MEDIA METADATA

Populate MediaSession metadata with:

-   title
-   artist where available
-   artwork where available

If artwork is unavailable:

Do not download random images.

Use a clean default artwork or no artwork.

Artwork implementation must not become a Phase 5 background system.

------------------------------------------------------------------------

# 41. LOCAL AUDIO FOR MVP

If Phase 1--3 currently contain no audio source system:

For this phase, use a small set of legally usable/local bundled or
app-provided audio assets for testing.

Examples:

-   ambient
-   rain
-   white noise
-   calm instrumental

Do not bundle copyrighted commercial songs without appropriate rights.

Do not build a commercial music catalog.

------------------------------------------------------------------------

# 42. FUTURE ONLINE AUDIO

If future versions support remote audio URLs:

Architecture should permit media URIs.

However:

Do not introduce a remote streaming backend in this phase.

Do not download arbitrary URLs from the internet.

Do not create a music-search engine.

Keep the MVP media source architecture local and controlled.

------------------------------------------------------------------------

# 43. NETWORK

Do not add INTERNET permission merely because Media3 is being added.

Only add INTERNET if this phase actually uses remote media.

If all test media is local:

Do not add INTERNET.

------------------------------------------------------------------------

# 44. STORAGE

Do NOT request broad storage permissions.

Do not use:

-   READ_EXTERNAL_STORAGE
-   WRITE_EXTERNAL_STORAGE
-   MANAGE_EXTERNAL_STORAGE

for this phase.

If user-selected audio is supported later, evaluate modern Android
media/file picker APIs at that time.

For this phase, keep the source controlled.

------------------------------------------------------------------------

# 45. POST_NOTIFICATIONS UX

If notification permission is required by the implemented Android
behavior:

Explain why it is useful.

Suggested explanation:

"Allow notifications to keep playback controls available while Focus
Clock is playing audio."

Do not make the explanation about app blocking or system control.

Ask at an appropriate moment rather than immediately on first launch.

------------------------------------------------------------------------

# 46. NO MICROPHONE

Absolutely do NOT request:

``` text
RECORD_AUDIO
```

The waveform is not a microphone visualizer.

The app must never record ambient sound for this feature.

------------------------------------------------------------------------

# 47. NO PHONE PERMISSIONS

Do not request:

-   READ_PHONE_STATE
-   READ_CALL_LOG
-   READ_CONTACTS
-   CALL_PHONE

Audio interruptions should use standard Android audio focus behavior.

------------------------------------------------------------------------

# 48. MEDIA ERROR HANDLING

Handle:

-   missing file
-   unsupported media
-   decoding error
-   service unavailable
-   invalid URI
-   playback error
-   empty playlist

When a track fails:

1.  Show a small non-intrusive error state.
2.  Try the next valid track if appropriate.
3.  Keep Focus Session alive.
4.  Do not crash.
5.  Do not cover the clock with an error screen.

------------------------------------------------------------------------

# 49. EMPTY PLAYLIST

If there are no tracks:

Show a minimal state:

``` text
No sound selected
```

and keep the Focus Clock fully functional.

The user should still be able to start and complete a Focus Session.

------------------------------------------------------------------------

# 50. AUDIO VOLUME

Do not override global device volume.

Use standard Media3/player volume.

If a volume control is later added:

-   provide a reasonable range
-   do not unexpectedly set the user's system volume

A simple in-app volume preference can be prepared if appropriate, but do
not overcomplicate this phase.

------------------------------------------------------------------------

# 51. DATASTORE

Persist:

-   selected track
-   loop mode
-   auto-play
-   player visibility
-   waveform visibility
-   volume preference if implemented

Use the existing DataStore.

Do not create another preferences repository.

------------------------------------------------------------------------

# 52. PLAYER UI STATE

Create or refine a player UI state.

Conceptually:

``` text
PlayerUiState(
    isConnected,
    playbackState,
    isPlaying,
    currentTrack,
    currentIndex,
    duration,
    position,
    repeatMode,
    shuffleEnabled,
    hasNext,
    hasPrevious,
    error
)
```

The exact model is your engineering decision.

Do not duplicate Media3 state unnecessarily.

------------------------------------------------------------------------

# 53. ACTIVE FOCUS INTEGRATION

The Phase 3 Active Focus screen should remain visually intact.

Replace:

``` text
FakePlayer
```

with:

``` text
RealMedia3Player
```

through the existing `FocusPlayerArea`.

The rest of the screen should not need major changes.

The clock must remain the hero.

------------------------------------------------------------------------

# 54. LANDSCAPE

Verify the real player inside the Phase 3 landscape layout.

Ensure:

-   controls fit in one row
-   track title does not push clock
-   waveform remains compact
-   touch targets remain adequate
-   no overlap
-   no accidental scrolling

Do not redesign the entire landscape screen.

------------------------------------------------------------------------

# 55. PORTRAIT

Verify:

-   clock remains dominant
-   timer remains secondary
-   track area fits at bottom
-   controls are accessible
-   long track titles truncate
-   waveform remains subtle

Do not allow the player to consume more visual attention than the clock.

------------------------------------------------------------------------

# 56. CONTROL ORDER

Maintain:

Previous Play/Pause Stop Next Loop

Do not add additional controls to Active Focus in this phase.

Advanced controls can be placed in future Settings or Music screens.

------------------------------------------------------------------------

# 57. MEDIA BUTTONS

Where Media3/Android supports standard media button events:

Handle:

-   play
-   pause
-   next
-   previous

Do not intercept unrelated hardware buttons.

------------------------------------------------------------------------

# 58. HEADSET DISCONNECT

When wired/Bluetooth audio disconnects:

Use appropriate Android/Media3 behavior.

Do not crash.

Do not force the user into Settings.

Do not automatically switch to speaker at an unexpectedly high volume.

Prefer safe media behavior.

------------------------------------------------------------------------

# 59. AUDIO OUTPUT

Do not implement custom audio routing.

Use standard Android media output routing.

Future versions may add a dedicated output selector if necessary.

Not part of this phase.

------------------------------------------------------------------------

# 60. LONG-RUN TEST

Run or reason through a long session:

-   30 minutes
-   1 hour
-   2 hours

Look for:

-   memory growth
-   player leaks
-   waveform CPU usage
-   timer drift
-   UI freezes
-   service lifecycle problems

The architecture must support long-running focus sessions.

------------------------------------------------------------------------

# 61. SCREEN ROTATION TEST

During playback:

1.  Start Focus.
2.  Start music.
3.  Rotate device.
4.  Verify music continues.
5.  Verify current track remains correct.
6.  Verify position remains correct.
7.  Verify clock remains correct.
8.  Verify Focus Timer remains correct.

Do not recreate ExoPlayer unnecessarily during rotation.

------------------------------------------------------------------------

# 62. BACKGROUND TEST

During playback:

1.  Start Focus.
2.  Start music.
3.  Leave Active Focus.
4.  Return.
5.  Verify playback state.
6.  Verify track metadata.
7.  Verify timer.
8.  Verify clock.

The service must remain the playback owner.

------------------------------------------------------------------------

# 63. SCREEN LOCK TEST

If the user locks the device during an active playback scenario:

Follow Android's normal media behavior.

Do not assume the Activity remains alive.

The MediaSessionService should own playback where allowed.

Do not use unnecessary wake locks.

------------------------------------------------------------------------

# 64. SCREEN AWAKE

The existing Phase 3 screen-awake behavior should remain limited to
Active Focus.

Do not keep the screen awake merely because music is playing in the
background after Focus has ended.

------------------------------------------------------------------------

# 65. BATTERY

Do not use:

-   high-frequency polling
-   custom audio threads
-   unnecessary wake locks
-   continuous CPU loops

Media3 should manage playback efficiently.

Waveform visualization should only update while the UI is visible and
should be lightweight.

------------------------------------------------------------------------

# 66. NOTIFICATION / MEDIA CONTROL UX

The user should be able to control playback without reopening the app.

Where supported:

-   notification
-   system media controls
-   lock-screen media controls
-   headset media buttons

The controls should reflect the actual MediaSession state.

------------------------------------------------------------------------

# 67. UI ANIMATION

Player controls may use subtle state transitions:

Play ↔ Pause

Loop ON ↔ OFF

Do not use large animations.

Waveform movement should be subtle.

Do not animate the clock layout because playback changes.

------------------------------------------------------------------------

# 68. ACCESSIBILITY

Player controls need:

-   content descriptions
-   meaningful labels
-   minimum touch targets
-   accessible selected states

Examples:

"Previous track"

"Play"

"Pause"

"Stop"

"Next track"

"Repeat playlist"

Do not rely only on icon shape or color.

------------------------------------------------------------------------

# 69. ERROR UX

Do not show giant error dialogs for normal playback failures.

Use:

-   small message
-   subtle snackbar
-   inline player state

The clock should remain visible.

Focus should continue.

------------------------------------------------------------------------

# 70. SECURITY

Do not download or execute arbitrary content.

Do not accept arbitrary external commands.

Do not expose unnecessary service interfaces.

Keep MediaSession commands limited to legitimate playback controls.

------------------------------------------------------------------------

# 71. MANIFEST REVIEW

After implementation, inspect AndroidManifest.xml carefully.

Only include permissions/services required by actual functionality.

Expected potential entries:

``` text
FOREGROUND_SERVICE
FOREGROUND_SERVICE_MEDIA_PLAYBACK
POST_NOTIFICATIONS
```

Only include INTERNET if remote media is actually implemented.

Do not add:

-   RECORD_AUDIO
-   VPN
-   Accessibility
-   Device Owner
-   exact alarms
-   broad storage
-   phone
-   contacts
-   location

------------------------------------------------------------------------

# 72. GOOGLE PLAY SAFETY

The app should be clearly positioned as a focus clock/productivity
application.

Do not describe the media service as a system-control mechanism.

Do not claim the app can block other applications.

Do not request permissions unrelated to actual media playback.

------------------------------------------------------------------------

# 73. PHASE 4 SCOPE LIMIT

Do NOT automatically implement:

-   full Music Library screen
-   online music catalog
-   streaming backend
-   user audio picker
-   background image system
-   slideshow
-   complete Settings redesign
-   app blocking
-   Device Owner
-   AccessibilityService
-   VPN
-   Focus Profiles
-   analytics
-   cloud sync

Those belong to later phases.

------------------------------------------------------------------------

# 74. Prepare for Phase 5

Phase 5 will implement:

-   solid backgrounds
-   user images
-   Photo Picker
-   slideshow
-   image rotation
-   shuffle
-   crossfade
-   fade
-   overlays
-   background contrast

The real media player must remain independent from background rendering.

Do not mix:

``` text
Player
```

with:

``` text
Background
```

------------------------------------------------------------------------

# 75. Prepare for Phase 6

Settings will eventually control:

-   selected track
-   auto-play
-   loop
-   waveform
-   player visibility
-   control visibility
-   volume
-   background
-   clock style
-   timer
-   screen awake

Use the existing configuration architecture.

Do not hardcode settings throughout the player.

------------------------------------------------------------------------

# 76. TEST CASES

At minimum test:

### Basic

1.  Start Focus with no audio.
2.  Start Focus with one track.
3.  Play.
4.  Pause.
5.  Stop.
6.  Next.
7.  Previous.
8.  Loop track.
9.  Loop playlist.
10. Empty playlist.

### Lifecycle

11. Rotate.
12. Background app.
13. Return to app.
14. Screen lock/unlock if practical.
15. Activity recreation.

### Audio

16. Audio interruption.
17. Phone call interruption if practical.
18. Headphone disconnect.
19. Bluetooth disconnect if available.
20. Another media app starts.

### Permissions

21. Notification permission allowed.
22. Notification permission denied.

### Error

23. Missing track.
24. Invalid media URI.
25. Unsupported media.
26. Playback error.

### Long run

27. 30-minute session.
28. 1-hour session if practical.

------------------------------------------------------------------------

# 77. BUILD VALIDATION

After implementation:

1.  Build the project.
2.  Fix compilation errors.
3.  Fix relevant warnings.
4.  Verify Media3 dependency versions.
5.  Verify manifest.
6.  Verify service declaration.
7.  Verify foreground-service configuration.
8.  Verify notification channel.
9.  Verify MediaSession connection.
10. Verify playback.
11. Verify lifecycle.
12. Verify Active Focus integration.

If something could not be physically tested, clearly say so.

Do not claim device testing when only static inspection was performed.

------------------------------------------------------------------------

# 78. PERFORMANCE REVIEW

Before completing:

Check:

-   CPU usage
-   memory usage
-   waveform rendering
-   recomposition
-   service lifecycle
-   player lifecycle
-   coroutine cancellation
-   MediaController lifecycle

Do not leave unnecessary observers running when Active Focus is not
visible.

------------------------------------------------------------------------

# 79. IMPORTANT ARCHITECTURE RULE

There must be one source of truth for playback.

Do NOT create:

``` text
UI playback state
+
ViewModel playback state
+
Service playback state
+
ExoPlayer playback state
```

with conflicting values.

The MediaSession/MediaController should be the authoritative playback
state, while the ViewModel derives appropriate UI state from it.

------------------------------------------------------------------------

# 80. IMPORTANT ARCHITECTURE RULE FOR FOCUS

Focus Session and Media Playback are separate state machines.

Do not make:

``` text
Music stopped → Focus stopped
```

or:

``` text
Focus paused → Music automatically paused
```

unless the product explicitly defines that behavior.

Keep them independent.

------------------------------------------------------------------------

# 81. SUCCESS CRITERIA

Phase 4 is complete only when:

1.  Real Media3 playback works.
2.  ExoPlayer is owned by the playback service.
3.  MediaSession is correctly configured.
4.  MediaController connects from Active Focus.
5.  Play/Pause works.
6.  Stop works.
7.  Previous works.
8.  Next works.
9.  Loop works.
10. Playlist works.
11. Track metadata appears correctly.
12. Playback state is reflected correctly in UI.
13. Background playback works where Android permits.
14. Media notification/system controls work appropriately.
15. Notification permission is handled correctly.
16. No microphone permission exists.
17. No unnecessary storage permission exists.
18. Audio focus is handled.
19. Audio interruptions are handled.
20. Headset/media buttons work where supported.
21. Rotation does not recreate the player unnecessarily.
22. Activity recreation does not lose playback.
23. Focus timer remains independent.
24. Stop does not end Focus.
25. Music does not dominate the clock.
26. Waveform is subtle and performant.
27. No obvious memory leaks exist.
28. No unnecessary wake locks exist.
29. No VPN, Accessibility, Device Owner, or app-blocking code was
    introduced.
30. The existing Phase 3 Active Focus design remains visually intact.

------------------------------------------------------------------------

# 82. PHASE COMPLETION REPORT

When finished, report:

1.  Existing architecture inspected.
2.  Media3 dependencies added/updated.
3.  Files created.
4.  Files modified.
5.  MediaSessionService implementation.
6.  ExoPlayer implementation.
7.  MediaController implementation.
8.  Playlist implementation.
9.  Playback controls.
10. Repeat/loop implementation.
11. Notification implementation.
12. Foreground-service implementation.
13. Audio-focus behavior.
14. Waveform implementation.
15. DataStore changes.
16. Manifest changes.
17. Permissions added/removed.
18. Runtime tests performed.
19. Lifecycle tests performed.
20. Performance observations.
21. Known limitations.
22. Anything Phase 5 needs to know.

Then STOP.

Do not automatically implement Phase 5.

Wait for the next instruction.

------------------------------------------------------------------------

# FINAL PRODUCT PRINCIPLE

Never lose sight of the core experience:

The user should be able to place the phone in front of them and see a
beautiful, large clock.

The music should feel like an ambient layer.

The waveform should feel like a subtle living detail.

The Focus Timer should remain secondary.

The playback controls should be easy to use but visually quiet.

The screen should feel calm for hours.

The goal is NOT:

"Build a music player with a clock."

The goal is:

"Build a Focus Clock whose ambient audio experience feels naturally
integrated into the clock."
