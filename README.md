# BedwarsRelAutoConfig (BRAC)

[中文文档](README_CN.md) | English

**Version:** 1.2.0  
**Author:** Ver_zhzh

---

## 📖 Overview

**BedwarsRelAutoConfig** is a powerful Spigot plugin that provides multi-configuration management for BedwarsRel and BedwarsScoreBoardAddon plugins. It allows different game instances to use different configuration files, enabling diverse gameplay experiences on the same server.

### Key Features

- ✅ **Per-Game Configuration**: Each game can use different configurations
- ✅ **Random Configuration**: Automatically select random configurations for each game
- ✅ **Template System**: Create and manage configuration templates easily
- ✅ **Hot Reload**: Apply configuration changes without server restart
- ✅ **Full SBA Support**: Complete support for BedwarsScoreBoardAddon configurations

---

## 🎮 Currently Supported Configuration Types

### ✅ Fully Supported (Per-Game)

| Type | Description | Multi-Game Support |
|------|-------------|-------------------|
| **SBAconfig** | BedwarsScoreBoardAddon main config | ✅ Yes |
| **teamshop** | BedwarsScoreBoardAddon team shop | ✅ Yes |

| [Only Limitation] Requires BedwarsScoreBoardAddon-Adapt adapted version

| Type | Description | Multi-Game Support |
|------|-------------|-------------------|
| **config** | BedwarsRel main config | ⚠️ Recommended for single-server single-game |
| **shop** | BedwarsRel shop config | Can be used for single-server multi-game |

> **Note**: Due to architectural limitations, BedwarsRel's config is global. For single-server multi-game setups, config may be shared when multiple games run simultaneously. If possible, we will update a dedicated BedwarsRel adapted version in the future and optimize some content.

---

## 📦 Installation

1. **Download** the latest release from [GitHub Releases](https://github.com/Ver-zhzh/BedwarsRelAutoConfig/releases)
2. **Place** the JAR file in your server's `plugins` folder
3. **Restart** your server
4. **Configure** using the commands below

---

## 🚀 Quick Start

### 1. Create Configuration Templates

```bash
# Create a PvP mode template
/brac create SBAconfig pvp_mode
/brac create teamshop pvp_mode

# Create a peaceful mode template
/brac create SBAconfig peaceful_mode
/brac create teamshop peaceful_mode
```

### 2. Apply Configurations to Games

```bash
# Apply PvP mode to game1
/brac enable bw4v4_1 SBAconfig pvp_mode
/brac enable bw4v4_1 teamshop pvp_mode

# Apply peaceful mode to game2
/brac enable bw4v4_2 SBAconfig peaceful_mode
/brac enable bw4v4_2 teamshop peaceful_mode
```

### 3. Enable Random Configuration (Optional)

```bash
# Enable random selection for a game
/brac RandomEnable bw4v4_1 SBAconfig
/brac RandomEnable bw4v4_1 teamshop

# Exclude specific templates from random selection
/brac RandomOut bw4v4_1 SBAconfig test_template
```

---

## 📚 Commands

### Template Management

| Command | Description |
|---------|-------------|
| `/brac create <type> <name>` | Create a new configuration template |
| `/brac delete <type> <name>` | Delete a configuration template |
| `/brac list <type>` | List all templates of a type |
| `/brac copy <type> <source> <target>` | Copy a template |

### Game Configuration

| Command | Description |
|---------|-------------|
| `/brac enable <game> <type> <template>` | Apply a template to a game |
| `/brac disable <game> <type>` | Remove custom config from a game |
| `/brac info <game>` | Show game's current configurations |

### Random Configuration

| Command | Description |
|---------|-------------|
| `/brac RandomEnable <game> <type>` | Enable random config selection |
| `/brac RandomDisable <game> <type>` | Disable random config selection |
| `/brac RandomOut <game> <type> <template>` | Exclude template from random |
| `/brac RandomIn <game> <type> <template>` | Include template in random |
| `/brac RandomList <game> <type>` | Show random exclusion list |

### System

| Command | Description |
|---------|-------------|
| `/brac reload` | Reload plugin configuration |
| `/brac help` | Show help message |

---

## 📁 Directory Structure

```
plugins/BedwarsRelAutoConfig/
├── config.yml              # Plugin configuration
├── messages.yml            # Message configuration
├── mappings.yml            # Game-template mappings
├── random.yml              # Random exclusion rules
└── configs/                # Configuration templates
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

## ⚙️ Configuration

### config.yml

```yaml
# Plugin settings
settings:
  debug: false

# Path configuration
paths:
  configs: "configs"
  mappings: "mappings.yml"
  random: "random.yml"

# Default template sources
defaults:
  bedwarsrel-config: "plugins/BedwarsRel/config.yml"
  bedwarsrel-shop: "plugins/BedwarsRel/shop.yml"
  sba-config: "plugins/BedwarsScoreBoardAddon/config.yml"
  sba-teamshop: "plugins/BedwarsScoreBoardAddon/team_shop.yml"
```

---

## 👨‍💻 Author

**Ver_zhzh**

---

