# Included software and corresponding source

The distributable is a Fabric jar-in-jar bundle. It includes the **unmodified official** Meteor Client 26.2-35, Catppuccin 2.4.0+mc26.2 and Fabric API 0.154.2+26.2 binaries. Their nested libraries, resources, licenses and mod metadata are preserved. Fabric Loader and Java are installed separately. This is an independent distribution, not an official upstream release.

Meteor and Catppuccin are GPL-3.0. A copy of GPL-3.0 is provided as LICENSE_meteor-purple-edition inside the JAR; the upstream binaries retain their own license files. Full pinned source archives, including upstream build scripts and assets, are supplied in the `*-corresponding-sources.zip` release attachment, alongside Purple Edition's complete source and build files.

| Component | Official project | Exact source revision |
| --- | --- | --- |
| Meteor Client 26.2-35 | https://github.com/MeteorDevelopment/meteor-client | 2385f7b4bb9de1a698b4503254bd32f30fd7a87d |
| Catppuccin 2.4.0 | https://github.com/X-C-0/catppuccin-addon | eaf2d93cb39db9261d490fd36c76aa8333a7fb79 |
| Fabric API 0.154.2+26.2 | https://github.com/FabricMC/fabric | Apache-2.0; upstream license and notices retained in the nested binary |

The build downloads these sources from immutable revision URLs and verifies their SHA-256 hashes. See build.gradle for the source archive checksums and Maven coordinates. The project release carries these source files directly, rather than relying only on links to upstream repositories.

Glazed is **not included**: its All Rights Reserved license prohibits redistribution and incorporation in distributed software. It can be installed from its official release page at https://github.com/realnnpg/glazed/releases. Classic Meteor/Catppuccin category windows display its registered modules without copying Glazed code. OpSec is also an optional separate install. Glazed bundles ExploitPreventer 1.1.0. OpSec 1.1.7.1 retains a soft conflict declaration but implements EP compatibility mode: overlapping features defer to EP. Purple checks the official compatibility flag without modifying either protection engine. The upstream Fabric warning is retained.

Gradle wrapper files are Apache-2.0; see LICENSE-GRADLE and NOTICE-GRADLE in the source repository. The underlying Minecraft game is not included.

The menu font is Comfortaa, Copyright 2011 The Comfortaa Project Authors, distributed under the SIL Open Font License 1.1. Its full license is included at `licenses/comfortaa-ofl.txt`. The unmodified TTF is the same font distributed by the pinned Meteor source. Official font project: https://github.com/googlefonts/comfortaa.

The menu Meteor mark is a native vector rendition of the seven purple faces in the bundled Meteor logo. Its geometry and upstream GPL attribution are preserved through the pinned Meteor corresponding-source archive. It is not an official Meteor endorsement.

## Minecraft panorama captures

The v1.3.0 panorama consists of original screenshots of the project's custom Minecraft block-built colosseum, captured with **Iris 1.11.4+26.2-fabric**, **Sodium 0.9.2+mc26.2** and **Complementary Reimagined r5.9.3**. The shader was obtained from its official [Modrinth project](https://modrinth.com/shader/complementary-reimagined); official creator site: https://www.complementary.dev/. Complementary License Agreement 1.7 section 1.1 permits normal usage for content creation. No shader code, Iris or Sodium binaries are distributed in this addon; only the rendered screenshots and our original scene/capture code are included. The capture records and SHA-256 image hashes are in textures/panorama/capture.json inside the addon assets.

The v1.4.0 bootstrap typography atlas is rasterized directly from the included Comfortaa OFL font. The original font and full OFL license remain included; the reproducible glyph-atlas script is tools/render_brand_atlas.py. The default Meteor mark uses the cached seven-face vector geometry credited above. The Minecraft colosseum captures are now optional.
