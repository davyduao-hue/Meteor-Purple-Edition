# v1.3.0 validation — 3 November 2026

## Build and package

Java 25 / Fabric Loom 1.17.11 / Minecraft 26.2. `gradlew.bat build` passed locally. JUnit body/selected-surface contrast checks pass for all ten palettes; animation tests verify settling, one-shot shine, re-entry and reduced-motion behavior. `scripts/verify_bundle.py` verified the actual nested mod IDs and versions and absence of Minecraft classes or bundled Glazed.

## In-game smoke tests

An isolated offline QA profile rendered the real game at 1280×800 on a Radeon RX 580. No normal launcher account, world or user instance was changed. The reproducible harness is in qa/bundle-smoke.

| Configuration | Observed result |
| --- | --- |
| Distributed bundle alone | PASS; 169 Meteor modules |
| Bundle + official Glazed 17.2 (26.2) + its nested ExploitPreventer 1.1.0 + official OpSec 1.1.7.1 (26.2) | PASS; 249 Meteor modules; OpSec EP compatibility flag true |

Assertions and captured screens cover native singleplayer action (an empty instance correctly opens Minecraft's Create World screen), Minecraft options, theme gallery, every classic palette, custom accent and focused-button underline, HUD installation without duplicates, bottom anchoring and viewport bounds, individual metric toggles, element on/off and serialized restoration, performance animation/blur bypass and vanilla main-menu fallback. The combined-addon test also opens the official OpSec configuration screen; its overlapping settings display “Managed by ExploitPreventer.”

The v1.3.0 main menu uses the actual six-face Minecraft colosseum capture. The 160×132 oval was generated in a separate native Fabric 26.2 flat world (seed 6582), with three arcades, 48 bays, stepped seating, a purple monument and a ruined western moon sightline. Iris 1.11.4, Sodium 0.9.2 and Complementary Reimagined r5.9.3 were downloaded from official Modrinth URLs and SHA-512 verified. The capture harness asserted Iris's shader pack was active and each camera FOV was 90 degrees. All six final PNGs are 1024×1024 and were copied without pixel edits; the shipped capture.json records their SHA-256 values. Frozen dawn tick: 22500.

The final panorama, native menu actions, ten themes, HUD and addon settings passed in-game with 249 modules. A real resource reload was performed and its Zenith title, exact tagline, dark background and lavender progress were visually inspected. The actual saved colosseum was then opened with Glazed/EP/OpSec installed; world entry and HUD rendering passed. World loading also displays the Zenith title and tagline. Its background is styled separately from Glazed's general menu background. Native reload/terrain progress and completion handlers are retained.

A native graphics-process exit occurred during an unused alternate shader capture preset; the validated preset captured successfully. The final menu requires neither Iris nor an active shader pack. No drivers, registry settings, overlays or Windows services were changed.

## Performance scope and limitations

The captured panorama uses Minecraft’s native cubemap pipeline, with approximately 24 MiB of base GPU texture storage. Shading, bloom and cast shadows are baked into the screenshots; no live world or shader pack is run behind the menu. Performance and reduced-motion settings freeze camera rotation. Purple adds no HUD shaders, polling threads or network work. Normal HUD data refresh is bounded at 4 Hz, performance mode at 2 Hz. Measurements/colors/corner geometry are cached. Frame-interval accumulation is two arithmetic operations per frame. Optional Catppuccin blur/shadows default off. A controlled in-world FPS benchmark was not performed, so no percentage FPS gain or zero-cost claim is made.

The QA instance used cached assets because Loom's online asset download verification stalled. 181 uncached assets were music/audio; GUI assets were present. Runtime assertions were executed, not skipped. Offline Realms authentication warnings are expected. This machine also emitted existing OSHI Windows performance-counter warnings, which were outside Purple and did not prevent the tests. Windows settings were not modified to suppress them.

OpSec retains a soft `conflicts` metadata declaration for EP despite implementing compatibility mode; Fabric's warning remains. Purple does not suppress the warning or change the protection engines. Live server joins, account/Realms authentication and full protection behavior against hostile servers were not tested. Glazed may supply its own background in other menu screens; its Background Switcher setting remains under Glazed's control.

The menu shine is tested for hover/focus transitions. Screenshots capture a settled underline, rather than claiming a still image proves animation timing. Existing customized HUD layouts are preserved and can be adjusted in Meteor's editor.

Final loading presentation was checked again with all optional addons (249 modules), then with the standalone bundle (169 modules). Both real resource reload and real colosseum world entry completed. The final world-loading background uses the Zenith dark gradient and retains native terrain progress.
