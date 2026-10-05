# Horizontal Traffic (横向红绿灯)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-blue.svg)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-0.16.10+-lightgrey.svg)](https://fabricmc.net/)

**Horizontal Traffic** 是基于 Minecraft 1.20.1 Fabric 的 [TrafficCraft](https://github.com/MrJulsen/TrafficCraft) 拓展附属模组。

为游戏追加了现实道路中常见的**横向排列交通信号灯（横向红绿灯）**，并完美兼容 TrafficCraft 的红绿灯控制系统与连接机制。

---

## 🌟 主要特性 (Features)

1. **横向红绿灯方块 (Horizontal Traffic Light)**
   - 信号灯按照现实规范从左至右排列（红、黄、绿）。
   - 拥有完整、精准的方块碰撞箱（VoxelShape），完美适配 4 个水平朝向（东、南、西、北）与 3 种立柱贴附安装位置（顶部 TOP、居中 CENTER、底部 BOTTOM）。
   - 背部配有贯穿一整格的横向安装支架与横梁，外观自然美观。

2. **TrafficCraft 原生系统深度兼容**
   - **控制器联动**：支持使用交通灯连接器（`Traffic Light Linker`）对横向红绿灯右键进行信号控制器（`Traffic Light Controller`）的绑定与解绑。
   - **提示格式统一**：绑定提示文本与原版规范完全一致（`Linked to X, Y, Z in dimension ...`）。
   - **完整支持信号模式**：静态模式（STATIC）、独立时刻表模式（OWN_SCHEDULE）以及控制器远程控制模式（REMOTE）。

3. **优化体验：相位 ID 无需按回车自动保存**
   - 修复了原版修改相位 ID（Phase ID）时若直接按 ESC 退出则修改失效（变回 0）的体验问题。
   - 实现了实时双向数值同步与退出时深层递归自动提交，修改数字后无需按 Enter 键即可立即保存并同步至服务端。

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

---

## 🛠️ 构建与编译 (Building)

使用 Gradle 即可进行本地构建：

```bash
# Windows
.\gradlew.bat build

# Linux / macOS
./gradlew build
```

构建生成的 Mod Jar 文件位于：`build/libs/horizontal_traffic-1.0.0.jar`。

---

## 🌿 分支管理 (Branches)

- **`main`**: 稳定发布分支。
- **`dev`**: 开发分支，用于新功能开发、测试与迭代。

---

## 📄 开源协议 (License)

本项目采用 [MIT License](LICENSE) 协议开源。
