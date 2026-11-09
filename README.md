# Long NBT Killer

Long NBT Killer helps locate excessively nested NBT in Minecraft saves. Install the JAR for your Minecraft version and loader on the client for single-player worlds or on the dedicated server that reads the save.

Fabric also requires [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port) and [Fabric API](https://modrinth.com/mod/fabric-api) for your Minecraft version.

| Minecraft | Loaders | Java |
| --- | --- | --- |
| 1.21.11 | Fabric, NeoForge | 21 |
| 26.1.2, 26.2, 26.3 | Fabric, NeoForge | 25 |

## Find the affected data

Look for `Excessive NBT nesting` in `logs/latest.log`. The warning includes the file, the depth reached and, when available, a path to the first compound or list that crosses the limit. Region warnings also include the dimension and chunk coordinates. A shortened path shows the beginning and end of the branch.

Minecraft normally rejects NBT nested beyond 512 levels. Some mods can add another layer to the same data each time a world is saved, so identify and update the mod that writes the data before repairing the save. Back up the world, then use the reported file and path in an NBT editor. For example, recursively nested terrain rules can be stored in `level.dat` on older worlds and in `data/minecraft/world_gen_settings.dat` on 26.x. See the [terrain rule repair example](https://github.com/MCreator/MCreator/issues/6088#issue-3943023251).

## Configuration

Edit `config/longnbtkiller-common.toml` on the server, or in your Minecraft instance for single-player.

| Setting | Default | Effect |
| --- | ---: | --- |
| `maxDepth` | `2147483647` | Maximum depth for supported save reads. Use `512` to restore the vanilla limit. |
| `warningIntervalTicks` | `1200` | Send the first warning as soon as the server detects deep NBT and has online players. Repeat every 1,200 server ticks after sending, about one minute at 20 TPS. Set to `0` to disable chat reminders. |

The warning threshold remains 512 even when the configured read limit is higher. Back up the world before repairing it. Extremely deep data can still exhaust the Java stack during reading, world loading, or saving.

## Build

Run `./gradlew buildAndCollect` (or `gradlew.bat buildAndCollect` on Windows) with JDK 25. Gradle uses JDK 21 for the 1.21.11 targets. Release JARs are collected in `build/libs`.
