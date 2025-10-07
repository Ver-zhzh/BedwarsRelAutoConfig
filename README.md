# BedwarsRelAutoConfig (BRAC)

[中文文档](README_CN.md) | English

**Version:** 1.0.0
**Author:** Ver_zhzh

---

## 📖 Overview

**BedwarsRelAutoConfig** is a powerful Spigot plugin that provides multi-configuration management for BedwarsRel and BedwarsScoreBoardAddon plugins. It allows different game instances to use different configuration files, enabling diverse gameplay experiences on the same server.

### Key Features

- ✅ **Per-Game Configuration**: Each game can use different configurations
- ✅ **Random Configuration**: Automatically select random configurations for each game
- ✅ **Template System**: Create and manage configuration templates easily
- ✅ **Hot Reload**: Apply configuration changes without server restart
- ✅ **Subfolder Structure**: Organized configuration storage with no file conflicts
- ✅ **Full SBA Support**: Complete support for BedwarsScoreBoardAddon configurations

---

## 🎮 Supported Configurations

### ✅ Fully Supported (Per-Game)

| Type | Description | Multi-Game Support |
|------|-------------|-------------------|
| **SBAconfig** | BedwarsScoreBoardAddon main config | ✅ Yes |
| **teamshop** | BedwarsScoreBoardAddon team shop | ✅ Yes |

### ⚠️ Partially Supported (Global)

| Type | Description | Multi-Game Support |
|------|-------------|-------------------|
| **config** | BedwarsRel main config | ⚠️ Single game only |
| **shop** | BedwarsRel shop config | ⚠️ Single game only |

> **Note**: BedwarsRel configurations are global due to architectural limitations. For multi-game servers, we recommend using only SBA configurations.

---

## 📋 Requirements

- **Minecraft Server**: Spigot/Paper 1.12.2 or higher
- **Java**: 8 or higher
- **Required Dependencies**:
  - BedwarsRel 1.3.6+
- **Optional Dependencies**:
  - BedwarsScoreBoardAddon 2.13.1+ (recommended)

## 📦 Installation

1. **Download** the latest release from [GitHub Releases](https://github.com/YOUR_USERNAME/BedwarsRelAutoConfig/releases)
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

## 🎯 Usage Examples

### Example 1: Multi-Game Server with Different Modes

```bash
# Create templates
/brac create SBAconfig normal
/brac create SBAconfig hardcore
/brac create SBAconfig speed

# Apply to different games
/brac enable bw4v4_1 SBAconfig normal
/brac enable bw4v4_2 SBAconfig hardcore
/brac enable bw4v4_3 SBAconfig speed
```

### Example 2: Random Configuration

```bash
# Create multiple templates
/brac create SBAconfig mode_a
/brac create SBAconfig mode_b
/brac create SBAconfig mode_c

# Enable random selection
/brac RandomEnable bw4v4_1 SBAconfig

# Exclude test template
/brac RandomOut bw4v4_1 SBAconfig mode_c
```

### Example 3: Synchronized Multi-Type Random

```bash
# Enable random for both types
/brac RandomEnable bw4v4_1 SBAconfig
/brac RandomEnable bw4v4_1 teamshop

# Both will use the same template name
# If SBAconfig selects "pvp_mode", teamshop will also use "pvp_mode"
```

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

## � Technical Details

### How It Works

1. **Template Storage**: Configurations are stored in subfolder structure
2. **Runtime Injection**: Configurations are injected using Java reflection
3. **Per-Game Isolation**: SBA configs are stored per-game using ConcurrentHashMap
4. **Event-Driven**: Configurations are applied on game start/end events

### Limitations

- BedwarsRel's main config and shop config are global (architectural limitation)
- For multi-game servers, only SBA configurations support true per-game isolation
- Random configuration requires at least one template to be available

---

## 🐛 Troubleshooting

### Configuration Not Applied

**Problem**: Configuration changes don't take effect

**Solution**:
1. Check if the template exists: `/brac list <type>`
2. Verify game name is correct: `/brac info <game>`
3. Check server logs for errors
4. Try reloading: `/brac reload`

### Multi-Game Conflicts

**Problem**: Multiple games share the same configuration

**Solution**:
- Use only SBA configurations (SBAconfig and teamshop)
- Avoid using BedwarsRel config and shop in multi-game environments

---

## 📄 License

This project is proprietary software. All rights reserved.

- ✅ You may use this plugin on your server
- ✅ You may report bugs and request features
- ❌ You may not redistribute this plugin
- ❌ You may not decompile or modify this plugin
- ❌ You may not claim this plugin as your own work

---

## 🤝 Support

- **Issues**: [GitHub Issues](https://github.com/YOUR_USERNAME/BedwarsRelAutoConfig/issues)
- **Discussions**: [GitHub Discussions](https://github.com/YOUR_USERNAME/BedwarsRelAutoConfig/discussions)

---

## 📝 Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history.

---

## 👨‍� Author

**Ver_zhzh**

---

## 🙏 Acknowledgments

- BedwarsRel team for the amazing base plugin
- BedwarsScoreBoardAddon team for the scoreboard addon
- Spigot community for support and feedback

---

**Made with ❤️ for the Minecraft community**

