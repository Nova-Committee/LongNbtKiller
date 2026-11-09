# Changelog

## [Unreleased]

## [1.1.0] - 2026-11-09

### Added

- Added Fabric and NeoForge builds for Minecraft 1.21.11, 26.1.2, 26.2, and 26.3.
- Added diagnostics that log the file, depth, and first over-limit NBT path when available. Region warnings include the dimension and chunk coordinates.
- Added a shared TOML configuration and in-game warnings when a save read exceeds 512 levels. The server warns online players immediately and repeats every 1,200 ticks by default, directing server administrators to the log.

### Changed

- Default the depth limit for supported save reads to `Integer.MAX_VALUE`. Preserve Minecraft's network NBT depth and byte limits.
- Require Forge Config API Port and Fabric API on Fabric. Use NeoForge's built-in configuration system on NeoForge.
- Build the supported Minecraft and loader combinations from one Stonecutter project.

[Unreleased]: https://github.com/Nova-Committee/LongNbtKiller/compare/v1.1.0...stonecutter-modern
[1.1.0]: https://github.com/Nova-Committee/LongNbtKiller/releases/tag/v1.1.0
