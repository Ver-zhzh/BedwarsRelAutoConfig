# BedwarsRelAutoConfig (BRAC)

[English](README.md) | 中文文档

**版本:** 1.0.0  
**作者:** Ver_zhzh

---

## 📖 简介

**BedwarsRelAutoConfig** 是一个强大的 Spigot 插件，为 BedwarsRel 和 BedwarsScoreBoardAddon 插件提供多配置管理功能。它允许不同的游戏实例使用不同的配置文件，在同一服务器上实现多样化的游戏体验。

### 核心功能

- ✅ **Per-Game 配置**: 每个游戏可以使用不同的配置
- ✅ **随机配置**: 自动为每个游戏随机选择配置
- ✅ **模板系统**: 轻松创建和管理配置模板
- ✅ **热重载**: 无需重启服务器即可应用配置更改
- ✅ **完整 SBA 支持**: 完全支持 BedwarsScoreBoardAddon 配置

---

## 🎮 目前支持的配置类型

### ✅ 完全支持（Per-Game）

| 类型 | 描述 | 多游戏支持 |
|------|------|-----------|
| **SBAconfig** | BedwarsScoreBoardAddon 主配置 | ✅ 是 |
| **teamshop** | BedwarsScoreBoardAddon 团队商店 | ✅ 是 |

|[唯一缺点] 需要搭配BedwarsScoreBoardAddon-Adapt 适配版使用

| 类型 | 描述 | 多游戏支持 |
|------|------|-----------|
| **config** | BedwarsRel 主配置 | ⚠️ 推荐一端一图 |
| **shop** | BedwarsRel 商店配置 | 可一端多图 |

> **注意**: 由于架构限制，BedwarsRel 配置config是全局的。对于一端多图可能导致config被多游戏同时运行时共有，如果可能，未来我们会更新专门的BedwarsRel的适配版本，并且优化部分内容

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
