# Changelog

## [Unreleased]

### Added

- Added Fabric and NeoForge builds for Minecraft 1.21.11, 26.1.2, 26.2, and 26.3.
- Log the file, depth, and first over-limit NBT path when available. Include dimension and chunk coordinates for region data.
- Added a shared TOML configuration and in-game warnings that start immediately when a save read exceeds 512 levels and repeat at a configurable interval.

### Changed

- Default the depth limit for supported save reads to `Integer.MAX_VALUE`. Preserve Minecraft's network NBT depth and byte limits.
- Require Forge Config API Port and Fabric API on Fabric. Use NeoForge's built-in configuration system on NeoForge.
- Build the supported Minecraft and loader combinations from one Stonecutter project.
