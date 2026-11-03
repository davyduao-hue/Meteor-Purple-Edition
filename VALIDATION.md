# v1.4.0 validation — 3 November 2026

## Build and invariants

Java 25 / Minecraft 26.2 / Fabric Loom 1.17.11. Local Gradle build passes. Existing palette contrast and button/reduced-motion checks pass. New spring tests check settling after a long frame stall, finite bounded values and return to rest after an impulse. The package verifier checks nested official Meteor/Catppuccin/Fabric API versions, six optional panorama face hashes/dimensions and the bootstrap typography atlas dimensions.

## Actual game validation

| Configuration | Result |
| --- | --- |
| Bundle + official Glazed 17.2 + nested ExploitPreventer 1.1.0 + official OpSec 1.1.7.1 | PASS; 249 modules |
| Standalone bundle | PASS; 169 modules |

The full runtime harness exercised the default Meteor menu, native singleplayer/options actions, selectable optional colosseum and return to Meteor, all ten themes, custom accent, HUD installation/anchoring/toggles/persistence, performance-mode bypasses, addon settings, vanilla-menu fallback, actual resource reload and actual saved-world entry. The standalone run additionally asserted UI animation on/off, game motion opt-in and reduced-motion override. A real game HUD was rendered with in-game feedback enabled, then the option was disabled again.

Visual inspection confirms that the custom menu has only the Meteor header mark, smooth rounded button typography, restrained depth and clean spacing. Actual native screenshots show the redesigned resource and world loading presenters with the Comfortaa title/tagline, pill-shaped progress, end cap and small sparks. The typography atlas is rendered from the bundled OFL font by tools/render_brand_atlas.py, not an AI picture. It is loaded directly at bootstrap, before the native font reload is ready. Native reload, completion, errors, readiness and action timing are retained; no artificial delay was introduced.

A 65-frame, 20 FPS menu preview was captured directly from the game during entrance and focused-button feedback. The local preview GIF is an encoding of those frames, not a reconstruction. Recording adds readback overhead, so it is not an FPS benchmark.

## Performance and scope

The default menu renders seven cached logo faces, a separate shadow and bounded native fills. The colosseum's extra 24 MiB base GPU image is registered only when selected and released on returning to Meteor. Its six previously verified Minecraft/shader captures remain packaged. No live world or shader pack runs behind the menu.

Animation uses bounded spring integration and six reusable loading particles. Glyphs, label components, geometry and colors are cached. HUD strings/measurements remain at 4 Hz (2 Hz in performance mode). Game motion is off by default and only affects Zenith HUD presentation and Minecraft pause/options buttons; camera, player position, physics and hitboxes are not changed. Editor positions remain static. Reduced motion/performance mode suppress effects, including during bootstrap via a tiny namespaced preference cache read once.

Checks ran in isolated offline QA instances on a Radeon RX 580. Existing OSHI performance-counter and offline authentication warnings were observed; they are unrelated to the client changes. No Windows registry, service, driver, security setting or normal launcher profile was changed. No controlled gameplay FPS-gain claim, live-server protection certification or Realms/account-authentication claim is made. OpSec's upstream EP metadata warning remains, with its recognized compatibility mode active in the combined-addon run.
