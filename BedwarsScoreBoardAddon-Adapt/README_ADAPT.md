# BedwarsScoreBoardAddon-Adapt

## 简介

这是 **BedwarsScoreBoardAddon** 的适配版本，专门为 **BedwarsRelAutoConfig (BRAC)** 插件设计。

原版 SBA 使用全局静态配置，无法支持 per-game 配置。本适配版本添加了 `ConfigAPI`，允许 BRAC 为每个游戏注入独立的配置，而不影响其他正在进行的游戏。

## 与原版的区别

### 原版 SBA
- 使用全局静态配置
- 所有游戏共享同一套配置
- 无法实现 per-game 定制

### 适配版 SBA
- 保留原版所有功能
- 新增 `ConfigAPI` 接口
- 支持 per-game 配置注入
- 与 BRAC 完美集成

## 核心修改

### 1. plugin.yml
- 插件名称改为 `BedwarsScoreBoardAddon-Adapt`
- 添加 `BedwarsRelAutoConfig` 为软依赖

### 2. ConfigAPI.java (新增)
提供以下 API：

```java
// 为指定游戏加载自定义配置
ConfigAPI.loadGameConfig(String gameName, File configFile, File languageFile, File teamShopFile)

// 恢复默认配置
ConfigAPI.restoreDefaultConfig()

// 获取当前游戏名称
ConfigAPI.getCurrentGame()

// 检查是否加载了自定义配置
ConfigAPI.isCustomConfigLoaded()

// 创建临时配置文件
ConfigAPI.createTempConfigFile(String gameName, FileConfiguration config, String fileName)

// 清理临时配置文件
ConfigAPI.cleanupTempConfigs(String gameName)
```

### 3. Config.java
- 添加 `getLanguageConfig()` 方法，供 ConfigAPI 访问

## 工作原理

1. **游戏开始前**：
   - BRAC 调用 `ConfigAPI.loadGameConfig()`
   - ConfigAPI 使用反射注入自定义配置到 `Config` 类的静态字段
   - 调用 `Config.loadConfig()` 重新加载所有静态字段
   - SBA 使用新配置运行游戏

2. **游戏结束后**：
   - BRAC 调用 `ConfigAPI.restoreDefaultConfig()`
   - ConfigAPI 恢复原始配置
   - 调用 `Config.loadConfig()` 重新加载
   - SBA 恢复默认配置

## 使用示例

### BRAC 中的使用

```java
// 在 SBAInjector.java 中

public boolean injectMainConfig(Game game, FileConfiguration config) {
    try {
        // 创建临时配置文件
        File tempConfigFile = ConfigAPI.createTempConfigFile(
            game.getName(), 
            config, 
            "config.yml"
        );
        
        // 加载游戏配置
        boolean success = ConfigAPI.loadGameConfig(
            game.getName(),
            tempConfigFile,
            null,  // language file (optional)
            null   // team_shop file (optional)
        );
        
        if (success) {
            plugin.getLogger().info("Successfully injected SBA config for game: " + game.getName());
        }
        
        return success;
        
    } catch (Exception e) {
        plugin.getLogger().log(Level.SEVERE, "Failed to inject SBA config", e);
        return false;
    }
}

public void restoreMainConfig(Game game) {
    try {
        // 恢复默认配置
        ConfigAPI.restoreDefaultConfig();
        
        // 清理临时文件
        ConfigAPI.cleanupTempConfigs(game.getName());
        
        plugin.getLogger().info("Restored SBA config for game: " + game.getName());
        
    } catch (Exception e) {
        plugin.getLogger().log(Level.SEVERE, "Failed to restore SBA config", e);
    }
}
```

## 兼容性

- **Minecraft**: 1.8.x - 1.12.x
- **BedwarsRel**: 1.3.6
- **BedwarsRelAutoConfig**: 1.0+
- **ProtocolLib**: 必需
- **Citizens, PlaceholderAPI, ServerJoiner, WorldEdit**: 可选

## 安装

1. 确保已安装 **BedwarsRel 1.3.6** 和 **ProtocolLib**
2. 安装 **BedwarsRelAutoConfig**
3. 将 `BedwarsScoreBoardAddon-Adapt.jar` 放入 `plugins` 文件夹
4. 重启服务器

**注意**：不要同时安装原版 SBA 和适配版 SBA！

## 构建

```bash
# 进入适配版目录
cd BedwarsScoreBoardAddon-Adapt/BedwarsScoreBoardAddon-master

# 使用 Maven 构建
mvn clean package

# 生成的 JAR 文件位于 target/ 目录
```

## 技术细节

### 反射注入原理

ConfigAPI 使用 Java 反射修改 `Config` 类的私有静态字段：

```java
Field field = Config.class.getDeclaredField("file_config");
field.setAccessible(true);
field.set(null, customConfig);  // null 表示静态字段
```

### 配置备份机制

首次调用 `loadGameConfig()` 时，ConfigAPI 会自动备份原始配置：

```java
private static FileConfiguration originalConfig = null;

if (originalConfig == null) {
    backupOriginalConfigs();
}
```

### 线程安全

ConfigAPI 的所有方法都是静态的，但不是线程安全的。BRAC 应该确保：
- 同一时间只有一个游戏在加载/恢复配置
- 在游戏开始前完成配置注入
- 在游戏结束后完成配置恢复

## 常见问题

### Q: 为什么不直接修改原版 SBA？
A: 原版 SBA 是开源项目，我们不想破坏其原有设计。适配版作为独立插件，用户可以选择使用原版或适配版。

### Q: 适配版会影响性能吗？
A: 不会。ConfigAPI 只在游戏开始/结束时调用，对游戏运行期间没有影响。

### Q: 可以同时安装原版和适配版吗？
A: 不可以！两个插件会冲突。请只安装其中一个。

### Q: 如果不使用 BRAC，适配版能正常工作吗？
A: 可以！如果没有 BRAC，适配版会像原版一样工作，使用默认配置。

## 贡献

本适配版基于 [BedwarsScoreBoardAddon](https://github.com/TheRamU/BedwarsScoreBoardAddon) 开发。

感谢原作者 **Ram** 的优秀工作！

## 许可证

与原版 SBA 相同的许可证。

## 联系

如有问题或建议，请在 BRAC 项目中提 Issue。

