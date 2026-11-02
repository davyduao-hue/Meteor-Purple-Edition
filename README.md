# Meteor Purple Edition

A Minecraft **26.2 / Fabric / Java 25** addon for the real Meteor Client and Catppuccin addon. It adds three live appearance profiles, nine independent purple HUD elements, and a defensive integration with the official OpSec configuration API.

This is an independent addon, not an official Meteor, Catppuccin, Glazed, or OpSec release. Upstream mods remain separate and retain their modules, commands, keybinds, profiles and configuration files.

## Installation

1. Install Minecraft **26.2**, **Java 25**, and Fabric Loader **0.19.3 or later**.
2. Install the following official mods in the same instance's `mods` directory:
   - [Meteor Client](https://meteorclient.com/) **26.2**, using the official 26.2 build, not a 26.3 build. The build is compiled against official Maven snapshot `26.2-20261009.074120-35`.
   - [Fabric API](https://modrinth.com/mod/fabric-api) **0.154.2+26.2**.
   - [Catppuccin Addon 2.4.0 for 26.2](https://github.com/X-C-0/catppuccin-addon/releases/tag/v2.4.0): `catppuccin-addon-2.4.0+mc26.2.jar`.
3. Add `meteor-purple-edition-1.0.0.jar` from this project's [Releases](https://github.com/davyduao-hue/Meteor-Purple-Edition/releases) or a successful [Actions build](https://github.com/davyduao-hue/Meteor-Purple-Edition/actions).
4. Optional: install [official OpSec](https://www.curseforge.com/minecraft/mc-mods/opsec), `opsec-26.2+v1.1.7.1.jar`, from [aurickk/OpSec](https://github.com/aurickk/OpSec/releases/tag/V1.1.7.1).
5. Optional: install [official Glazed v17.2](https://github.com/realnnpg/glazed/releases/tag/v17.2), choosing **`glazed-26.2-n-17.2.jar`**, together with the dependencies declared by that official build (including its matching Baritone and Fabric API). Glazed is not bundled, modified, or redistributed here. Its license prohibits redistribution.

Do not install duplicate Meteor or Catppuccin versions. No third-party binaries are nested in the Purple Edition JAR.

## Appearance and controls

Open Meteor with **Right Shift**. Open **Purple → Purple Appearance** settings (right-click its module). Choose a theme; settings apply even when this control module is toggled off. Click **Apply appearance now** to reapply the preset. Meteor stores these settings in its normal module/profile configuration.

| Profile | Behavior |
| --- | --- |
| Catppuccin Mocha | Actual Catppuccin Mocha palette, lavender accent, rounded panels, soft shadows and configurable animations. |
| Purple Glass | Translucent Catppuccin panels and a purple gradient HUD background. This version does **not** implement a frosted framebuffer blur. |
| Minimal Lavender | Compact scale, small corners, opaque default panels, no shadows and effectively immediate animations. |

GUI scale, panel opacity and animation duration are configurable. Performance mode disables shadows and animations and forces opaque GUI panels. The actual Catppuccin settings remain available in Meteor's GUI tab. Applying a Purple preset intentionally changes only its listed visual settings; it does not reset module configuration.

The real Catppuccin ClickGUI provides rounded, movable category windows, animated controls, searchable module settings, descriptions, sliders, color pickers, keybind editing, tooltips, scrolling and category snapping. Modules installed by other Meteor addons use the same Meteor settings widgets automatically. Purple Edition does not clone or replace these modules.

## HUD

Click **Add missing Purple HUD elements**, then arrange them using Meteor's normal **HUD editor**. The action preserves existing elements and adds only missing Purple elements; it enables the HUD.

Nine independently movable panels provide watermark, FPS, latency, coordinates, active modules, armor durability, potion timers, memory usage and server name. Each has its own scale, background, opacity, theme synchronization and color picker. Text data is refreshed at 4 Hz and list sizes are bounded. Theme colors update immediately while rendering; performance mode avoids gradient HUD drawing.

## OpSec integration

**Purple → Purple Privacy** shows selected protection status, opens the complete official OpSec settings screen, and provides chat-alert/toast controls which save through OpSec's configuration manager. The adapter is optional and reports an unavailable API rather than crashing if a future OpSec version changes it. It never reads account/session tokens or changes protection defaults. OpSec remains the protection engine; this project does not claim to be an enhanced OpSec fork.

## Build

Install a Java 25 JDK. From the repository root on Windows:

```bat
gradlew.bat build
```

Linux/macOS: `bash ./gradlew build`. Gradle 9.7.1 is bootstrapped by the included official wrapper, with a pinned distribution SHA-256. Fabric Loom **1.17.11** uses the unobfuscated Minecraft 26.2 classes; no Yarn mappings are declared. Dependencies use official Fabric, Meteor, Maven Central and Modrinth repositories. A network connection is needed for the first build.

Outputs:

```text
build/libs/meteor-purple-edition-1.0.0.jar
build/libs/meteor-purple-edition-1.0.0-sources.jar
```

GitHub Actions validates the wrapper and builds on both Windows and Linux with Java 25. Each successful run exposes downloadable JAR artifacts. Version tags, or the explicit publish input of the manual workflow, publish a **prerelease** with the compiled JAR and source JAR. This remains a prerelease until runtime and combined-addon compatibility have been verified.

## Compatibility and limitations

- Target is exactly **26.2**. Do not mix with 1.21.11, 26.1.2 or 26.3 binaries.
- The three selections are coordinated presets of the real Catppuccin theme, selectable under Purple Appearance; they are not three independent entries in Meteor's native theme dropdown.
- Purple Glass has transparency and HUD gradients, **not adjustable frosted-glass blur** or a custom blur shader. No performance claims or benchmark results are asserted.
- Glazed is an independently installed official binary. Its 26.2 release exists, but combined runtime behavior and all Glazed features require an actual in-game test. No Glazed source is included.
- OpSec's complete settings retain their upstream screen design; the status and notification controls are embedded in the themed Meteor module screen.
- Existing Meteor HUDs are preserved. The added armor panel is textual durability/status, not a replacement for Meteor's armor icon HUD.
- No screenshots are included unless captured from an actual running client. Build and runtime validation results are recorded in `VALIDATION.md`.

## Credits and license

- [MeteorDevelopment/meteor-client](https://github.com/MeteorDevelopment/meteor-client) — framework, module system, GUI and HUD APIs; GPL-3.0.
- [X-C-0/catppuccin-addon](https://github.com/X-C-0/catppuccin-addon) — actual theme, rounded renderer and widgets; GPL-3.0.
- [Catppuccin](https://catppuccin.com/) — palette.
- [aurickk/OpSec](https://github.com/aurickk/OpSec) — official privacy mod; GPL-3.0. [CurseForge project 1519411](https://www.curseforge.com/minecraft/mc-mods/opsec).
- [realnnpg/glazed](https://github.com/realnnpg/glazed) — optional independent addon; **All Rights Reserved**. Follow its [license](https://github.com/realnnpg/glazed/blob/main/LICENSE); no redistribution permission is implied.
- [Fabric](https://fabricmc.net/) and [Gradle](https://gradle.org/) — loader and build tooling. Wrapper files are distributed by Gradle under Apache-2.0.

Purple Edition's own source is GPL-3.0-only; see `LICENSE`. Upstream binaries and assets retain their respective licenses and are not part of the distributable.
