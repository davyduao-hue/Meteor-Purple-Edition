# Validation evidence

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
