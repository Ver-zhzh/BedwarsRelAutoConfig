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
- ✅ **子文件夹结构**: 有组织的配置存储，无文件冲突
- ✅ **完整 SBA 支持**: 完全支持 BedwarsScoreBoardAddon 配置

---

## 🎮 支持的配置类型

### ✅ 完全支持（Per-Game）

| 类型 | 描述 | 多游戏支持 |
|------|------|-----------|
| **SBAconfig** | BedwarsScoreBoardAddon 主配置 | ✅ 是 |
| **teamshop** | BedwarsScoreBoardAddon 团队商店 | ✅ 是 |

### ⚠️ 部分支持（全局）

| 类型 | 描述 | 多游戏支持 |
|------|------|-----------|
| **config** | BedwarsRel 主配置 | ⚠️ 仅单游戏 |
| **shop** | BedwarsRel 商店配置 | ⚠️ 仅单游戏 |

> **注意**: 由于架构限制，BedwarsRel 配置是全局的。对于多游戏服务器，我们建议只使用 SBA 配置。

---

## 📋 环境要求

- **Minecraft 服务器**: Spigot/Paper 1.12.2 或更高版本
- **Java**: 8 或更高版本
- **必需依赖**:
  - BedwarsRel 1.3.6+
- **可选依赖**:
  - BedwarsScoreBoardAddon 2.13.1+（推荐）

---

## 📦 安装

1. **下载** 最新版本从 [GitHub Releases](https://github.com/YOUR_USERNAME/BedwarsRelAutoConfig/releases)
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

## 📚 命令列表

### 模板管理

| 命令 | 描述 |
|------|------|
| `/brac create <类型> <名称>` | 创建新的配置模板 |
| `/brac delete <类型> <名称>` | 删除配置模板 |
| `/brac list <类型>` | 列出某类型的所有模板 |
| `/brac copy <类型> <源> <目标>` | 复制模板 |

### 游戏配置

| 命令 | 描述 |
|------|------|
| `/brac enable <游戏> <类型> <模板>` | 为游戏应用模板 |
| `/brac disable <游戏> <类型>` | 移除游戏的自定义配置 |
| `/brac info <游戏>` | 显示游戏当前的配置 |

### 随机配置

| 命令 | 描述 |
|------|------|
| `/brac RandomEnable <游戏> <类型>` | 启用随机配置选择 |
| `/brac RandomDisable <游戏> <类型>` | 禁用随机配置选择 |
| `/brac RandomOut <游戏> <类型> <模板>` | 从随机中排除模板 |
| `/brac RandomIn <游戏> <类型> <模板>` | 在随机中包含模板 |
| `/brac RandomList <游戏> <类型>` | 显示随机排除列表 |

### 系统

| 命令 | 描述 |
|------|------|
| `/brac reload` | 重载插件配置 |
| `/brac help` | 显示帮助信息 |

---

## 🎯 使用示例

### 示例1: 多游戏服务器不同模式

```bash
# 创建模板
/brac create SBAconfig normal
/brac create SBAconfig hardcore
/brac create SBAconfig speed

# 应用到不同游戏
/brac enable bw4v4_1 SBAconfig normal
/brac enable bw4v4_2 SBAconfig hardcore
/brac enable bw4v4_3 SBAconfig speed
```

### 示例2: 随机配置

```bash
# 创建多个模板
/brac create SBAconfig mode_a
/brac create SBAconfig mode_b
/brac create SBAconfig mode_c

# 启用随机选择
/brac RandomEnable bw4v4_1 SBAconfig

# 排除测试模板
/brac RandomOut bw4v4_1 SBAconfig mode_c
```

### 示例3: 同步多类型随机

```bash
# 为两种类型启用随机
/brac RandomEnable bw4v4_1 SBAconfig
/brac RandomEnable bw4v4_1 teamshop

# 两者将使用相同的模板名称
# 如果 SBAconfig 选择了 "pvp_mode"，teamshop 也会使用 "pvp_mode"
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

## 🔧 技术细节

### 工作原理

1. **模板存储**: 配置存储在子文件夹结构中
2. **运行时注入**: 使用 Java 反射注入配置
3. **Per-Game 隔离**: SBA 配置使用 ConcurrentHashMap 按游戏存储
4. **事件驱动**: 配置在游戏开始/结束事件时应用

### 限制

- BedwarsRel 的主配置和商店配置是全局的（架构限制）
- 对于多游戏服务器，只有 SBA 配置支持真正的 per-game 隔离
- 随机配置需要至少有一个可用的模板

---

## 🐛 故障排除

### 配置未应用

**问题**: 配置更改未生效

**解决方案**:
1. 检查模板是否存在: `/brac list <类型>`
2. 验证游戏名称正确: `/brac info <游戏>`
3. 检查服务器日志错误
4. 尝试重载: `/brac reload`

### 多游戏冲突

**问题**: 多个游戏共享相同配置

**解决方案**:
- 只使用 SBA 配置（SBAconfig 和 teamshop）
- 避免在多游戏环境中使用 BedwarsRel config 和 shop

---

## 📄 许可证

本项目为专有软件。保留所有权利。

- ✅ 您可以在服务器上使用此插件
- ✅ 您可以报告错误和请求功能
- ❌ 您不得重新分发此插件
- ❌ 您不得反编译或修改此插件
- ❌ 您不得声称此插件为您的作品

---

## 🤝 支持

- **问题反馈**: [GitHub Issues](https://github.com/YOUR_USERNAME/BedwarsRelAutoConfig/issues)
- **讨论**: [GitHub Discussions](https://github.com/YOUR_USERNAME/BedwarsRelAutoConfig/discussions)

---

## 📝 更新日志

查看 [CHANGELOG.md](CHANGELOG.md) 了解版本历史。

---

## 👨‍💻 作者

**Ver_zhzh**

---

## 🙏 致谢

- BedwarsRel 团队提供的优秀基础插件
- BedwarsScoreBoardAddon 团队提供的计分板插件
- Spigot 社区的支持和反馈

---

**用 ❤️ 为 Minecraft 社区制作**

