# BedwarsRelAutoConfig (BRAC)

[English](README.md) | 中文文档

**版本:** 1.2.5  
**作者:** Ver_zhzh

---

## ✨ v1.2.0 更新亮点

- **Legacy 模式增强**: 完美支持旧版 BedwarsRel (BungeeCord)，采用 "备份-覆盖-恢复" 机制，确保全局配置安全。
- **安全升级**: 插件卸载或服务器关闭时自动恢复所有已注入的配置，防止文件残留。
- **Bug 修复**: 修复了 EnderPearlChair、MagicMilk 等多个物品的逻辑问题。
- **Pre-game的Pre版本插件已经全部更新！**: 已支持BedwarsRel，BedwarsITemAddon

## 📖 简介

**BedwarsRelAutoConfig** 是一个强大的 Spigot 插件，为 BedwarsRel 和 BedwarsScoreBoardAddon 插件提供多配置管理功能。它允许不同的游戏实例使用不同的配置文件，在同一服务器上实现多样化的游戏体验。

### 核心功能

- ✅ **Per-Game 配置**: 每个游戏可以使用不同的配置
- ✅ **随机配置**: 自动为每个游戏随机选择配置
- ✅ **模板系统**: 轻松创建和管理配置模板
- ✅ **热重载**: 无需重启服务器即可应用配置更改
- ✅ **完整 SBA 支持**: 完全支持 BedwarsScoreBoardAddon 配置
- ✅ **Legacy 支持**: 完美支持旧版 BedwarsRel (BungeeCord 模式)，包含自动备份与恢复
- ✅ **安全机制**: 插件卸载时自动恢复配置，防止数据损坏
- ✅ **视觉优化**: 炫酷启动横幅与彩色日志支持

---

## 🎮 目前支持的配置类型

### ✅ 完全支持（Per-Game）

| 类型 | 描述 | 多游戏支持 |
|------|------|-----------|
| **SBAconfig** | BedwarsScoreBoardAddon 主配置 | ✅ 是 |
| **teamshop** | BedwarsScoreBoardAddon 团队商店 | ✅ 是 |
| **items** | BedwarsItemAddon 物品配置 | ✅ 是 |

### ✅ Legacy 支持（全局覆盖）

| 类型 | 描述 | 多游戏支持 |
|------|------|-----------|
| **config** | BedwarsRel 主配置 | ⚠️ 单游戏 (BungeeCord) |
| **shop** | BedwarsRel 商店配置 | ⚠️ 单游戏 (BungeeCord) |

> **注意**: 对于旧版 BedwarsRel，插件采用安全的 "备份 -> 覆盖 -> 重载 -> 恢复" 机制。配置会在首位玩家加入时注入，并在游戏结束或插件卸载时自动恢复。

---

## 📦 安装

1. **下载** 最新版本从 [GitHub Releases](https://github.com/Ver-zhzh/BedwarsRelAutoConfig/releases)
2. **放置** JAR 文件到服务器的 `plugins` 文件夹
3. **重启** 服务器
4. **配置** 使用下面的命令

---

## 🚀 快速开始

### 1. 创建配置模板

```bash
# 创建 PvP 模式模板
/brac create SBAconfig pvp_mode
/brac create teamshop pvp_mode

# 创建和平模式模板
/brac create SBAconfig peaceful_mode
/brac create teamshop peaceful_mode
```

### 2. 为游戏应用配置

```bash
# 为游戏1应用 PvP 模式
/brac enable bw4v4_1 SBAconfig pvp_mode
/brac enable bw4v4_1 teamshop pvp_mode

# 为游戏2应用和平模式
/brac enable bw4v4_2 SBAconfig peaceful_mode
/brac enable bw4v4_2 teamshop peaceful_mode
```

### 3. 启用随机配置（可选）

```bash
# 为游戏启用随机选择
/brac RandomEnable bw4v4_1 SBAconfig
/brac RandomEnable bw4v4_1 teamshop

# 从随机选择中排除特定模板
/brac RandomOut bw4v4_1 SBAconfig test_template
```

---
## 📁 目录结构

```
plugins/BedwarsRelAutoConfig/
├── config.yml              # 插件配置
├── messages.yml            # 消息配置
├── mappings.yml            # 游戏-模板映射
├── random.yml              # 随机排除规则
└── configs/                # 配置模板
    ├── template_name/
    │   ├── config/
    │   │   └── config.yml
    │   ├── shop/
    │   │   └── shop.yml
    │   ├── SBAconfig/
    │   │   └── config.yml
    │   └── teamshop/
    │       └── team_shop.yml
    └── another_template/
        └── ...
```

---

## ⚙️ 配置文件

### config.yml

```yaml
# 插件设置
settings:
  debug: false

# 路径配置
paths:
  configs: "configs"
  mappings: "mappings.yml"
  random: "random.yml"

# 默认模板来源
defaults:
  bedwarsrel-config: "plugins/BedwarsRel/config.yml"
  bedwarsrel-shop: "plugins/BedwarsRel/shop.yml"
  sba-config: "plugins/BedwarsScoreBoardAddon/config.yml"
  sba-teamshop: "plugins/BedwarsScoreBoardAddon/team_shop.yml"
```

---

## 👨‍💻 作者

**Ver_zhzh**

---
