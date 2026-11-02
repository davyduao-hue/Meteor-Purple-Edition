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

Runtime launch and GitHub Actions verification are in progress. Do not interpret this document as a runtime pass until this section is updated with an observed result.

## Unimplemented specification items

Adjustable frosted-glass blur, three independently registered native Meteor themes, a separately reimplemented ClickGUI, benchmark-backed performance claims, and a modified Glazed/OpSec fork are not claimed. Existing Catppuccin supplies the real ClickGUI. Optional mods preserve their upstream behavior and are installed separately.
