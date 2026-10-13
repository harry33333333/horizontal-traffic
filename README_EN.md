# Horizontal Traffic

[English](README_EN.md) | [简体中文](README.md)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-blue.svg)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-0.16.10+-lightgrey.svg)](https://fabricmc.net/)

**Horizontal Traffic** is an extension add-on mod for [TrafficCraft](https://github.com/MrJulsen/TrafficCraft) on Minecraft 1.20.1 Fabric.

It introduces realistic **horizontal traffic lights** (signals arranged horizontally: Red, Yellow, Green), complete with deep integration and enhanced support for [Aftersans53228's Fabric Roads (aft_fabroads)](https://github.com/Aftersans53228/fabric_roads) pillar systems and 45-degree diagonal mechanics.

---

## 🌟 Key Features

### 1. Horizontal Traffic Lights
- **Multiple Configurations**: Single-lamp, double-lamp, and standard triple-lamp horizontal traffic light blocks.
- **Realistic Alignment & Clean Aesthetics**: Lamps follow real-world road standards from left to right (Red, Yellow, Green). Support pole and bracket models are redesigned with shortened necks and without protruding side visor ears for clean, snug mounting.
- **Accurate Hitboxes & Positioning**: Full and precise VoxelShapes supporting 4 horizontal facings (North, South, East, West) and 3 vertical attachment positions (TOP, CENTER, BOTTOM).
- **Lighting Enhancements**: Blocks emit base ambient light level (level 3), with bulbs rendered at Full Bright for clear night visibility.

### 2. Deep TrafficCraft Integration
- **Signal Controller Linking**: Full support for `Traffic Light Linker` to bind/unbind traffic lights to `Traffic Light Controller`.
- **All Signal Modes Supported**: STATIC, OWN_SCHEDULE, and REMOTE controller modes.
- **Auto-Saving Phase IDs**: Eliminates the vanilla issue where pressing ESC without hitting Enter discards Phase ID changes. Input changes are automatically committed and synced in real-time.
- **Icons & Flashing Animations**: Complete support for directional turn arrows (straight, left, right, divergent arrows) and green flashing animations.

### 3. Integrated Digital Countdown Display
- **Yellow Lens Countdown (1–9s)**: Displays a single-digit countdown timer on the center yellow aspect counting down from 9s to 1s before changing phase (Red phase counts down in glowing Red, Green phase counts down in glowing Green; Yellow phase does not count down).
- **11s Pre-Countdown Alert Blink**: For Red or Green phases lasting at least 12 seconds (≥ 240 ticks), exactly 11 seconds before phase transition (prior to countdown digit appearance), the active Red or Green lamp quickly blinks off for 0.2s (4 ticks) and resumes illumination as an advance warning; phases shorter than 12s automatically omit this blink.
- **Synchronized Green Flashing**: In the final 3 seconds of Green phase (3, 2, 1), the countdown digit flashes in exact synchronization with the green lamp if green flashing is enabled (or stays steadily lit if green flashing is disabled).
- **Dual Orientation & Two-Aspect Support**: Works seamlessly on both horizontal triple lights and vanilla TrafficCraft vertical lights. Automatically omitted on two-aspect signals (such as pedestrian lights without a yellow aspect).
- **Independent Control & Seamless Upgrades**: Each traffic light has its own independent countdown switch, accessible via the GUI. Enabled by default; existing worlds and blocks automatically default to enabled upon upgrading the mod.
- **Centered Full-Bright Rendering**: Rendered with Minecraft's built-in font precisely centered on the yellow bulb surface at Full Bright light level for optimal daytime and nighttime visibility.

### 4. Aftersans53228's Fabric Roads (`aft_fabroads`) Ecosystem Integration
- **T-Pillar Auto-Mounting**: Right-clicking a T-shaped pillar (`HorizontalTshapedPillar`, `VerticalTshapedPillar`, etc.) with a horizontal traffic light automatically snaps it to the stem end facing the player.
- **Full 8-Directional / 45° Diagonal Support**:
  - Supports horizontal traffic lights, directional pillars, and road signs (`SignNormal`).
  - Right-clicking with a Wrench rotates blocks in 45-degree steps with sound and chat feedback.
  - Placement angles automatically calculate 45° sectors based on player yaw.
- **45° Sign Adaptive Alignment & Shading**:
  - Road signs at 45° maintain their true circular disc proportions without stretching, and automatically shift along the stem axis to eliminate the diagonal gap with T-pillars.
  - Normal and shading optimization removes diagonal wedge shadows and pinwheel artifacts on 45° signs for clean, crisp visuals.
- **SmartPillar Diagonal Connections**:
  - Smart pillars automatically form 45° diagonal support arms towards adjacent 45° pillars and signs.
  - Correctly avoids connecting diagonal arms to cardinal (0°, 90°, 180°, 270°) signs.

---

## 📋 Dependencies

Running this mod requires:

- **Minecraft**: `1.20.1`
- **Fabric Loader**: `>= 0.16.10`
- **Fabric API**: `>= 0.92.6`
- **TrafficCraft**: `1.20.1-1.2.0-beta.3`
- **DragonLib**: `1.20.1-beta-3.0.19+`
- **Architectury API**
- **Forge Config API Port**
- **Aftersans53228's Fabric Roads (`aft_fabroads`)**: *(Optional, recommended for pillar snap-mounting and 45° diagonal features)*

---

## 🛠️ Building from Source

Build using Gradle:

```bash
# Windows
.\gradlew.bat build

# Linux / macOS
./gradlew build
```

The output jar will be located at: `build/libs/horizontal_traffic-1.1.1.jar`.

---

## 🌿 Branch Structure

- **`1.20.1-custom`**: Default release branch with all stable features and integration fixes.
- **`dev`**: Active development branch for new features and iterations.
- **`1.20.1-legacy`**: Archived historical branch (renamed from original `main`).

---

## 📄 License

This project is open-source under the [MIT License](LICENSE).
