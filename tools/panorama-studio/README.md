# Actual Minecraft colosseum capture studio

This Fabric 26.2 tool creates a new local flat world named Zenith-Colosseum, builds the 160×132 arena with 246,288 placed block operations and a ruined western arcade, freezes dawn at tick 22500, then photographs six 90-degree views at 1024×1024. It exits after verifying Iris reports an active shader pack. The saved world is in run/saves; images and the capture record are in captures. It does not join any external server or touch a normal launcher instance.

Run `python prepare.py` to fetch the three official, SHA-512-pinned capture dependencies into this tool's own run folder. Then, from this directory, run `../../gradlew.bat runClient` (Windows) or `bash ../../gradlew runClient` (Linux). Requires Java 25 and a graphical session. The first launch needs normal Minecraft assets. With already cached GUI/game assets, `runClient --offline -x downloadAssets` can be used when the asset host is unavailable. An offline studio name is used; authentication warnings are expected.

The scene and all lighting are rendered by the actual game. This tool is not part of the distributed runtime addon. Shader binaries are neither embedded in the addon nor committed to the repository. Complementary is credited in the main README/THIRD_PARTY.md. Its License Agreement 1.7 section 1.1 permits content creation.

The six raw PNGs are copied directly into the addon as colosseum_0.png through colosseum_5.png. Keep equal square sizes and preserve their native camera order. Update capture.json SHA-256 values if capturing again. Do not patch generated pictures or claim a replacement render was made by Minecraft.
