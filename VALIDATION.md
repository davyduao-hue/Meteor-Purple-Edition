# v1.5.0 validation · 5 November 2026

## Build and package

Local Java 25 / Minecraft 26.2 build passes. Nine automated tests pass: palette contrast (body, selected surface, control and secondary text across all ten themes), one-shot button feedback and reduced motion, bounded spring integration/settling, loading particle bounds, exact reduced-motion progress, and merged corner coverage for every radius from 0 through 96.

The package verifier checks the official nested Meteor/Catppuccin/Fabric API versions, absence of Minecraft classes or bundled Glazed, all six panorama face hashes and dimensions, the bootstrap type atlas, ten icon atlas dimensions/hashes, all 260 symbol slots, and the bundled Fredoka OFL license. Complete pinned upstream corresponding sources are packaged separately.

## Actual game checks

| Runtime | Result |
| --- | --- |
| Standalone bundled client | PASS · 169 modules |
| Bundle + official Glazed 17.2 + nested ExploitPreventer 1.1.0 + official OpSec 1.1.7.1 | PASS · 249 modules |

Both full runs exercised native main-menu actions, quick motion presets and overrides, native options/sub-options/sliders, all ten redesigned classic themes, actual tab navigation, category bounds, custom accent, editable anchored HUD, HUD persistence/toggles, optional panorama, native menu fallback, official addon settings/status, an actual resource reload, actual saved-world entry, game HUD feedback, pause screen and the native pause/options action.

The widget audit verifies the real theme factories and actual native mouse handlers: left-click module toggle and restoration, right-click module settings, a single button callback, switch input, exact native slider values, dropdown selection, packed icon UVs and reduced-motion behavior. It also verifies the bundled typeface and configured-font fallback without changing the global font preference. Native client-theme fallback is tested separately from the redesigned Classic Meteor palette.

Screenshots under docs/screenshots/v1.5.0 are native game captures. Visual inspection covers the default menu, full classic interface, pearl theme/icon contrast, live gallery, loading and optional game pause UI. Loading percentages are actual progress values. A targeted resource-reload check follows the final percentage-spacing adjustment. The standalone run captures 65 source menu frames; the local GIF merges duplicate frames and is a visual preview, not a frame-rate measurement. GPU readback affects capture cadence.

## Rendering improvement and practical limits

An in-process comparison extracts 200 identical 28×24, radius-7 panels at GUI scale 2. The individual-strip path submits **5,800 native GUI states**; the new rounded-panel path submits **200**: one state per surface, about **96.6% fewer**. Raw measurements are preserved in docs/screenshots/v1.5.0/render-budget.txt. This isolates panel extraction; it does not measure whole-menu GPU cost, in-world performance or a gameplay FPS multiplier.

Merged spans preserve the former rounded-corner pixel coverage and reduce duplicate geometry. The module-row implementation also avoids copying and scanning an entire category for every row initialization. Text measurements, palettes, icon regions and geometry are cached; resting springs return immediately. Motes and sparks have fixed limits, and native loaders never wait for an animation. The optional colosseum cubemap remains unloaded on the default logo backdrop and is released on return from the panorama.

Theme glyphs use ten compact atlases packed once with Meteor's GUI, rather than hundreds of separately registered GPU images. Colored icons preserve caller opacity while avoiding text-color multiplication, which was checked in the light pearl theme. The client typeface replaces the theme renderer and reuses native batching; no system font is installed and the user's global Meteor font selection is retained. Performance/reduced-motion options suppress decorative effects and upstream Catppuccin transitions as well as the new feedback.

Window entrance offsets are applied only during drawing and restored afterward; saved drag positions and native collapse/scroll state remain owned by Meteor/Catppuccin. Native control actions, permissions and persistence remain in use. Other addons' custom Minecraft screens are excluded from the native-menu repaint scope. Glazed and OpSec code/protection settings are not modified; OpSec's recognized EP compatibility flag is checked and its upstream soft conflict warning is retained.

Tests use isolated offline QA profiles on the existing Radeon RX 580. Existing OSHI performance-counter and offline authentication warnings remain. This is not live-server protection certification or a Realms/account authentication test. No registry, service, driver, security setting or normal launcher profile is changed.
