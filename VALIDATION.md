# v1.2.0 validation — 3 November 2026

## Build and package

Java 25 / Fabric Loom 1.17.11 / Minecraft 26.2. `gradlew.bat build` passed locally. JUnit body/selected-surface contrast checks pass for all ten palettes; animation tests verify settling, one-shot shine, re-entry and reduced-motion behavior. `scripts/verify_bundle.py` verified the actual nested mod IDs and versions and absence of Minecraft classes or bundled Glazed.

## In-game smoke tests

An isolated offline QA profile rendered the real game at 1280×800 on a Radeon RX 580. No normal launcher account, world or user instance was changed. The reproducible harness is in qa/bundle-smoke.

| Configuration | Observed result |
| --- | --- |
| Distributed bundle alone | PASS; 169 Meteor modules |
| Bundle + official Glazed 17.2 (26.2) + its nested ExploitPreventer 1.1.0 + official OpSec 1.1.7.1 (26.2) | PASS; 249 Meteor modules; OpSec EP compatibility flag true |

Assertions and captured screens cover native singleplayer action (an empty instance correctly opens Minecraft's Create World screen), Minecraft options, theme gallery, every classic palette, custom accent and focused-button underline, HUD installation without duplicates, bottom anchoring and viewport bounds, individual metric toggles, element on/off and serialized restoration, performance animation/blur bypass and vanilla main-menu fallback. The combined-addon test also opens the official OpSec configuration screen; its overlapping settings display “Managed by ExploitPreventer.”

Visual QA found and corrected an initially tiled logo. The final logo uses seven cached native vector faces so it stays sharp without large pixel blocks or a panorama render pass. Light themes keep a dark menu backdrop so native Minecraft text remains readable.

## Performance scope and limitations

Purple adds no HUD shaders, polling threads or network work. Normal HUD data refresh is bounded at 4 Hz, performance mode at 2 Hz. Measurements/colors/corner geometry are cached. Frame-interval accumulation is two arithmetic operations per frame. Optional Catppuccin blur/shadows default off. A controlled in-world FPS benchmark was not performed, so no percentage FPS gain or zero-cost claim is made.

The QA instance used cached assets because Loom's online asset download verification stalled. 181 uncached assets were music/audio; GUI assets were present. Runtime assertions were executed, not skipped. Offline Realms authentication warnings are expected. This machine also emitted existing OSHI Windows performance-counter warnings, which were outside Purple and did not prevent the tests. Windows settings were not modified to suppress them.

OpSec retains a soft `conflicts` metadata declaration for EP despite implementing compatibility mode; Fabric's warning remains. Purple does not suppress the warning or change the protection engines. Live server joins, account/Realms authentication and full protection behavior against hostile servers were not tested. Glazed may supply its own background in other menu screens; its Background Switcher setting remains under Glazed's control.

The menu shine is tested for hover/focus transitions. Screenshots capture a settled underline, rather than claiming a still image proves animation timing. Existing customized HUD layouts are preserved and can be adjusted in Meteor's editor.
