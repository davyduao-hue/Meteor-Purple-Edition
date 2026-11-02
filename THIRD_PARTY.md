# Included software and corresponding source

The distributable is a Fabric jar-in-jar bundle. It includes the **unmodified official** Meteor Client 26.2-35, Catppuccin 2.4.0+mc26.2 and Fabric API 0.154.2+26.2 binaries. Their nested libraries, resources, licenses and mod metadata are preserved. Fabric Loader and Java are installed separately. This is an independent distribution, not an official upstream release.

Meteor and Catppuccin are GPL-3.0. A copy of GPL-3.0 is provided as LICENSE_meteor-purple-edition inside the JAR; the upstream binaries retain their own license files. Full pinned source archives, including upstream build scripts and assets, are supplied in the `*-corresponding-sources.zip` release attachment, alongside Purple Edition's complete source and build files.

| Component | Official project | Exact source revision |
| --- | --- | --- |
| Meteor Client 26.2-35 | https://github.com/MeteorDevelopment/meteor-client | 2385f7b4bb9de1a698b4503254bd32f30fd7a87d |
| Catppuccin 2.4.0 | https://github.com/X-C-0/catppuccin-addon | eaf2d93cb39db9261d490fd36c76aa8333a7fb79 |
| Fabric API 0.154.2+26.2 | https://github.com/FabricMC/fabric | Apache-2.0; upstream license and notices retained in the nested binary |

The build downloads these sources from immutable revision URLs and verifies their SHA-256 hashes. See build.gradle for the source archive checksums and Maven coordinates. The project release carries these source files directly, rather than relying only on links to upstream repositories.

Glazed is **not included**: its All Rights Reserved license prohibits redistribution and incorporation in distributed software. It can be installed from its official release page at https://github.com/realnnpg/glazed/releases. The unified UI discovers its modules through Meteor's public module API without copying Glazed code. OpSec is also an optional separate install. Glazed's bundled ExploitPreventer has a declared conflict with OpSec 1.1.7.1; Purple Edition does not silently strip or disable either mod.

Gradle wrapper files are Apache-2.0; see LICENSE-GRADLE and NOTICE-GRADLE in the source repository. The underlying Minecraft game is not included.

The menu font is Comfortaa, Copyright 2011 The Comfortaa Project Authors, distributed under the SIL Open Font License 1.1. Its full license is included at `licenses/comfortaa-ofl.txt`. The unmodified TTF is the same font distributed by the pinned Meteor source. Official font project: https://github.com/googlefonts/comfortaa.
