# Horizontal Traffic (横向红绿灯)

[English](README_EN.md) | [简体中文](README.md)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-blue.svg)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-0.16.10+-lightgrey.svg)](https://fabricmc.net/)

**Horizontal Traffic** 是基于 Minecraft 1.20.1 Fabric 的 [TrafficCraft](https://github.com/MrJulsen/TrafficCraft) 拓展附属模组。

为游戏追加了现实道路中常见的**横向排列交通信号灯（横向红绿灯）**，并深度融合与优化了与 [Aftersans53228's Fabric Roads (aft_fabroads)](https://github.com/Aftersans53228/fabric_roads) 道路模组的立柱连接与 45° 斜角机制。

---

## 🌟 主要特性 (Features)

### 1. 多联横向红绿灯 (Horizontal Traffic Lights)
- **规格齐全**：提供单联（Single）、双联（Double）、三联（Tripple / 现实标准）三种横向交通信号灯方块。
- **真实排列与结构优化**：信号灯严格按照现实道路规范从左至右排列（红、黄、绿）；重新设计的 SupportPole 与 Bracket 结构缩短了支撑脖颈，去除了外侧悬空的侧耳薄片，紧凑贴合立柱。
- **全方位碰撞箱与预设高度**：拥有完整、精准的方块碰撞箱（VoxelShape），完美适配 4 个水平朝向（东、南、西、北）与 3 种立柱贴附安装位置（顶部 TOP、居中 CENTER、底部 BOTTOM）。
- **光源与渲染优化**：方块内置 3 级环境发光，灯泡使用全亮度渲染（Full Bright），夜间发光效果清晰自然。

### 2. TrafficCraft 原生系统深度兼容
- **控制器联动**：完整支持使用交通灯连接器（`Traffic Light Linker`）右键横向红绿灯进行信号控制器（`Traffic Light Controller`）的绑定与解绑。
- **全信号模式支持**：静态模式（STATIC）、独立时刻表模式（OWN_SCHEDULE）以及控制器远程控制模式（REMOTE）。
- **相位 ID 自动保存优化**：修复了原版修改相位 ID（Phase ID）时直接按 ESC 退出会导致修改失效变回 0 的问题，输入数值实时双向自动提交与同步，退出无需按 Enter 即可保存。
- **丰富图标与闪烁动画**：完整支持 TrafficCraft 各类数字倒计时、直行/左转/右转/斜向分流等转向箭头以及绿灯倒数闪烁逻辑。

### 3. Aftersans53228's Fabric Roads (aft_fabroads) 深度生态联动
- **T 型立柱挂载与自动吸附**：手持横向红绿灯右键 `aft_fabroads` 的 T 型立柱（如 `HorizontalTshapedPillar`、`VerticalTshapedPillar`），会自动吸附至分支末端放置，并自动朝向玩家。
- **全 8 向 / 45° 对角旋转支持**：
  - 涵盖横向红绿灯、各类横向/纵向/转角/T型立柱以及道路交通标牌（`SignNormal`，包括各类限速牌与禁行/指示牌）。
  - 支持手持扳手（Wrench）右键方块以 45 度步进旋转（共 8 个方向），附带方块更新、音效与操作提示。
  - 45 度放置时自动根据玩家视角计算朝向。
- **45 度标牌光照与连接自适应优化**：
  - 45 度对角标牌尺寸保持严谨圆盘/面板比例（不被拉伸），且平移自适应抵消对角距离差，与 45 度 T 型立柱末端严丝合缝、零间隙。
  - 优化法线与平滑光照，消除 45 度多面体标牌在对角线上的杂乱阴影与扇形暗块切线，保持标牌表面纯净清晰。
- **智能立柱（SmartPillar）对角自动连接**：
  - 智能立柱与 45 度斜置的立柱或标牌相邻时，会自动生成 45 度对角连接杆；
  - 当相邻标牌为正向（0°、90°、180°、270°）时，智能立柱智能识别并不误连对角连接杆。

---

## 📋 依赖需求 (Dependencies)

运行本模组需要以下前置：

- **Minecraft**: `1.20.1`
- **Fabric Loader**: `>= 0.16.10`
- **Fabric API**: `>= 0.92.6`
- **TrafficCraft**: `1.20.1-1.2.0-beta.3`
- **DragonLib**: `1.20.1-beta-3.0.19+`
- **Architectury API**
- **Forge Config API Port**
- **Aftersans53228's Fabric Roads (`aft_fabroads`)**: （可选联动，推荐配合使用以获得立柱吸附与 45° 旋转特性）

---

## 🛠️ 构建与编译 (Building)

使用 Gradle 即可进行本地构建：

```bash
# Windows
.\gradlew.bat build

# Linux / macOS
./gradlew build
```

构建生成的 Mod Jar 文件位于：`build/libs/horizontal_traffic-1.1.0.jar`。

---

## 🌿 分支管理 (Branches)

- **`1.20.1-custom`**: 默认稳定发布分支，包含所有稳定特性与联动修复。
- **`dev`**: 活跃开发分支，用于新特性开发与持续迭代。
- **`1.20.1-legacy`**: 历史归档分支（原 main 分支）。

---

## 📄 开源协议 (License)

本项目采用 [MIT License](LICENSE) 协议开源。
