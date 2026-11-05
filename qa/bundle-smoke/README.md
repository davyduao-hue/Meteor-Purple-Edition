# Isolated in-game smoke test

Build the parent project first. From this directory, run `../../gradlew.bat runClient --console=plain` on Windows or `bash ../../gradlew runClient --console=plain` on Linux. Requires a graphical session, Java 25 and the normal Minecraft assets. The test uses an offline QA identity and its own run directory, not your launcher profile. It does not join a server or create a world.

It checks native menu actions, ten classic themes, a custom accent, HUD installation idempotence, bottom anchoring, toggles/settings restoration, performance bypasses and the vanilla-menu fallback. Screenshots and RESULT.txt appear in run/screenshots. If Glazed is present, the test also requires OpSec and Glazed's nested ExploitPreventer, verifies OpSec's compatibility flag, and opens the official OpSec screen. Place only official 26.2 addon JARs in run/mods; they are not redistributed here.

If the asset server is unavailable but GUI assets are already cached, `runClient --offline -x downloadAssets` can launch using those assets. That skips Loom's download check, not mod loading or runtime assertions. Missing music files are irrelevant to this UI-only test. Realms and live multiplayer are not authenticated by this offline harness.

The v1.3 harness also performs a real resource reload to capture the Zenith overlay. If run/saves/Zenith-Colosseum exists, it opens that actual world, captures branded terrain loading and the in-world HUD, and sets its spawn to the panorama point. It does not require this optional world for the menu/HUD assertions.


The v1.5 harness includes ClientUiAudit: native widget input, theme factories, typeface/fallback without a global preference change, category bounds and all ten packed icon families. It opens actual tab screens (including the responsive navigation), captures live theme/motion controls, then tests optional world HUD and native pause/options actions. RenderBudget compares native panel extraction states; its timings are not gameplay FPS results.

For a short resource-reload typography check after a presentation-only change, use `runClient -Ppolish --offline -x downloadAssets`. It captures the actual overlay, menu and final category bounds without opening a world. Source and package builds use the prebuilt font/icons; Pillow/fonttools are only needed to reproduce artwork/type assets with the tools scripts.
