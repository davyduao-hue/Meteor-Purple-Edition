# Validation evidence — 1.1.0

Verified on 2 November 2026 with Java 25, Gradle 9.7.1, Fabric Loom 1.17.11 and Minecraft 26.2.

## Build and package checks

- `gradlew.bat build --console=plain` passes locally, including the JUnit contrast test for body text against base and selected surfaces in every palette.
- `scripts/verify_bundle.py` opens the built JAR and checks the real nested mod metadata: Meteor 26.2-35, Catppuccin's 26.2 release binary and Fabric API 0.154.2+26.2. Glazed is absent from the distributed bundle. No Minecraft classes are packaged at the outer JAR root.
- The corresponding-source archive contains the complete Purple project and the exact Meteor/Catppuccin upstream source archives. Source archive SHA-256 checks pass. Font licensing and Gradle notices are included.

## Actual game tests

An isolated Fabric project in `qa/bundle-smoke` loads **only the assembled Purple Edition JAR** as its runtime mod dependency; Meteor/Catppuccin/Fabric API are compile-only dependencies of the QA harness. The game resolves the three real bundled mods recursively from the nested JARs. No real account token is used.

The base bundle loaded **169 modules**. With official `glazed-26.2-n-17.2.jar` installed separately, it loaded **249 modules**. In-game tests rendered all ten palettes, the unified hub, theme gallery, HUD editor, original Meteor widgets and Classic category windows. The test checks actual module search results and an unmatched query, menu replacement/fallback, Unified/Classic routing and the performance-mode blur bypass. Runs completed normally with a PASS result and screenshots taken from the real render target.

Actual screenshots were reviewed. The review led to removing text shadows from the light menu, adding the licensed Comfortaa font, smoothing corner geometry and removing a hard gradient seam. The unified hub uses its own coordinated background in menus, while retaining the world background and native blur during gameplay.

The observed GPU was a Radeon RX 580 with the OpenGL 3.3 backend. These results do not certify other GPUs, all screen scales, every keybind interaction, accessibility or every upstream gameplay feature. UI rendering is tested; there is no comparative FPS benchmark. The test uses only English UI assets and omits some sound downloads. Development-mode Realms authentication errors and an unrelated Windows performance-counter warning are not treated as successful online-server tests.

## Optional addon limitations

Glazed remains an external install because its license prohibits redistribution. The Glazed integration run omitted OpSec: Glazed's nested ExploitPreventer 1.1.0 is explicitly marked as conflicting by OpSec 1.1.7.1. The UI exposes that warning when both are installed. It does not strip dependencies or disable protections. Live-server behavior and all individual Glazed modules were not tested.

## CI

The 1.1 workflow builds/tests on Windows and Linux, validates the wrapper, verifies nested mod metadata and publishes the corresponding source ZIP alongside the JAR. The authoritative status is on the repository Actions page. The initial release run for 1.0 is retained below as historical evidence, not a claim about 1.1 CI.

# Historical validation — 1.0.0

Verified on 2 November 2026.

## Local build

`gradlew.bat build --console=plain` completed successfully with Gradle 9.7.1, Fabric Loom 1.17.11 and Temurin Java 25.0.4.1 on Windows. The compiler checked the full addon against the published Minecraft 26.2, Meteor 26.2 and Catppuccin 2.4.0+mc26.2 APIs. Both the binary JAR and source JAR were generated under `build/libs/`.

The build has no unit-test suite. A successful compilation does not demonstrate graphics correctness, gameplay compatibility, smooth performance, or that every original requirement is complete.

## Upstream verification

- Official Meteor `26.2-SNAPSHOT` metadata resolves to timestamped publication `26.2-20261009.074120-35`.
- Official Meteor's last pre-26.3 source revision inspected: `2385f7b4bb9de1a698b4503254bd32f30fd7a87d`. It declares Minecraft 26.2, Java 25, Loader 0.19.3 and Fabric API 0.154.2+26.2.
- Catppuccin official release v2.4.0 includes a real 26.2 JAR. Modrinth project/version identifiers are `3Q35hPRA` / `TAH6i1zw`. Its public theme API was inspected from that binary.
- Official Glazed release v17.2 includes `glazed-26.2-n-17.2.jar`. The default source branch still declares 1.21.11, so the default branch was not treated as a 26.2 source port. License forbids redistribution and incorporation in distributed projects. No Glazed code/binary is included.
- Original OpSec CurseForge project is `opsec`, ID 1519411, author aurickk, GPLv3; its official GitHub release is V1.1.7.1 with a 26.2 binary. It is distinct from similarly named community and enhanced forks.

## Runtime and CI

An isolated `gradlew.bat runClient -x downloadAssets` session loaded Minecraft 26.2 and all five requested mods: Meteor 26.2-35, Catppuccin's 26.2 binary, Purple Edition, OpSec 1.1.7.1 and Glazed 26.2-n-17.2. The official non-audio English UI assets were downloaded and SHA-1 verified separately; sound files were intentionally incomplete for this UI smoke test. Meteor and Catppuccin initialized, resource atlases loaded, and the actual Minecraft/Glazed title screen with the installed addon credits was observed.

Computer interaction was stopped by the user with Escape. Theme switching, blur appearance, HUD dragging, a world session and individual Glazed modules have **not** been visually/gameplay verified. Development-mode Realms authentication errors were expected because no real account token was used. This is a startup smoke test, not a complete runtime pass.

The corrected release commit `95196e3b0b003598cfea6d4993a327d1aec10386` passed **both Windows and Linux builds**, including official Gradle wrapper validation and JAR artifact uploads. The release job also succeeded and published **Meteor Purple Edition v1.0.0** as a prerelease with binary and source JARs.

- [Successful cross-platform build and release run](https://github.com/davyduao-hue/Meteor-Purple-Edition/actions/runs/41515309990)
- [Published prerelease](https://github.com/davyduao-hue/Meteor-Purple-Edition/releases/tag/v1.0.0)

The initial Linux run's line-ending failure is retained in the repository history; the corrected run above is the release evidence. Later documentation and distribution-notice changes do not imply additional gameplay testing.

## Unimplemented specification items

Three independently registered native Meteor themes, a separately reimplemented ClickGUI, benchmark-backed performance claims, and a modified Glazed/OpSec fork are not claimed. Existing Catppuccin supplies the real ClickGUI. Purple Glass now implements adjustable native background blur through Minecraft's per-frame render state. Optional mods preserve their upstream behavior and are installed separately.

The first combined runtime attempt exposed and led to a fix for a Fabric version constraint rejecting Meteor's real `26.2-35` version. The first Linux CI run exposed CRLF line endings in `gradlew`; the file was normalized and `.gitattributes` added. Both issues would not be caught by Java compilation alone.

Fabric's combined-mod resolution reports that Glazed bundles ExploitPreventer 1.1.0, which conflicts with OpSec 1.1.7.1. This is a real upstream warning and remains a compatibility limitation; no nested third-party binary was removed or modified.
