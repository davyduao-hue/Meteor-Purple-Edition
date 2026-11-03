# Meteor Purple Edition v1.2.0 · Zenith HUD

- Familiar vanilla main menu and real vanilla actions, with a static purple Meteor backdrop replacing the panorama.
- Subtle accent underline and one-shot hover shine; custom accent color and reduced motion.
- Quieter theme surfaces, clearer secondary text; classic client UI only.
- Movable Zenith Client/FPS bar at the top left and compact frame-time, ping and JVM-heap panel at the bottom left. Toggle and edit them in Meteor's HUD editor.
- Blur and window shadows off by default; bounded HUD refresh and cached colors/measurements.
- Glazed/ExploitPreventer/OpSec status and upstream EP compatibility detection.

Minecraft 26.2 / Java 25 / Fabric Loader 0.19.3+. Meteor, Catppuccin and Fabric API are bundled. Replace the old Purple JAR and remove duplicate standalone bundled mods. Glazed and OpSec remain separate official installs. Glazed includes EP; avoid adding another copy.

OpSec still declares a Fabric conflict warning for EP, although its release includes a compatibility mode. Purple checks that mode and never strips or disables a protection mod. Validation covers startup, menus and HUD behavior; live-server protection coverage and a gameplay FPS gain are not certified. Full details are in VALIDATION.md.
